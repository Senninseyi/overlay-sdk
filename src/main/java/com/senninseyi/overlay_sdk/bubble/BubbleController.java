package com.senninseyi.overlay_sdk.bubble;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.senninseyi.overlay_sdk.config.OverlayOptions;
import com.senninseyi.overlay_sdk.utils.ScreenUtils;

public class BubbleController {

    private final View bubbleView;
    private final WindowManager.LayoutParams params;
    private final WindowManager windowManager;
    private final BubblePhysics physics;
    private final BubbleAnimator animator;
    private final OverlayOptions options;
    private final Callback callback;

    private final int touchSlop;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private boolean dragging;
    private long downTime;

    public BubbleController(
            View bubbleView,
            WindowManager.LayoutParams params,
            WindowManager windowManager,
            BubblePhysics physics,
            BubbleAnimator animator,
            OverlayOptions options,
            Callback callback
    ) {
        this.bubbleView = bubbleView;
        this.params = params;
        this.windowManager = windowManager;
        this.physics = physics;
        this.animator = animator;
        this.options = options;
        this.callback = callback;
        this.touchSlop = ViewConfiguration.get(bubbleView.getContext()).getScaledTouchSlop();
    }

    public void attach() {
        bubbleView.setOnTouchListener(this::handleTouch);
    }

    public void detach() {
        animator.cancelAll();
        bubbleView.setOnTouchListener(null);
    }

    private boolean handleTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animator.cancelAll();
                dragging = false;
                downTime = System.currentTimeMillis();

                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                physics.onDown(event.getRawX(), event.getRawY(), downTime);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!options.isDraggable()) {
                    return true;
                }

                int newX = initialX + Math.round(event.getRawX() - initialTouchX);
                int newY = initialY + Math.round(event.getRawY() - initialTouchY);

                Rect bounds = getMovementBounds();
                params.x = ScreenUtils.clamp(newX, bounds.left, bounds.right);
                params.y = ScreenUtils.clamp(newY, bounds.top, bounds.bottom);

                physics.onMove(event.getRawX(), event.getRawY(), System.currentTimeMillis());

                updateLayout();

                if (!dragging && movedBeyondSlop(event)) {
                    dragging = true;
                    callback.onBubbleDragStart();
                }

                if (dragging) {
                    callback.onBubbleMove(params.x, params.y, bubbleView.getWidth(), bubbleView.getHeight());
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isClick(event)) {
                    callback.onBubbleClick();
                    return true;
                }

                if (dragging) {
                    boolean consumed = callback.onBubbleDragEnd(
                            params.x,
                            params.y,
                            bubbleView.getWidth(),
                            bubbleView.getHeight()
                    );
                    if (consumed) {
                        return true;
                    }
                }

                Rect releaseBounds = getMovementBounds();
                animator.flingThenSnap(
                        physics.getVelocityX(),
                        physics.getVelocityY(),
                        releaseBounds,
                        options.isEdgeMagnet()
                );
                callback.onReleased(params.x, params.y);
                return true;

            default:
                return false;
        }
    }

    private boolean movedBeyondSlop(MotionEvent event) {
        return Math.abs(event.getRawX() - initialTouchX) > touchSlop
                || Math.abs(event.getRawY() - initialTouchY) > touchSlop;
    }

    private boolean isClick(MotionEvent event) {
        long clickDuration = System.currentTimeMillis() - downTime;
        return clickDuration < 220
                && Math.abs(event.getRawX() - initialTouchX) < touchSlop
                && Math.abs(event.getRawY() - initialTouchY) < touchSlop;
    }

    private Rect getMovementBounds() {
        int width = ScreenUtils.getScreenWidth(windowManager);
        int height = ScreenUtils.getScreenHeight(windowManager);

        int bubbleWidth = Math.max(1, bubbleView.getWidth());
        int bubbleHeight = Math.max(1, bubbleView.getHeight());

        int minX = options.getLeftMargin();
        int maxX = width - bubbleWidth - options.getRightMargin();
        int minY = options.getTopMargin();
        int maxY = height - bubbleHeight - options.getBottomMargin();

        if (!options.isSafeZone()) {
            minX = 0;
            maxX = width - bubbleWidth;
            minY = 0;
            maxY = height - bubbleHeight;
        }

        return new Rect(minX, minY, maxX, maxY);
    }

    private void updateLayout() {
        if (!bubbleView.isAttachedToWindow()) {
            return;
        }
        try {
            windowManager.updateViewLayout(bubbleView, params);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public interface Callback {
        void onBubbleClick();

        void onBubbleDragStart();

        void onBubbleMove(int x, int y, int width, int height);

        boolean onBubbleDragEnd(int x, int y, int width, int height);

        void onReleased(int x, int y);
    }
}
