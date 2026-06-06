package com.senninseyi.overlay_sdk;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.senninseyi.overlay_sdk.bubble.BubbleAnimator;
import com.senninseyi.overlay_sdk.bubble.BubbleController;
import com.senninseyi.overlay_sdk.bubble.BubblePhysics;
import com.senninseyi.overlay_sdk.callbacks.OverlayListener;
import com.senninseyi.overlay_sdk.config.BubbleStyle;
import com.senninseyi.overlay_sdk.config.OverlayOptions;
import com.senninseyi.overlay_sdk.panel.OverlayPanelController;
import com.senninseyi.overlay_sdk.permission.OverlayPermissionManager;
import com.senninseyi.overlay_sdk.storage.OverlayStateStore;
import com.senninseyi.overlay_sdk.utils.ScreenUtils;
import com.senninseyi.overlay_sdk.views.BubbleView;

import java.util.HashMap;
import java.util.Map;

public class OverlayManager {

    private static final String TAG = "OverlayManager";
    private static volatile OverlayManager instance;

    public static OverlayManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OverlayManager.class) {
                if (instance == null) {
                    instance = new OverlayManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private final Context appContext;
    private final WindowManager windowManager;
    private final OverlayStateStore stateStore;
    private final Map<String, BubbleRecord> bubbles = new HashMap<>();

    private OverlayListener listener = new OverlayListener() {
    };

    private View trashZoneView;
    private WindowManager.LayoutParams trashZoneParams;
    private final Rect trashRect = new Rect();
    private String activeDragBubbleId;
    private boolean activeDragHoveringTrash;

    private OverlayManager(Context context) {
        this.appContext = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.stateStore = new OverlayStateStore(context);
    }

    public synchronized void setListener(@Nullable OverlayListener listener) {
        this.listener = listener != null ? listener : new OverlayListener() {
        };
    }

    public synchronized void onServiceCreated() {
        for (BubbleRecord record : bubbles.values()) {
            saveState(record);
        }
    }

    public synchronized void onServiceDestroyed() {
        hideTrashZone();
    }

    public synchronized boolean showBubble(String bubbleId, OverlayOptions options) {
        if (!OverlayPermissionManager.hasPermission(appContext)) {
            listener.onPermissionRequired();
            return false;
        }

        String id = normalizeId(bubbleId);
        BubbleRecord existing = bubbles.get(id);
        if (existing != null) {
            existing.bubbleView.setVisibility(View.VISIBLE);
            if (options.getIconResId() != 0) {
                existing.bubbleView.setIconRes(options.getIconResId());
            }
            saveState(existing);
            return true;
        }

        BubbleView bubbleView = new BubbleView(appContext);
        bubbleView.applyStyle(options.getBubbleStyle());
        bubbleView.setIconRes(options.getIconResId());

        int bubbleSize = bubbleView.getBubbleSizePx();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                bubbleSize,
                bubbleSize,
                getOverlayType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;

        OverlayStateStore.BubbleState restored = stateStore.loadState(id);
        if (restored != null) {
            params.x = restored.x;
            params.y = restored.y;
        } else {
            params.x = options.getInitialX() != null ? options.getInitialX() : options.getLeftMargin();
            params.y = options.getInitialY() != null ? options.getInitialY() : options.getTopMargin();
        }

        Rect bounds = movementBounds(options, bubbleSize, bubbleSize);
        params.x = ScreenUtils.clamp(params.x, bounds.left, bounds.right);
        params.y = ScreenUtils.clamp(params.y, bounds.top, bounds.bottom);

        try {
            windowManager.addView(bubbleView, params);
        } catch (Exception exception) {
            listener.onError(id, "Unable to add overlay view: " + exception.getMessage());
            Log.e(TAG, "Failed to add bubble", exception);
            return false;
        }

        BubbleAnimator animator = new BubbleAnimator(windowManager, bubbleView, params);
        BubblePhysics physics = new BubblePhysics();
        OverlayPanelController panelController = new OverlayPanelController(appContext, windowManager);

        BubbleRecord record = new BubbleRecord(id, options, bubbleView, params, animator, physics, panelController);
        BubbleController controller = new BubbleController(
                bubbleView,
                params,
                windowManager,
                physics,
                animator,
                options,
                new BubbleController.Callback() {
                    @Override
                    public void onBubbleClick() {
                        listener.onBubbleClick(id);
                        if (record.panelController.isExpanded()) {
                            record.panelController.hidePanel();
                            listener.onBubbleCollapsed(id);
                        } else {
                            record.panelController.showDefaultPanel(record.params, record.bubbleView.getWidth());
                            listener.onBubbleExpanded(id);
                        }
                        saveState(record);
                    }

                    @Override
                    public void onBubbleDragStart() {
                        activeDragBubbleId = id;
                        activeDragHoveringTrash = false;
                        showTrashZone(record.options.getBubbleStyle());
                        listener.onBubbleDragStart(id);
                    }

                    @Override
                    public void onBubbleMove(int x, int y, int width, int height) {
                        maybeMagnetizeToTrash(record, width, height);
                        record.panelController.updateAnchorPosition(record.params, width);
                    }

                    @Override
                    public boolean onBubbleDragEnd(int x, int y, int width, int height) {
                        listener.onBubbleDragEnd(id);
                        boolean removed = isInsideTrash(record.params, width, height, record.options.getBubbleStyle());
                        hideTrashZone();
                        activeDragBubbleId = null;
                        activeDragHoveringTrash = false;

                        if (removed) {
                            removeBubble(id);
                            listener.onBubbleRemoved(id);
                            return true;
                        }
                        saveState(record);
                        return false;
                    }

                    @Override
                    public void onReleased(int x, int y) {
                        saveState(record);
                    }
                }
        );

        record.controller = controller;
        controller.attach();
        bubbles.put(id, record);

        if (restored != null && !restored.visible) {
            bubbleView.setVisibility(View.GONE);
        }

        if (restored != null && restored.expanded) {
            panelController.showDefaultPanel(params, bubbleView.getWidth());
            listener.onBubbleExpanded(id);
        }

        saveState(record);
        return true;
    }

    public synchronized void hideBubble(String bubbleId) {
        BubbleRecord record = bubbles.get(normalizeId(bubbleId));
        if (record == null) {
            return;
        }
        record.bubbleView.setVisibility(View.GONE);
        record.panelController.hidePanel();
        saveState(record);
    }

    public synchronized void removeBubble(String bubbleId) {
        String id = normalizeId(bubbleId);
        BubbleRecord record = bubbles.remove(id);
        if (record == null) {
            return;
        }

        record.controller.detach();
        record.panelController.release();

        if (record.bubbleView.isAttachedToWindow()) {
            try {
                windowManager.removeView(record.bubbleView);
            } catch (IllegalArgumentException ignored) {
            }
        }

        stateStore.removeState(id);
    }

    public synchronized void expandBubble(String bubbleId) {
        BubbleRecord record = bubbles.get(normalizeId(bubbleId));
        if (record == null) {
            return;
        }
        record.panelController.showDefaultPanel(record.params, record.bubbleView.getWidth());
        listener.onBubbleExpanded(record.id);
        saveState(record);
    }

    public synchronized void collapseBubble(String bubbleId) {
        BubbleRecord record = bubbles.get(normalizeId(bubbleId));
        if (record == null) {
            return;
        }
        record.panelController.hidePanel();
        listener.onBubbleCollapsed(record.id);
        saveState(record);
    }

    public synchronized void showPanel(String bubbleId, View contentView) {
        BubbleRecord record = bubbles.get(normalizeId(bubbleId));
        if (record == null) {
            return;
        }
        record.panelController.showPanel(record.params, record.bubbleView.getWidth(), contentView);
        listener.onBubbleExpanded(record.id);
        saveState(record);
    }

    public synchronized void hidePanel(String bubbleId) {
        BubbleRecord record = bubbles.get(normalizeId(bubbleId));
        if (record == null) {
            return;
        }
        record.panelController.hidePanel();
        listener.onBubbleCollapsed(record.id);
        saveState(record);
    }

    public synchronized void removeAll() {
        for (String bubbleId : new HashMap<>(bubbles).keySet()) {
            removeBubble(bubbleId);
        }
        hideTrashZone();
    }

    private Rect movementBounds(OverlayOptions options, int bubbleWidth, int bubbleHeight) {
        int screenWidth = ScreenUtils.getScreenWidth(windowManager);
        int screenHeight = ScreenUtils.getScreenHeight(windowManager);

        int minX = options.isSafeZone() ? options.getLeftMargin() : 0;
        int maxX = screenWidth - bubbleWidth - (options.isSafeZone() ? options.getRightMargin() : 0);

        int minY = options.isSafeZone() ? options.getTopMargin() : 0;
        int maxY = screenHeight - bubbleHeight - (options.isSafeZone() ? options.getBottomMargin() : 0);

        return new Rect(
                minX,
                minY,
                Math.max(minX, maxX),
                Math.max(minY, maxY)
        );
    }

    private void saveState(BubbleRecord record) {
        OverlayStateStore.BubbleState state = new OverlayStateStore.BubbleState();
        state.x = record.params.x;
        state.y = record.params.y;

        int centerX = state.x + (record.bubbleView.getWidth() / 2);
        int screenHalf = ScreenUtils.getScreenWidth(windowManager) / 2;
        state.lastEdge = centerX < screenHalf ? "left" : "right";

        state.visible = record.bubbleView.getVisibility() == View.VISIBLE;
        state.expanded = record.panelController.isExpanded();
        stateStore.saveState(record.id, state);
    }

    private void showTrashZone(BubbleStyle style) {
        ensureTrashZone(style);

        if (!trashZoneView.isAttachedToWindow()) {
            windowManager.addView(trashZoneView, trashZoneParams);
        }

        updateTrashRect(style);
        trashZoneView.setAlpha(0f);
        trashZoneView.animate().alpha(1f).setDuration(120L).start();
    }

    private void hideTrashZone() {
        if (trashZoneView == null || !trashZoneView.isAttachedToWindow()) {
            return;
        }

        try {
            windowManager.removeView(trashZoneView);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void maybeMagnetizeToTrash(BubbleRecord record, int bubbleWidth, int bubbleHeight) {
        if (trashZoneView == null || !trashZoneView.isAttachedToWindow()) {
            return;
        }

        BubbleStyle style = record.options.getBubbleStyle();
        updateTrashRect(style);

        int bubbleCenterX = record.params.x + (bubbleWidth / 2);
        int bubbleCenterY = record.params.y + (bubbleHeight / 2);

        int trashCenterX = trashRect.centerX();
        int trashCenterY = trashRect.centerY();

        int magnetRadius = ScreenUtils.dpToPx(appContext, style.getRemoveMagnetRadiusDp());
        double distance = Math.hypot(trashCenterX - bubbleCenterX, trashCenterY - bubbleCenterY);

        boolean shouldHover = distance <= magnetRadius;
        if (shouldHover != activeDragHoveringTrash) {
            activeDragHoveringTrash = shouldHover;
            trashZoneView.animate()
                    .scaleX(shouldHover ? 1.1f : 1f)
                    .scaleY(shouldHover ? 1.1f : 1f)
                    .alpha(shouldHover ? 1f : 0.9f)
                    .setDuration(110L)
                    .start();
        }

        if (shouldHover && activeDragBubbleId != null && activeDragBubbleId.equals(record.id)) {
            record.params.x += (int) ((trashCenterX - bubbleCenterX) * 0.16f);
            record.params.y += (int) ((trashCenterY - bubbleCenterY) * 0.16f);

            try {
                windowManager.updateViewLayout(record.bubbleView, record.params);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private boolean isInsideTrash(WindowManager.LayoutParams bubbleParams, int bubbleWidth, int bubbleHeight, BubbleStyle style) {
        if (trashZoneView == null) {
            return false;
        }

        updateTrashRect(style);

        Rect bubbleRect = new Rect(
                bubbleParams.x,
                bubbleParams.y,
                bubbleParams.x + bubbleWidth,
                bubbleParams.y + bubbleHeight
        );

        Rect expandedTrashRect = new Rect(trashRect);
        int margin = ScreenUtils.dpToPx(appContext, 12);
        expandedTrashRect.inset(-margin, -margin);
        return Rect.intersects(bubbleRect, expandedTrashRect);
    }

    private void ensureTrashZone(BubbleStyle style) {
        int width = ScreenUtils.dpToPx(appContext, style.getRemoveZoneWidthDp());
        int height = ScreenUtils.dpToPx(appContext, style.getRemoveZoneHeightDp());

        if (trashZoneView == null) {
            TextView textView = new TextView(appContext);
            textView.setText("Remove");
            textView.setTextColor(0xFFFFFFFF);
            textView.setGravity(Gravity.CENTER);

            GradientDrawable background = new GradientDrawable();
            background.setColor(0xCCDB3A34);
            background.setCornerRadius(ScreenUtils.dpToPx(appContext, 28));
            textView.setBackground(background);
            trashZoneView = textView;
        }

        if (trashZoneParams == null) {
            trashZoneParams = new WindowManager.LayoutParams(
                    width,
                    height,
                    getOverlayType(),
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT
            );
            trashZoneParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            trashZoneParams.y = ScreenUtils.dpToPx(appContext, 32);
        } else {
            trashZoneParams.width = width;
            trashZoneParams.height = height;
        }
    }

    private void updateTrashRect(BubbleStyle style) {
        int screenWidth = ScreenUtils.getScreenWidth(windowManager);
        int screenHeight = ScreenUtils.getScreenHeight(windowManager);

        int width = ScreenUtils.dpToPx(appContext, style.getRemoveZoneWidthDp());
        int height = ScreenUtils.dpToPx(appContext, style.getRemoveZoneHeightDp());
        int bottomMargin = ScreenUtils.dpToPx(appContext, 32);

        int left = (screenWidth - width) / 2;
        int top = screenHeight - bottomMargin - height;
        trashRect.set(left, top, left + width, top + height);
    }

    @SuppressWarnings("deprecation")
    private int getOverlayType() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
    }

    private String normalizeId(String bubbleId) {
        if (bubbleId == null || bubbleId.trim().isEmpty()) {
            return OverlaySDK.DEFAULT_BUBBLE_ID;
        }
        return bubbleId;
    }

    private static class BubbleRecord {
        private final String id;
        private final OverlayOptions options;
        private final BubbleView bubbleView;
        private final WindowManager.LayoutParams params;
        private final BubbleAnimator animator;
        private final BubblePhysics physics;
        private final OverlayPanelController panelController;
        private BubbleController controller;

        private BubbleRecord(
                String id,
                OverlayOptions options,
                BubbleView bubbleView,
                WindowManager.LayoutParams params,
                BubbleAnimator animator,
                BubblePhysics physics,
                OverlayPanelController panelController
        ) {
            this.id = id;
            this.options = options;
            this.bubbleView = bubbleView;
            this.params = params;
            this.animator = animator;
            this.physics = physics;
            this.panelController = panelController;
        }
    }
}
