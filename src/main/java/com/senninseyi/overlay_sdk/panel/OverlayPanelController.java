package com.senninseyi.overlay_sdk.panel;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.senninseyi.overlay_sdk.utils.ScreenUtils;

public class OverlayPanelController {

    private final Context context;
    private final WindowManager windowManager;

    private OverlayPanel panel;
    private WindowManager.LayoutParams panelParams;

    private final int panelWidth;
    private final int panelHeight;
    private final int topSpacing;

    private boolean expanded;

    public OverlayPanelController(Context context, WindowManager windowManager) {
        this.context = context;
        this.windowManager = windowManager;
        this.panelWidth = ScreenUtils.dpToPx(context, 280);
        this.panelHeight = ScreenUtils.dpToPx(context, 360);
        this.topSpacing = ScreenUtils.dpToPx(context, 12);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void showDefaultPanel(WindowManager.LayoutParams bubbleParams, int bubbleWidth) {
        TextView textView = new TextView(context);
        textView.setText("Overlay panel");
        textView.setTextSize(16f);
        int padding = ScreenUtils.dpToPx(context, 16);
        textView.setPadding(padding, padding, padding, padding);
        showPanel(bubbleParams, bubbleWidth, textView);
    }

    public void showPanel(WindowManager.LayoutParams bubbleParams, int bubbleWidth, View contentView) {
        ensurePanel();
        panel.setPanelContent(detachFromParent(contentView));
        updateAnchorPosition(bubbleParams, bubbleWidth);

        if (!panel.isAttachedToWindow()) {
            windowManager.addView(panel, panelParams);
        } else {
            windowManager.updateViewLayout(panel, panelParams);
        }

        panel.setAlpha(0f);
        panel.setScaleX(0.92f);
        panel.setScaleY(0.92f);
        panel.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start();
        expanded = true;
    }

    public void hidePanel() {
        if (panel == null || !panel.isAttachedToWindow()) {
            expanded = false;
            return;
        }

        try {
            windowManager.removeView(panel);
        } catch (IllegalArgumentException ignored) {
        }
        expanded = false;
    }

    public void updateAnchorPosition(WindowManager.LayoutParams bubbleParams, int bubbleWidth) {
        if (panelParams == null) {
            return;
        }

        int screenWidth = ScreenUtils.getScreenWidth(windowManager);
        int screenHeight = ScreenUtils.getScreenHeight(windowManager);

        int x = bubbleParams.x - ((panelWidth - bubbleWidth) / 2);
        int y = bubbleParams.y - panelHeight - topSpacing;

        panelParams.x = ScreenUtils.clamp(x, 0, Math.max(0, screenWidth - panelWidth));
        panelParams.y = ScreenUtils.clamp(y, 0, Math.max(0, screenHeight - panelHeight));

        if (panel != null && panel.isAttachedToWindow()) {
            windowManager.updateViewLayout(panel, panelParams);
        }
    }

    public void release() {
        hidePanel();
        panel = null;
        panelParams = null;
    }

    private void ensurePanel() {
        if (panel != null && panelParams != null) {
            return;
        }

        panel = new OverlayPanel(context);
        panel.setOnClickListener(v -> hidePanel());

        panelParams = new WindowManager.LayoutParams(
                panelWidth,
                panelHeight,
                getOverlayType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        panelParams.gravity = Gravity.TOP | Gravity.START;
    }

    @SuppressWarnings("deprecation")
    private int getOverlayType() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
    }

    private View detachFromParent(View view) {
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }
}
