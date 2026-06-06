package com.senninseyi.overlay_sdk.api;

public class OverlayOptions {

    private final int leftMargin;
    private final int rightMargin;
    private final int topMargin;
    private final int bottomMargin;

    private OverlayOptions(Builder builder) {
        this.leftMargin = builder.leftMargin;
        this.rightMargin = builder.rightMargin;
        this.topMargin = builder.topMargin;
        this.bottomMargin = builder.bottomMargin;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public static class Builder {
        private int leftMargin = 0;
        private int rightMargin = 0;
        private int topMargin = 0;
        private int bottomMargin = 0;
        public Builder leftMargin(int value) {
            this.leftMargin = value;
            return this;
        }

        public Builder rightMargin(int value) {
            this.rightMargin = value;
            return this;
        }

        public Builder topMargin(int value) {
            this.topMargin = value;
            return this;
        }

        public Builder bottomMargin(int value) {
            this.bottomMargin = value;
            return this;
        }

        public OverlayOptions build() {
            return new OverlayOptions(this);
        }
    }
}
