package com.senninseyi.overlay_sdk.views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.senninseyi.overlay_sdk.config.BubbleStyle;
import com.senninseyi.overlay_sdk.utils.ScreenUtils;

public class BubbleView extends FrameLayout {

    private final ImageView iconView;
    private final GradientDrawable bubbleBackground;
    private int bubbleSizePx;

    public BubbleView(Context context) {
        super(context);

        bubbleSizePx = ScreenUtils.dpToPx(context, 64);
        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        bubbleBackground = new GradientDrawable();
        bubbleBackground.setShape(GradientDrawable.OVAL);
        bubbleBackground.setColor(0xFF2F80ED);

        setBackground(bubbleBackground);
        setClipChildren(false);
        setClipToPadding(false);

        LayoutParams iconParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        );
        int iconPadding = ScreenUtils.dpToPx(context, 12);
        iconView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        addView(iconView, iconParams);
    }

    public void setIconRes(int iconResId) {
        if (iconResId != 0) {
            iconView.setImageResource(iconResId);
        }
    }

    public void applyStyle(BubbleStyle style) {
        bubbleSizePx = ScreenUtils.dpToPx(getContext(), style.getBubbleSizeDp());
        bubbleBackground.setColor(style.getBubbleColor());
        if (style.getIconResId() != 0) {
            iconView.setImageResource(style.getIconResId());
        }
    }

    public int getBubbleSizePx() {
        return bubbleSizePx;
    }
}
