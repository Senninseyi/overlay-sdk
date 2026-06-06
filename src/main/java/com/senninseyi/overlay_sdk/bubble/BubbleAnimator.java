package com.senninseyi.overlay_sdk.bubble;

import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class BubbleAnimator {

    private final WindowManager windowManager;
    private final View bubbleView;
    private final WindowManager.LayoutParams params;

    private final FloatValueHolder xHolder = new FloatValueHolder();
    private final FloatValueHolder yHolder = new FloatValueHolder();

    private FlingAnimation flingX;
    private FlingAnimation flingY;
    private SpringAnimation springX;
    private SpringAnimation springY;

    public BubbleAnimator(WindowManager windowManager, View bubbleView, WindowManager.LayoutParams params) {
        this.windowManager = windowManager;
        this.bubbleView = bubbleView;
        this.params = params;
    }

    public void cancelAll() {
        if (flingX != null) {
            flingX.cancel();
        }
        if (flingY != null) {
            flingY.cancel();
        }
        if (springX != null) {
            springX.cancel();
        }
        if (springY != null) {
            springY.cancel();
        }
    }

    public void flingThenSnap(float velocityX, float velocityY, Rect movementBounds, boolean edgeMagnet) {
        cancelAll();

        xHolder.setValue(params.x);
        yHolder.setValue(params.y);

        flingX = new FlingAnimation(xHolder)
                .setStartVelocity(velocityX)
                .setMinValue(movementBounds.left)
                .setMaxValue(movementBounds.right)
                .setFriction(1.3f);

        flingY = new FlingAnimation(yHolder)
                .setStartVelocity(velocityY)
                .setMinValue(movementBounds.top)
                .setMaxValue(movementBounds.bottom)
                .setFriction(1.35f);

        flingX.addUpdateListener((animation, value, velocity) -> {
            params.x = Math.round(value);
            updateLayoutSafely();
        });

        flingY.addUpdateListener((animation, value, velocity) -> {
            params.y = Math.round(value);
            updateLayoutSafely();
        });

        flingX.addEndListener((animation, canceled, value, velocity) -> {
            int clampedX = Math.max(movementBounds.left, Math.min(Math.round(value), movementBounds.right));
            if (!edgeMagnet) {
                springXTo(clampedX);
                return;
            }

            int centerX = clampedX + (bubbleView.getWidth() / 2);
            int halfX = (movementBounds.left + movementBounds.right) / 2;
            int targetX = centerX < halfX ? movementBounds.left : movementBounds.right;
            springXTo(targetX);
        });

        flingY.addEndListener((animation, canceled, value, velocity) -> {
            int clampedY = Math.max(movementBounds.top, Math.min(Math.round(value), movementBounds.bottom));
            springYTo(clampedY);
        });

        flingX.start();
        flingY.start();
    }

    private void springXTo(int target) {
        xHolder.setValue(params.x);
        springX = new SpringAnimation(xHolder)
                .setSpring(new SpringForce(target)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                        .setStiffness(SpringForce.STIFFNESS_MEDIUM));

        springX.addUpdateListener((DynamicAnimation.OnAnimationUpdateListener) (animation, value, velocity) -> {
            params.x = Math.round(value);
            updateLayoutSafely();
        });
        springX.start();
    }

    private void springYTo(int target) {
        yHolder.setValue(params.y);
        springY = new SpringAnimation(yHolder)
                .setSpring(new SpringForce(target)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
                        .setStiffness(SpringForce.STIFFNESS_MEDIUM));

        springY.addUpdateListener((DynamicAnimation.OnAnimationUpdateListener) (animation, value, velocity) -> {
            params.y = Math.round(value);
            updateLayoutSafely();
        });
        springY.start();
    }

    private void updateLayoutSafely() {
        if (!bubbleView.isAttachedToWindow()) {
            return;
        }

        try {
            windowManager.updateViewLayout(bubbleView, params);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
