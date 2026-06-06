package com.senninseyi.overlay_sdk.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class OverlayPermissionManager {

    public static boolean hasPermission(Context context) {
        return Settings.canDrawOverlays(context);
    }

    public static void requestPermission(Activity activity) {

        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.getPackageName())
        );

        activity.startActivity(intent);
    }
}