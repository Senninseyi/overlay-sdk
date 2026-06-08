package com.senninseyi.overlay_sdk.views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.senninseyi.overlay_sdk.config.BubbleStyle;
import com.senninseyi.overlay_sdk.utils.ScreenUtils;

import coil.Coil;
import coil.ImageLoader;
import coil.decode.SvgDecoder;
import coil.request.ImageRequest;

public class BubbleView extends FrameLayout {

    private final ImageView iconView;
    private final GradientDrawable bubbleBackground;
    private int bubbleSizePx;
    private final ImageLoader imageLoader;

    public BubbleView(Context context) {
        super(context);

        // Initialize Coil ImageLoader with SVG support
        imageLoader = new ImageLoader.Builder(context)
                .build();

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

    public void setIconPath(String path) {
        if (path != null && !path.isEmpty()) {
            ImageRequest request = new ImageRequest.Builder(getContext())
                    .data(path)
                    .target(iconView)
                    .build();
            imageLoader.enqueue(request);
        }
    }

    public void applyStyle(BubbleStyle style) {
        bubbleSizePx = ScreenUtils.dpToPx(getContext(), style.getBubbleSizeDp());
        bubbleBackground.setColor(style.getBubbleColor());

        if (style.getIconPath() != null && !style.getIconPath().isEmpty()) {
            setIconPath(style.getIconPath());
        } else if (style.getIconResId() != 0) {
            setIconRes(style.getIconResId());
        }
    }

    public int getBubbleSizePx() {
        return bubbleSizePx;
    }
}
