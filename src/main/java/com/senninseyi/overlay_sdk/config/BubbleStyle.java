package com.senninseyi.overlay_sdk.config;

public class BubbleStyle {

    private final int bubbleSizeDp;
    private final int bubbleColor;
    private final int removeZoneWidthDp;
    private final int removeZoneHeightDp;
    private final int removeMagnetRadiusDp;

    private BubbleStyle(Builder builder) {
        this.bubbleSizeDp = builder.bubbleSizeDp;
        this.bubbleColor = builder.bubbleColor;
        this.removeZoneWidthDp = builder.removeZoneWidthDp;
        this.removeZoneHeightDp = builder.removeZoneHeightDp;
        this.removeMagnetRadiusDp = builder.removeMagnetRadiusDp;
    }

    public int getBubbleSizeDp() {
        return bubbleSizeDp;
    }

    public int getBubbleColor() {
        return bubbleColor;
    }

    public int getRemoveZoneWidthDp() {
        return removeZoneWidthDp;
    }

    public int getRemoveZoneHeightDp() {
        return removeZoneHeightDp;
    }

    public int getRemoveMagnetRadiusDp() {
        return removeMagnetRadiusDp;
    }

    public static class Builder {
        private int bubbleSizeDp = 64;
        private int bubbleColor = 0xFF2F80ED;
        private int removeZoneWidthDp = 128;
        private int removeZoneHeightDp = 56;
        private int removeMagnetRadiusDp = 96;

        public Builder bubbleSizeDp(int value) {
            this.bubbleSizeDp = value;
            return this;
        }

        public Builder bubbleColor(int value) {
            this.bubbleColor = value;
            return this;
        }

        public Builder removeZoneWidthDp(int value) {
            this.removeZoneWidthDp = value;
            return this;
        }

        public Builder removeZoneHeightDp(int value) {
            this.removeZoneHeightDp = value;
            return this;
        }

        public Builder removeMagnetRadiusDp(int value) {
            this.removeMagnetRadiusDp = value;
            return this;
        }

        public BubbleStyle build() {
            return new BubbleStyle(this);
        }
    }
}
