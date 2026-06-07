package com.senninseyi.overlay_sdk.config;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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

    public static OverlayOptions fromMap(Map<String, ?> payload) {
        Builder builder = new Builder();
        if (payload == null || payload.isEmpty()) {
            return builder.build();
        }

        String bubbleIdValue = stringValue(payload, "bubbleId", "bubble_id");
        if (bubbleIdValue != null) {
            builder.bubbleId(bubbleIdValue);
        }

        Integer iconResIdValue = intValue(payload, "iconResId", "icon", "icon_res_id");
        if (iconResIdValue != null) {
            builder.icon(iconResIdValue);
        }

        Boolean draggableValue = booleanValue(payload, "draggable");
        if (draggableValue != null) {
            builder.draggable(draggableValue);
        }

        Boolean edgeMagnetValue = booleanValue(payload, "edgeMagnet", "edge_magnet");
        if (edgeMagnetValue != null) {
            builder.edgeMagnet(edgeMagnetValue);
        }

        Boolean safeZoneValue = booleanValue(payload, "safeZone", "safe_zone");
        if (safeZoneValue != null) {
            builder.safeZone(safeZoneValue);
        }

        Integer leftMarginValue = intValue(payload, "leftMargin", "left_margin");
        if (leftMarginValue != null) {
            builder.leftMargin(leftMarginValue);
        }

        Integer rightMarginValue = intValue(payload, "rightMargin", "right_margin");
        if (rightMarginValue != null) {
            builder.rightMargin(rightMarginValue);
        }

        Integer topMarginValue = intValue(payload, "topMargin", "top_margin");
        if (topMarginValue != null) {
            builder.topMargin(topMarginValue);
        }

        Integer bottomMarginValue = intValue(payload, "bottomMargin", "bottom_margin");
        if (bottomMarginValue != null) {
            builder.bottomMargin(bottomMarginValue);
        }

        Integer initialXValue = intValue(payload, "initialX", "initial_x");
        if (initialXValue != null) {
            builder.initialX(initialXValue);
        }

        Integer initialYValue = intValue(payload, "initialY", "initial_y");
        if (initialYValue != null) {
            builder.initialY(initialYValue);
        }

        BubbleClickAction clickAction = clickActionValue(payload, "bubbleClickAction", "bubble_click_action");
        if (clickAction != null) {
            builder.bubbleClickAction(clickAction);
        }

        BubbleStyle styleValue = styleValue(payload, "bubbleStyle", "bubble_style");
        if (styleValue != null) {
            builder.bubbleStyle(styleValue);
        }

        return builder.build();
    }

    public static OverlayOptions fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new Builder().build();
        }

        try {
            return fromJsonObject(new JSONObject(json));
        } catch (JSONException ignored) {
            return new Builder().build();
        }
    }

    public static OverlayOptions fromJsonObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            return new Builder().build();
        }

        Builder builder = new Builder();

        if (jsonObject.has("bubbleId")) {
            builder.bubbleId(jsonObject.optString("bubbleId", "default"));
        }

        if (jsonObject.has("iconResId")) {
            builder.icon(jsonObject.optInt("iconResId"));
        } else if (jsonObject.has("icon")) {
            builder.icon(jsonObject.optInt("icon"));
        }

        if (jsonObject.has("draggable")) {
            builder.draggable(jsonObject.optBoolean("draggable"));
        }
        if (jsonObject.has("edgeMagnet")) {
            builder.edgeMagnet(jsonObject.optBoolean("edgeMagnet"));
        }
        if (jsonObject.has("safeZone")) {
            builder.safeZone(jsonObject.optBoolean("safeZone"));
        }

        if (jsonObject.has("leftMargin")) {
            builder.leftMargin(jsonObject.optInt("leftMargin"));
        }
        if (jsonObject.has("rightMargin")) {
            builder.rightMargin(jsonObject.optInt("rightMargin"));
        }
        if (jsonObject.has("topMargin")) {
            builder.topMargin(jsonObject.optInt("topMargin"));
        }
        if (jsonObject.has("bottomMargin")) {
            builder.bottomMargin(jsonObject.optInt("bottomMargin"));
        }
        if (jsonObject.has("initialX")) {
            builder.initialX(jsonObject.optInt("initialX"));
        }
        if (jsonObject.has("initialY")) {
            builder.initialY(jsonObject.optInt("initialY"));
        }

        BubbleClickAction clickAction = parseClickAction(jsonObject.optString("bubbleClickAction", null));
        if (clickAction == null) {
            clickAction = parseClickAction(jsonObject.optString("bubble_click_action", null));
        }
        if (clickAction != null) {
            builder.bubbleClickAction(clickAction);
        }

        JSONObject bubbleStyleObject = jsonObject.optJSONObject("bubbleStyle");
        if (bubbleStyleObject == null) {
            bubbleStyleObject = jsonObject.optJSONObject("bubble_style");
        }
        if (bubbleStyleObject != null) {
            BubbleStyle style = parseBubbleStyle(bubbleStyleObject);
            if (style != null) {
                builder.bubbleStyle(style);
            }
        }

        return builder.build();
    }

    private static BubbleStyle styleValue(Map<String, ?> source, String... keys) {
        Object raw = valueForAnyKey(source, keys);
        if (!(raw instanceof Map)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String, ?> styleMap = (Map<String, ?>) raw;
        BubbleStyle.Builder styleBuilder = new BubbleStyle.Builder();

        Integer bubbleSizeDp = intValue(styleMap, "bubbleSizeDp", "bubble_size_dp");
        if (bubbleSizeDp != null) {
            styleBuilder.bubbleSizeDp(bubbleSizeDp);
        }

        Integer iconResId = intValue(styleMap, "iconResId", "icon_res_id", "icon");
        if (iconResId != null) {
            styleBuilder.iconResId(iconResId);
        }

        Integer removeZoneWidthDp = intValue(styleMap, "removeZoneWidthDp", "remove_zone_width_dp");
        if (removeZoneWidthDp != null) {
            styleBuilder.removeZoneWidthDp(removeZoneWidthDp);
        }

        Integer removeZoneHeightDp = intValue(styleMap, "removeZoneHeightDp", "remove_zone_height_dp");
        if (removeZoneHeightDp != null) {
            styleBuilder.removeZoneHeightDp(removeZoneHeightDp);
        }

        Integer removeMagnetRadiusDp = intValue(styleMap, "removeMagnetRadiusDp", "remove_magnet_radius_dp");
        if (removeMagnetRadiusDp != null) {
            styleBuilder.removeMagnetRadiusDp(removeMagnetRadiusDp);
        }

        Integer bubbleColor = colorValue(styleMap, "bubbleColor", "bubble_color");
        if (bubbleColor != null) {
            styleBuilder.bubbleColor(bubbleColor);
        }

        return styleBuilder.build();
    }

    private static BubbleStyle parseBubbleStyle(JSONObject styleObject) {
        BubbleStyle.Builder builder = new BubbleStyle.Builder();

        if (styleObject.has("bubbleSizeDp")) {
            builder.bubbleSizeDp(styleObject.optInt("bubbleSizeDp"));
        } else if (styleObject.has("bubble_size_dp")) {
            builder.bubbleSizeDp(styleObject.optInt("bubble_size_dp"));
        }

        if (styleObject.has("iconResId")) {
            builder.iconResId(styleObject.optInt("iconResId"));
        } else if (styleObject.has("icon_res_id")) {
            builder.iconResId(styleObject.optInt("icon_res_id"));
        } else if (styleObject.has("icon")) {
            builder.iconResId(styleObject.optInt("icon"));
        }

        if (styleObject.has("removeZoneWidthDp")) {
            builder.removeZoneWidthDp(styleObject.optInt("removeZoneWidthDp"));
        } else if (styleObject.has("remove_zone_width_dp")) {
            builder.removeZoneWidthDp(styleObject.optInt("remove_zone_width_dp"));
        }

        if (styleObject.has("removeZoneHeightDp")) {
            builder.removeZoneHeightDp(styleObject.optInt("removeZoneHeightDp"));
        } else if (styleObject.has("remove_zone_height_dp")) {
            builder.removeZoneHeightDp(styleObject.optInt("remove_zone_height_dp"));
        }

        if (styleObject.has("removeMagnetRadiusDp")) {
            builder.removeMagnetRadiusDp(styleObject.optInt("removeMagnetRadiusDp"));
        } else if (styleObject.has("remove_magnet_radius_dp")) {
            builder.removeMagnetRadiusDp(styleObject.optInt("remove_magnet_radius_dp"));
        }

        if (styleObject.has("bubbleColor")) {
            builder.bubbleColor(parseColor(styleObject.opt("bubbleColor")));
        } else if (styleObject.has("bubble_color")) {
            builder.bubbleColor(parseColor(styleObject.opt("bubble_color")));
        }

        return builder.build();
    }

    private static BubbleClickAction clickActionValue(Map<String, ?> source, String... keys) {
        String raw = stringValue(source, keys);
        return parseClickAction(raw);
    }

    private static BubbleClickAction parseClickAction(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        String normalized = raw.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        try {
            return BubbleClickAction.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static String stringValue(Map<String, ?> source, String... keys) {
        Object raw = valueForAnyKey(source, keys);
        if (raw == null) {
            return null;
        }
        if (raw instanceof String) {
            String value = ((String) raw).trim();
            return value.isEmpty() ? null : value;
        }
        return String.valueOf(raw);
    }

    private static Integer intValue(Map<String, ?> source, String... keys) {
        Object raw = valueForAnyKey(source, keys);
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }
        if (raw instanceof String) {
            try {
                return Integer.parseInt(((String) raw).trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Integer colorValue(Map<String, ?> source, String... keys) {
        Object raw = valueForAnyKey(source, keys);
        if (raw == null) {
            return null;
        }
        return parseColor(raw);
    }

    private static int parseColor(Object raw) {
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }

        if (raw instanceof String) {
            String value = ((String) raw).trim();
            if (value.isEmpty()) {
                return 0xFF2F80ED;
            }

            try {
                if (value.startsWith("#")) {
                    return Color.parseColor(value);
                }

                String normalized = value.startsWith("0x") || value.startsWith("0X")
                        ? value.substring(2)
                        : value;
                return (int) Long.parseLong(normalized, 16);
            } catch (IllegalArgumentException ignored) {
                return 0xFF2F80ED;
            }
        }

        return 0xFF2F80ED;
    }

    private static Boolean booleanValue(Map<String, ?> source, String... keys) {
        Object raw = valueForAnyKey(source, keys);
        if (raw == null) {
            return null;
        }
        if (raw instanceof Boolean) {
            return (Boolean) raw;
        }
        if (raw instanceof String) {
            String value = ((String) raw).trim();
            if (value.equalsIgnoreCase("true")) {
                return true;
            }
            if (value.equalsIgnoreCase("false")) {
                return false;
            }
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue() != 0;
        }
        return null;
    }

    private static Object valueForAnyKey(Map<String, ?> source, String... keys) {
        if (source == null || keys == null) {
            return null;
        }

        for (String key : keys) {
            if (source.containsKey(key)) {
                return source.get(key);
            }
        }
        return null;
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
