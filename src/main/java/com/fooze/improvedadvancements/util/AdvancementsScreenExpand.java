package com.fooze.improvedadvancements.util;

import com.fooze.improvedadvancements.Config;

public final class AdvancementsScreenExpand {
    public static final int VANILLA_WINDOW_WIDTH = 252;
    public static final int VANILLA_WINDOW_HEIGHT = 140;
    public static final int HEADER_HEIGHT = 18;
    public static final int HORIZONTAL_BORDER = 9;
    public static final int BOTTOM_BORDER = 9;
    public static final int MARGIN = 128;

    private static int windowWidth = VANILLA_WINDOW_WIDTH;
    private static int windowHeight = VANILLA_WINDOW_HEIGHT;

    private AdvancementsScreenExpand() {
    }

    // Updates the advancement window size based on the screen size and the expand amount
    public static void expand(int screenWidth, int screenHeight) {
        // Only update if expand is enabled
        if (!Config.EXPAND_SCREEN.get()) {
            windowWidth = VANILLA_WINDOW_WIDTH;
            windowHeight = VANILLA_WINDOW_HEIGHT;
            return;
        }

        // Use the expand amount as a percentage of the screen size
        double scale = Config.EXPAND_AMOUNT.get() / 100.0;

        // Calculate the new window size based on the expand amount
        int expandedWidth = (int) Math.round(screenWidth * scale);
        int expandedHeight = (int) Math.round(screenHeight * scale);

        // Set the max window size to the screen size with a margin
        int maxWidth = Math.max(VANILLA_WINDOW_WIDTH, screenWidth - MARGIN);
        int maxHeight = Math.max(VANILLA_WINDOW_HEIGHT, screenHeight - MARGIN);

        // Set the window size to be within the minimum and maximum bounds
        windowWidth = Math.clamp(expandedWidth, VANILLA_WINDOW_WIDTH, maxWidth);
        windowHeight = Math.clamp(expandedHeight, VANILLA_WINDOW_HEIGHT, maxHeight);
    }

    // Returns the advancement window's width
    public static int windowWidth() {
        return windowWidth;
    }

    // Returns the advancement window's height
    public static int windowHeight() {
        return windowHeight;
    }

    // Returns the advancement window's inside width
    public static int insideWidth() {
        return windowWidth - HORIZONTAL_BORDER * 2;
    }

    // Returns the advancement window's inside height
    public static int insideHeight() {
        return windowHeight - HEADER_HEIGHT - BOTTOM_BORDER;
    }

    // Returns whether the advancement window is expanded
    public static boolean isExpanded() {
        return windowWidth != VANILLA_WINDOW_WIDTH || windowHeight != VANILLA_WINDOW_HEIGHT;
    }
}