package com.senninseyi.overlay_sdk.panel;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;

import com.senninseyi.overlay_sdk.utils.ScreenUtils;

public class OverlayPanel extends FrameLayout {

    private final FrameLayout contentHost;

    public OverlayPanel(Context context) {
        super(context);

        int radius = ScreenUtils.dpToPx(context, 16);
        int padding = ScreenUtils.dpToPx(context, 12);

        GradientDrawable background = new GradientDrawable();
        background.setColor(0xFFF4F6FA);
        background.setCornerRadius(radius);

        setBackground(background);
        setPadding(padding, padding, padding, padding);
        setClickable(true);

        contentHost = new FrameLayout(context);
        LayoutParams hostParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        addView(contentHost, hostParams);
    }

    public void setPanelContent(View view) {
        contentHost.removeAllViews();
        if (view.getParent() instanceof FrameLayout) {
            ((FrameLayout) view.getParent()).removeView(view);
        }
        contentHost.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
}
