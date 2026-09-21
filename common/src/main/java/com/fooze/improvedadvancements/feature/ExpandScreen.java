package com.fooze.improvedadvancements.feature;

import com.fooze.improvedadvancements.Config;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.Nullable;

import java.util.Map;

public class ExpandScreen {
    private static final int VANILLA_WINDOW_WIDTH = 252;
    private static final int VANILLA_WINDOW_HEIGHT = 140;
    private static final int MARGIN = 128;
    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 6;
    private static final int TITLE_COLOR = 4210752;
    private static final int HEADER_HEIGHT = 18;
    private static final int HORIZONTAL_BORDER = 9;
    private static final int BOTTOM_BORDER = 9;
    private static final int HORIZONTAL_TAB_OFFSET = 4;
    private static final int HORIZONTAL_TAB_STEP = 32;
    private static final int VERTICAL_TAB_STEP = 28;
    private static final int BACKGROUND_PADDING = 32;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    // Texture for the advancement window
    private static final Identifier WINDOW_TEXTURE = Identifier.withDefaultNamespace(
            "textures/gui/advancements/window.png"
    );

    private static int windowWidth = VANILLA_WINDOW_WIDTH;
    private static int windowHeight = VANILLA_WINDOW_HEIGHT;

    // Expands the advancement window size based on the screen size and the expand amount
    public static void expand(int screenWidth, int screenHeight) {
        // Only expand if the config option is enabled
        if (!Config.get().expandScreen()) {
            windowWidth = VANILLA_WINDOW_WIDTH;
            windowHeight = VANILLA_WINDOW_HEIGHT;
            return;
        }

        // Use the expand amount as a percentage of the screen size
        double scale = Config.get().expandAmount() / 100.0;

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

    // Renders the expanded advancement window
    public static boolean render(
            GuiGraphicsExtractor graphics, int offsetX, int offsetY,
            Map<AdvancementHolder, AdvancementTab> tabs,
            @Nullable AdvancementTab selectedTab, Font font, Component title
    ) {
        // Only render the window if it's expanded
        if (!isExpanded()) {
            return false;
        }

        // Draw the window shadow and frame
        drawWindowShadow(graphics, offsetX, offsetY);
        drawWindowFrame(graphics, offsetX, offsetY);

        // Draw the tabs if there's more than one
        if (tabs.size() > 1) {
            for (AdvancementTab tab : tabs.values()) {

                tab.extractTab(
                        graphics, offsetX, offsetY,
                        Integer.MIN_VALUE, Integer.MIN_VALUE,
                        tab == selectedTab
                );

                tab.extractIcon(graphics, offsetX, offsetY);
            }
        }

        // Set the window title to the screen title
        Component windowTitle = title;

        // Set the window title to the selected tab title if it exists
        if (selectedTab != null) {
            windowTitle = selectedTab.getTitle();
        }

        // Draw the window title
        graphics.text(
                font, windowTitle,
                offsetX + TITLE_X, offsetY + TITLE_Y,
                TITLE_COLOR, false
        );

        return true;
    }

    // Restores the selected tab after the advancement screen rebuilds its tabs
    public static void restoreTab(
            ClientAdvancements advancements, Map<AdvancementHolder, AdvancementTab> tabs,
            @Nullable AdvancementTab selectedTab
    ) {
        if (selectedTab != null && tabs.containsKey(selectedTab.getRootAdvancement())) {
            advancements.setSelectedTab(selectedTab.getRootAdvancement(), true);
        }
    }

    // Returns whether the window is expanded
    public static boolean isExpanded() {
        return windowWidth != VANILLA_WINDOW_WIDTH || windowHeight != VANILLA_WINDOW_HEIGHT;
    }

    // Returns the expanded value when the window is expanded
    public static int value(int original, int expanded) {
        if (isExpanded()) {
            return expanded;
        }

        return original;
    }

    // Returns the width of the window
    public static int windowWidth() {
        return windowWidth;
    }

    // Returns the height of the window
    public static int windowHeight() {
        return windowHeight;
    }

    // Returns the inside width of the window
    public static int insideWidth() {
        return windowWidth - HORIZONTAL_BORDER * 2;
    }

    // Returns the inside height of the window
    public static int insideHeight() {
        return windowHeight - HEADER_HEIGHT - BOTTOM_BORDER;
    }

    // Returns whether the window size has changed
    public static boolean sizeChanged(int previousWidth, int previousHeight) {
        return isExpanded() && (
                insideWidth() != previousWidth || insideHeight() != previousHeight
        );
    }

    // Returns how many tabs can fit on the window based on the type
    public static int tabCapacity(Enum<?> tabType) {
        if (isHorizontalTabType(tabType)) {
            return (windowWidth + HORIZONTAL_TAB_OFFSET) / HORIZONTAL_TAB_STEP;
        } else {
            return windowHeight / VERTICAL_TAB_STEP;
        }
    }

    // Returns the tab capacity used for edge tabs
    public static int edgeTabCapacity(Enum<?> tabType) {
        boolean horizontal = isHorizontalTabType(tabType);
        int capacity = tabCapacity(tabType);

        // Calculate whether the tab is an edge tab
        if (horizontal) {
            if ((capacity - 1) * HORIZONTAL_TAB_STEP + VERTICAL_TAB_STEP == windowWidth()) {
                return capacity;
            }
        } else {
            if (capacity * VERTICAL_TAB_STEP == windowHeight()) {
                return capacity;
            }
        }

        return Integer.MAX_VALUE;
    }

    // Returns whether the tab type is horizontal
    private static boolean isHorizontalTabType(Enum<?> tabType) {
        return switch (tabType.name()) {
            case "ABOVE", "BELOW" -> true;
            case "LEFT", "RIGHT" -> false;
            default -> throw new IllegalArgumentException("Invalid tab type: " + tabType);
        };
    }

    // Draws the background
    public static void drawBackground(
            GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier texture,
            int x, int y, float u, float v, int width, int height,
            int textureWidth, int textureHeight
    ) {
        if (isExpanded()) {
            width = insideWidth() + BACKGROUND_PADDING;
            height = insideHeight() + BACKGROUND_PADDING;
        }

        graphics.blit(pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    // Draws the window shadow
    private static void drawWindowShadow(GuiGraphicsExtractor graphics, int x, int y) {
        int insideWidth = insideWidth();
        int insideHeight = insideHeight();
        int rightX = x + HORIZONTAL_BORDER + insideWidth;
        int bottomY = y + HEADER_HEIGHT + insideHeight;

        blit(graphics, x + 9, y + 18, 6, 5, 9, 18, 6, 5);
        blit(graphics, x + 15, y + 18, insideWidth - 12, 5, 15, 18, 222, 5);
        blit(graphics, rightX - 6, y + 18, 6, 5, 237, 18, 6, 5);
        blit(graphics, x + 9, y + 23, 6, insideHeight - 11, 9, 23, 6, 102);
        blit(graphics, rightX - 6, y + 23, 6, insideHeight - 11, 237, 23, 6, 102);
        blit(graphics, x + 9, bottomY - 6, 6, 6, 9, 125, 6, 6);
        blit(graphics, x + 15, bottomY - 6, insideWidth - 12, 6, 15, 125, 222, 6);
        blit(graphics, rightX - 6, bottomY - 6, 6, 6, 237, 125, 6, 6);
    }

    // Draws the window frame
    private static void drawWindowFrame(GuiGraphicsExtractor graphics, int x, int y) {
        int insideWidth = insideWidth();
        int insideHeight = insideHeight();
        int rightX = x + HORIZONTAL_BORDER + insideWidth;
        int bottomY = y + HEADER_HEIGHT + insideHeight;

        blit(graphics, x, y, 9, 18, 0, 0, 9, 18);
        blit(graphics, x + 9, y, insideWidth, 18, 9, 0, 234, 18);
        blit(graphics, rightX, y, 9, 18, 243, 0, 9, 18);
        blit(graphics, x, y + 18, 9, insideHeight, 0, 18, 9, 113);
        blit(graphics, rightX, y + 18, 9, insideHeight, 243, 18, 9, 113);
        blit(graphics, x, bottomY, 9, 9, 0, 131, 9, 9);
        blit(graphics, x + 9, bottomY, insideWidth, 9, 9, 131, 234, 9);
        blit(graphics, rightX, bottomY, 9, 9, 243, 131, 9, 9);
    }

    // Helper method for drawing a section of the window
    private static void blit(
            GuiGraphicsExtractor graphics, int x, int y, int width, int height,
            int textureX, int textureY, int textureWidth, int textureHeight
    ) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, WINDOW_TEXTURE, x, y,
                textureX, textureY, width, height, textureWidth, textureHeight,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }
}