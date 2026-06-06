package com.senninseyi.overlay_sdk.bubble;

public class BubblePhysics {

    private float lastRawX;
    private float lastRawY;
    private long lastMoveTime;
    private float velocityX;
    private float velocityY;

    public void onDown(float rawX, float rawY, long timeMs) {
        lastRawX = rawX;
        lastRawY = rawY;
        lastMoveTime = timeMs;
        velocityX = 0f;
        velocityY = 0f;
    }

    public void onMove(float rawX, float rawY, long timeMs) {
        long deltaTime = Math.max(1L, timeMs - lastMoveTime);
        velocityX = ((rawX - lastRawX) * 1000f) / deltaTime;
        velocityY = ((rawY - lastRawY) * 1000f) / deltaTime;

        lastRawX = rawX;
        lastRawY = rawY;
        lastMoveTime = timeMs;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }
}
