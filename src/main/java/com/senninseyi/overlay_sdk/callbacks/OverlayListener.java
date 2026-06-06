package com.senninseyi.overlay_sdk.callbacks;

public interface OverlayListener {

    default void onBubbleClick(String bubbleId) {
    }

    default void onBubbleDragStart(String bubbleId) {
    }

    default void onBubbleDragEnd(String bubbleId) {
    }

    default void onBubbleExpanded(String bubbleId) {
    }

    default void onBubbleCollapsed(String bubbleId) {
    }

    default void onBubbleRemoved(String bubbleId) {
    }

    default void onPermissionRequired() {
    }

    default void onError(String bubbleId, String message) {
    }
}
