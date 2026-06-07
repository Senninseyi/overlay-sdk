package com.senninseyi.overlay_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.annotation.Nullable;

import com.senninseyi.overlay_sdk.callbacks.OverlayListener;
import com.senninseyi.overlay_sdk.config.OverlayOptions;
import com.senninseyi.overlay_sdk.permission.OverlayPermissionManager;
import com.senninseyi.overlay_sdk.service.OverlayService;

import java.util.Map;

public final class OverlaySDK {

    public static final String DEFAULT_BUBBLE_ID = "default";

    private static Context appContext;
    private static OverlayManager overlayManager;

    private OverlaySDK() {
    }

    public static synchronized void initialize(Context context) {
        appContext = context.getApplicationContext();
        overlayManager = OverlayManager.getInstance(appContext);
    }

    public static synchronized void setListener(@Nullable OverlayListener listener) {
        ensureInitialized();
        overlayManager.setListener(listener);
    }

    public static boolean hasOverlayPermission() {
        ensureInitialized();
        return OverlayPermissionManager.hasPermission(appContext);
    }

    public static void requestOverlayPermission(Activity activity) {
        OverlayPermissionManager.requestPermission(activity);
    }

    public static void showBubble(OverlayOptions options) {
        ensureInitialized();
        ensureServiceRunning();
        String id = options.getBubbleId() != null ? options.getBubbleId() : DEFAULT_BUBBLE_ID;
        overlayManager.showBubble(id, options);
    }

    public static void showBubble(Map<String, ?> payload) {
        showBubble(OverlayOptions.fromMap(payload));
    }

    public static void showBubbleFromJson(String payload) {
        showBubble(OverlayOptions.fromJson(payload));
    }

    public static void showBubble(String bubbleId) {
        showBubble(new OverlayOptions.Builder().bubbleId(bubbleId).build());
    }

    public static void hideBubble() {
        hideBubble(DEFAULT_BUBBLE_ID);
    }

    public static void hideBubble(String bubbleId) {
        ensureInitialized();
        overlayManager.hideBubble(bubbleId);
    }

    public static void removeBubble() {
        removeBubble(DEFAULT_BUBBLE_ID);
    }

    public static void removeBubble(String bubbleId) {
        ensureInitialized();
        overlayManager.removeBubble(bubbleId);
    }

    public static void expandBubble() {
        expandBubble(DEFAULT_BUBBLE_ID);
    }

    public static void expandBubble(String bubbleId) {
        ensureInitialized();
        overlayManager.expandBubble(bubbleId);
    }

    public static void collapseBubble() {
        collapseBubble(DEFAULT_BUBBLE_ID);
    }

    public static void collapseBubble(String bubbleId) {
        ensureInitialized();
        overlayManager.collapseBubble(bubbleId);
    }

    public static void showPanel(View contentView) {
        showPanel(DEFAULT_BUBBLE_ID, contentView);
    }

    public static void showPanel(String bubbleId, View contentView) {
        ensureInitialized();
        overlayManager.showPanel(bubbleId, contentView);
    }

    public static void hidePanel() {
        hidePanel(DEFAULT_BUBBLE_ID);
    }

    public static void hidePanel(String bubbleId) {
        ensureInitialized();
        overlayManager.hidePanel(bubbleId);
    }

    public static void shutdown() {
        if (overlayManager != null) {
            overlayManager.removeAll();
        }
    }

    private static void ensureServiceRunning() {
        if (appContext == null) {
            return;
        }

        Intent intent = new Intent(appContext, OverlayService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext.startForegroundService(intent);
            } else {
                appContext.startService(intent);
            }
        } catch (RuntimeException exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    && exception instanceof android.app.ForegroundServiceStartNotAllowedException) {
                return;
            }
            throw exception;
        }
    }

    private static void ensureInitialized() {
        if (appContext != null && overlayManager != null) {
            return;
        }
        throw new IllegalStateException("OverlaySDK.initialize(context) must be called first.");
    }
}
