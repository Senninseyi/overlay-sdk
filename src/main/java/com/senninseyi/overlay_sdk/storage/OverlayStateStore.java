package com.senninseyi.overlay_sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class OverlayStateStore {

    private static final String PREFS_NAME = "overlay_sdk_state";

    private final SharedPreferences prefs;

    public OverlayStateStore(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveState(String bubbleId, BubbleState state) {
        prefs.edit()
                .putInt(key(bubbleId, "x"), state.x)
                .putInt(key(bubbleId, "y"), state.y)
                .putString(key(bubbleId, "edge"), state.lastEdge)
                .putBoolean(key(bubbleId, "visible"), state.visible)
                .putBoolean(key(bubbleId, "expanded"), state.expanded)
                .apply();
    }

    public BubbleState loadState(String bubbleId) {
        if (!prefs.contains(key(bubbleId, "x")) || !prefs.contains(key(bubbleId, "y"))) {
            return null;
        }

        BubbleState state = new BubbleState();
        state.x = prefs.getInt(key(bubbleId, "x"), 0);
        state.y = prefs.getInt(key(bubbleId, "y"), 0);
        state.lastEdge = prefs.getString(key(bubbleId, "edge"), "left");
        state.visible = prefs.getBoolean(key(bubbleId, "visible"), true);
        state.expanded = prefs.getBoolean(key(bubbleId, "expanded"), false);
        return state;
    }

    public void removeState(String bubbleId) {
        prefs.edit()
                .remove(key(bubbleId, "x"))
                .remove(key(bubbleId, "y"))
                .remove(key(bubbleId, "edge"))
                .remove(key(bubbleId, "visible"))
                .remove(key(bubbleId, "expanded"))
                .apply();
    }

    private String key(String bubbleId, String name) {
        return bubbleId + "_" + name;
    }

    public static class BubbleState {
        public int x;
        public int y;
        public String lastEdge = "left";
        public boolean visible = true;
        public boolean expanded = false;
    }
}
