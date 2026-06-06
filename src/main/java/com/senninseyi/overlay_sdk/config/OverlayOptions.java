package com.senninseyi.overlay_sdk.config;

public class OverlayOptions {

    private final String bubbleId;
    private final int iconResId;
    private final boolean draggable;
    private final boolean edgeMagnet;
    private final boolean safeZone;
    private final int leftMargin;
    private final int rightMargin;
    private final int topMargin;
    private final int bottomMargin;
    private final Integer initialX;
    private final Integer initialY;
    private final BubbleStyle bubbleStyle;
    private final BubbleClickAction bubbleClickAction;

    private OverlayOptions(Builder builder) {
        this.bubbleId = builder.bubbleId;
        this.iconResId = builder.iconResId;
        this.draggable = builder.draggable;
        this.edgeMagnet = builder.edgeMagnet;
        this.safeZone = builder.safeZone;
        this.leftMargin = builder.leftMargin;
        this.rightMargin = builder.rightMargin;
        this.topMargin = builder.topMargin;
        this.bottomMargin = builder.bottomMargin;
        this.initialX = builder.initialX;
        this.initialY = builder.initialY;
        this.bubbleStyle = builder.bubbleStyle;
        this.bubbleClickAction = builder.bubbleClickAction != null
            ? builder.bubbleClickAction
            : BubbleClickAction.OPEN_APP;
    }

    public String getBubbleId() {
        return bubbleId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public boolean isEdgeMagnet() {
        return edgeMagnet;
    }

    public boolean isSafeZone() {
        return safeZone;
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

    public Integer getInitialX() {
        return initialX;
    }

    public Integer getInitialY() {
        return initialY;
    }

    public BubbleStyle getBubbleStyle() {
        return bubbleStyle;
    }

    public BubbleClickAction getBubbleClickAction() {
        return bubbleClickAction;
    }

    public static class Builder {
        private String bubbleId = "default";
        private int iconResId = android.R.drawable.ic_dialog_info;
        private boolean draggable = true;
        private boolean edgeMagnet = true;
        private boolean safeZone = true;
        private int leftMargin = 20;
        private int rightMargin = 20;
        private int topMargin = 50;
        private int bottomMargin = 50;
        private Integer initialX;
        private Integer initialY;
        private BubbleStyle bubbleStyle = new BubbleStyle.Builder().build();
        private BubbleClickAction bubbleClickAction = BubbleClickAction.OPEN_APP;

        public Builder bubbleId(String value) {
            this.bubbleId = value;
            return this;
        }

        public Builder icon(int value) {
            this.iconResId = value;
            return this;
        }

        public Builder draggable(boolean value) {
            this.draggable = value;
            return this;
        }

        public Builder edgeMagnet(boolean value) {
            this.edgeMagnet = value;
            return this;
        }

        public Builder safeZone(boolean value) {
            this.safeZone = value;
            return this;
        }

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

        public Builder initialX(int value) {
            this.initialX = value;
            return this;
        }

        public Builder initialY(int value) {
            this.initialY = value;
            return this;
        }

        public Builder bubbleStyle(BubbleStyle style) {
            this.bubbleStyle = style;
            return this;
        }

        public Builder bubbleClickAction(BubbleClickAction value) {
            this.bubbleClickAction = value != null ? value : BubbleClickAction.OPEN_APP;
            return this;
        }

        public OverlayOptions build() {
            return new OverlayOptions(this);
        }
    }
}
