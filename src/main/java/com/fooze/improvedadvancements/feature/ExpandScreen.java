package com.fooze.improvedadvancements.feature;

import com.fooze.improvedadvancements.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public final class ExpandScreen {
    public static final int VANILLA_WINDOW_WIDTH = 252;
    public static final int VANILLA_WINDOW_HEIGHT = 140;
    public static final int HEADER_HEIGHT = 18;
    public static final int HORIZONTAL_BORDER = 9;
    public static final int BOTTOM_BORDER = 9;
    public static final int MARGIN = 128;
    private static final int HORIZONTAL_TAB_STEP = 32;
    private static final int VERTICAL_TAB_STEP = 28;

    private static final ResourceLocation WINDOW_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/advancements/window.png");

    private static int windowWidth = VANILLA_WINDOW_WIDTH;
    private static int windowHeight = VANILLA_WINDOW_HEIGHT;

    private ExpandScreen() {}

    // Expands the advancement window size based on the screen size and the expand amount
    public static void expand(int screenWidth, int screenHeight) {
        // Only expand if the config option is enabled
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

    // Renders the expanded advancement window
    public static boolean render(
            GuiGraphics graphics, int offsetX, int offsetY,
            Map<AdvancementHolder, AdvancementTab> tabs, int tabPage,
            @Nullable AdvancementTab selectedTab, Font font, Component title
    ) {
        // Only render the window if it's expanded
        if (!isExpanded()) {
            return false;
        }

        // Enable blending and draw the window shadow and frame
        RenderSystem.enableBlend();
        drawWindowShadow(graphics, offsetX, offsetY);
        drawWindowFrame(graphics, offsetX, offsetY);

        // Draw the tabs if there's more than one
        if (tabs.size() > 1) {
            for (AdvancementTab tab : tabs.values()) {
                if (tab.getPage() == tabPage) {
                    tab.drawTab(graphics, offsetX, offsetY, tab == selectedTab);
                    tab.drawIcon(graphics, offsetX, offsetY);
                }
            }
        }

        // Draw the title of the selected tab if it exists, otherwise draw the screen title
        graphics.drawString(
                font, selectedTab != null ? selectedTab.getTitle() : title,
                offsetX + 8, offsetY + 6, 4210752, false
        );

        return true;
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

    // Returns whether the window is expanded
    public static boolean isExpanded() {
        return windowWidth != VANILLA_WINDOW_WIDTH || windowHeight != VANILLA_WINDOW_HEIGHT;
    }

    // Returns how many horizontal tabs can fit on the window
    public static int horizontalTabCapacity() {
        return (windowWidth + 4) / HORIZONTAL_TAB_STEP;
    }

    // Returns how many vertical tabs can fit on the window
    public static int verticalTabCapacity() {
        return windowHeight / VERTICAL_TAB_STEP;
    }

    // Returns whether the window size has changed
    public static boolean sizeChanged(int previousWidth, int previousHeight) {
        return insideWidth() != previousWidth || insideHeight() != previousHeight;
    }

    // Returns how many tabs can fit on a single page
    public static int tabsPerPage() {
        return 2 * (horizontalTabCapacity() + verticalTabCapacity());
    }

    // Returns how many tabs can fit on the window based on the type
    public static int tabCapacity(Enum<?> tabType) {
        if (isHorizontalTabType(tabType)) {
            return horizontalTabCapacity();
        } else {
            return verticalTabCapacity();
        }
    }

    // Returns whether the last tab reaches the edge of the window
    public static boolean lastTabReachesEdge(Enum<?> tabType) {
        int capacity = tabCapacity(tabType);

        if (isHorizontalTabType(tabType)) {
            return (capacity - 1) * HORIZONTAL_TAB_STEP + VERTICAL_TAB_STEP == windowWidth();
        } else {
            return capacity * VERTICAL_TAB_STEP == windowHeight();
        }
    }

    // Returns whether the tab type is horizontal
    private static boolean isHorizontalTabType(Enum<?> tabType) {
        return switch (tabType.name()) {
            case "ABOVE", "BELOW" -> true;
            case "LEFT", "RIGHT" -> false;
            default -> throw new IllegalArgumentException("Unknown advancement tab type: " + tabType);
        };
    }

    // Draws the window shadow
    private static void drawWindowShadow(GuiGraphics graphics, int x, int y) {
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
    private static void drawWindowFrame(GuiGraphics graphics, int x, int y) {
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
            GuiGraphics graphics, int x, int y, int width, int height,
            int textureX, int textureY, int textureWidth, int textureHeight
    ) {
        graphics.blit(
                WINDOW_TEXTURE, x, y, width, height,
                textureX, textureY, textureWidth, textureHeight, 256, 256
        );
    }
}