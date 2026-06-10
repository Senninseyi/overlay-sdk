package com.senninseyi.overlay_sdk.views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.senninseyi.overlay_sdk.config.BubbleStyle;
import com.senninseyi.overlay_sdk.utils.ScreenUtils;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class BubbleView extends FrameLayout {

    private final ImageView iconView;
    private final GradientDrawable bubbleBackground;
    private int bubbleSizePx;
    private final ImageLoader imageLoader;

    public BubbleView(Context context) {
        super(context);

        // Initialize Coil ImageLoader with SVG support
        imageLoader = new ImageLoader.Builder(context).build();

        bubbleSizePx = ScreenUtils.dpToPx(context, 64);
        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
        addView(iconView, iconParams);
        
        // Default padding
        int defaultPadding = ScreenUtils.dpToPx(context, 12);
        iconView.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
    }

    public void setIconRes(int iconResId) {
        if (iconResId == 0) {
            iconView.setImageDrawable(null);
            return;
        }
        iconView.setImageResource(iconResId);
    }

    public void setIconSource(String source) {
        if (source == null || source.isEmpty()) {
            iconView.setImageDrawable(null);
            return;
        }
        ImageRequest request = new ImageRequest.Builder(getContext())
                .data(source)
                .target(iconView)
                .build();

        imageLoader.enqueue(request);
    }

    public void applyStyle(BubbleStyle style) {
        bubbleSizePx = ScreenUtils.dpToPx(getContext(), style.getBubbleSizeDp());
        bubbleBackground.setColor(style.getBubbleColor());

        // Apply scale type
        if (style.getIconScaleType() != null) {
            switch (style.getIconScaleType()) {
                case "centerCrop":
                    iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case "fitXY":
                    iconView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case "centerInside":
                default:
                    iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
            }
        }

        // Apply padding
        int paddingPx = ScreenUtils.dpToPx(getContext(), style.getIconPaddingDp());
        iconView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        // Load icon
        if (style.getIconResId() != 0) {
            setIconRes(style.getIconResId());
        } else if (style.getIconSource() != null && !style.getIconSource().isEmpty()) {
            setIconSource(style.getIconSource());
        }
    }

    public int getBubbleSizePx() {
        return bubbleSizePx;
    }
}
