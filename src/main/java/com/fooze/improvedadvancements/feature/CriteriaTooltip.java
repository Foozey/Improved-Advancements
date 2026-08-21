package com.fooze.improvedadvancements.feature;

import com.fooze.improvedadvancements.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.*;

public final class CriteriaTooltip {
    // Background texture for the tooltip body
    private static final ResourceLocation TITLE_BOX =
            ResourceLocation.withDefaultNamespace("advancements/title_box");

    // Progress box texture for completed advancements
    private static final ResourceLocation BOX_OBTAINED =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/advancements/box_obtained.png");

    // Progress box texture for incomplete advancements
    private static final ResourceLocation BOX_UNOBTAINED =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/advancements/box_unobtained.png");

    private CriteriaTooltip() {}

    // Stores the textures and width needed to draw the progress bar
    private record ProgressStyle(
            AdvancementWidgetType completedType,
            AdvancementWidgetType remainingType,
            AdvancementWidgetType frameType,
            int completedWidth
    ) {}

    // Returns a list of criteria that will be displayed in the tooltip
    public static List<FormattedCharSequence> criteriaList(
            AdvancementNode advancementNode, @Nullable AdvancementProgress progress, Minecraft minecraft
    ) {
        // Only show criteria if the config option is enabled and criteria exists
        if (!Config.SHOW_CRITERIA.get() || progress == null) {
            return List.of();
        }

        // Store the criteria and their names
        Set<String> names = new TreeSet<>();
        Set<String> criteria = new TreeSet<>();

        // Add both completed and remaining criteria names
        progress.getCompletedCriteria().forEach(names::add);
        progress.getRemainingCriteria().forEach(names::add);

        // Only keep criteria that must be completed individually
        for (List<String> requirement : advancementNode.advancement().requirements().requirements()) {
            if (requirement.size() == 1) {
                criteria.add(requirement.getFirst());
            }
        }

        // Only keep criteria from the advancement's criteria list
        names.retainAll(criteria);

        // Only show a list when there is multiple criteria
        if (names.size() <= 1) {
            return List.of();
        }

        // Create the criteria list
        List<FormattedCharSequence> list = new ArrayList<>(names.size());

        // Add a colored status icon for each criterion
        for (String name : names) {
            CriterionProgress criterion = progress.getCriterion(name);
            boolean complete = criterion != null && criterion.isDone();

            // If the criterion is complete, show a green tick, otherwise show a red cross
            Component icon = Component.literal(complete ? "✔ " : "✘ ")
                    .withStyle(complete ? ChatFormatting.GREEN : ChatFormatting.RED)
                    .append(Component.literal(formatCriteria(name)).withStyle(ChatFormatting.GRAY));
            list.add(Language.getInstance().getVisualOrder(icon));
        }

        return list;
    }

    // Renders the tooltip for the advancement
    public static void render(
            GuiGraphics guiGraphics,
            AdvancementTab advancementTab,
            AdvancementNode advancementNode,
            DisplayInfo display,
            FormattedCharSequence title,
            int width,
            Minecraft minecraft,
            @Nullable AdvancementProgress progress,
            int widgetX,
            int widgetY,
            int x,
            int y,
            int screenOffsetX,
            int screenOffsetY,
            List<FormattedCharSequence> criteria
    ) {
        // Store the description text
        List<FormattedCharSequence> description = descriptionList(display);

        // Calculate the criteria display metrics and screen bounds
        boolean showCriteria = !criteria.isEmpty();
        int criteriaWidth = criteria.stream().mapToInt(minecraft.font::width).max().orElse(0);
        int screenX = screenOffsetX + 9 + x + widgetX;
        int screenY = screenOffsetY + 18 + y + widgetY;
        int screenWidth = advancementTab.getScreen().width;
        int screenHeight = advancementTab.getScreen().height;

        // Calculate the screen space above and below the advancement to determine placement
        int belowRows = Math.max(0, (screenHeight - screenY - 32) / 9);
        int aboveRows = Math.max(0, (screenY - 6) / 9);
        boolean drawAbove = aboveRows > belowRows;

        // Calculate the multi-column criteria layout
        int maxRows = Math.max(1, Math.max(aboveRows, belowRows) - description.size() - (showCriteria ? 2 : 0));
        int columns = showCriteria ? (criteria.size() + maxRows - 1) / maxRows : 0;
        int criteriaRows = showCriteria ? (criteria.size() + columns - 1) / columns : 0;
        int columnWidth = criteriaWidth + 10;

        // Calculate the tooltip dimensions
        int descriptionWidth = description.stream().mapToInt(minecraft.font::width).max().orElse(0);
        int tooltipWidth = Math.max(width, Math.max(showCriteria ? columns * columnWidth : 0, descriptionWidth + 10));

        // Calculate the screen space left and right of the advancement to determine placement
        int rightSpace = screenWidth - screenX;
        int leftSpace = screenX + 32;
        boolean drawLeft = tooltipWidth > rightSpace && (tooltipWidth <= leftSpace || leftSpace > rightSpace);

        // Calculate the progress bar dimensions and style
        Component progressText = progress == null ? null : progress.getProgressText();
        int progressWidth = progressText == null ? 0 : minecraft.font.width(progressText);
        ProgressStyle progressStyle = progressStyle(progress, tooltipWidth);
        RenderSystem.enableBlend();

        // Calculate the tooltip positioning
        int contentLines = description.size() + (showCriteria ? 2 + criteriaRows : 0);
        int tooltipY = y + widgetY;
        int tooltipX = drawLeft ? x + widgetX - tooltipWidth + 26 + 6 : x + widgetX;
        int tooltipHeight = 32 + contentLines * 9;

        // Draw the tooltip above or below the advancement depending on space
        if (drawAbove) {
            guiGraphics.blitSprite(
                    TITLE_BOX,
                    tooltipX,
                    tooltipY + 26 - tooltipHeight,
                    tooltipWidth,
                    tooltipHeight
            );
        } else {
            guiGraphics.blitSprite(
                    TITLE_BOX,
                    tooltipX,
                    tooltipY,
                    tooltipWidth,
                    tooltipHeight
            );
        }

        // Draw the progress remaining bar
        drawProgressBox(
                guiGraphics,
                progressStyle.remainingType(),
                tooltipX,
                tooltipY,
                tooltipWidth
        );

        // Overlay the progress completed bar
        drawProgressFill(
                guiGraphics,
                progressStyle.completedType(),
                tooltipX,
                tooltipY,
                progressStyle.completedWidth()
        );

        // Draw the advancement frame over the progress bar
        guiGraphics.blitSprite(
                progressStyle.frameType().frameSprite(display.getType()),
                x + widgetX + 3,
                y + widgetY,
                26,
                26
        );

        // Draw the title and progress text depending on the tooltip position
        if (drawLeft) {
            guiGraphics.drawString(
                    minecraft.font,
                    title,
                    tooltipX + 5,
                    tooltipY + 9,
                    -1
            );

            if (progressText != null) {
                guiGraphics.drawString(
                        minecraft.font,
                        progressText,
                        x + widgetX - progressWidth,
                        tooltipY + 9,
                        -1
                );
            }
        } else {
            guiGraphics.drawString(
                    minecraft.font,
                    title,
                    tooltipX + 32,
                    tooltipY + 9,
                    -1
            );

            if (progressText != null) {
                guiGraphics.drawString(
                        minecraft.font,
                        progressText,
                        tooltipX + tooltipWidth - progressWidth - 5,
                        tooltipY + 9,
                        -1
                );
            }
        }

        // Draw content above or below the title depending on space
        int contentY = drawAbove ? tooltipY + 26 - tooltipHeight + 7 : tooltipY + 26;

        // Draw the advancement description
        for (int index = 0; index < description.size(); index++) {
            guiGraphics.drawString(
                    minecraft.font,
                    description.get(index),
                    tooltipX + 5,
                    contentY + index * 9,
                    -5592406,
                    false
            );
        }

        // Draw criteria across multiple columns depending on space
        if (showCriteria) {
            guiGraphics.drawString(
                    minecraft.font,
                    Component.literal("Criteria:"),
                    tooltipX + 5, contentY + (description.size() + 1) * 9,
                    -1,
                    false
            );

            int column = 0;
            int row = 0;
            int criteriaPerColumn = criteria.size() / columns;
            int columnsWithExtraCriteria = criteria.size() % columns;

            // Draw criteria at the calculated column and row position
            for (FormattedCharSequence criterion : criteria) {
                guiGraphics.drawString(
                        minecraft.font,
                        criterion,
                        tooltipX + 5 + column * columnWidth,
                        contentY + (description.size() + 2 + row) * 9,
                        -1,
                        false
                );

                row++;

                // Add criteria to a new column when the column is full
                if (row == criteriaPerColumn + (column < columnsWithExtraCriteria ? 1 : 0)) {
                    column++;
                    row = 0;
                }
            }
        }

        // Draw the advancement icon in the tooltip title
        guiGraphics.renderFakeItem(display.getIcon(), x + widgetX + 8, y + widgetY + 5);
    }

    // Sets the progress bar style based on the percentage completed
    private static ProgressStyle progressStyle(@Nullable AdvancementProgress progress, int width) {
        float percent = progress == null ? 0.0F : progress.getPercent();
        int completedWidth = Mth.floor(percent * (float)width);

        if (percent >= 1.0F) {
            return new ProgressStyle(
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.OBTAINED,
                    width / 2
            );
        }

        if (completedWidth < 2) {
            return new ProgressStyle(
                    AdvancementWidgetType.UNOBTAINED,
                    AdvancementWidgetType.UNOBTAINED,
                    AdvancementWidgetType.UNOBTAINED,
                    width / 2
            );
        }

        if (completedWidth > width - 2) {
            return new ProgressStyle(
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.UNOBTAINED,
                    width / 2
            );
        }

        return new ProgressStyle(
                AdvancementWidgetType.OBTAINED,
                AdvancementWidgetType.UNOBTAINED,
                AdvancementWidgetType.UNOBTAINED,
                completedWidth
        );
    }

    // Formats criteria keys into readable names
    private static String formatCriteria(String name) {

        // Omit the namespace
        int separator = name.indexOf(':');

        if (separator >= 0) {
            name = name.substring(separator + 1);
        }

        // Remove symbols
        String[] words = name.replace('_', ' ').replace('-', ' ').trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        // Add a space between each word and convert to title case
        for (String word : words) {
            if (!word.isEmpty()) {
                if (!result.isEmpty()) {
                    result.append(' ');
                }

                result.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
                result.append(word.substring(1));
            }
        }
        return result.toString();
    }

    // Makes the description 12-words long before moving to a new line
    private static List<FormattedCharSequence> descriptionList(DisplayInfo display) {
        String text = display.getDescription().getString().trim();

        if (text.isEmpty()) {
            return List.of();
        }

        String[] words = text.split("\\s+");
        List<FormattedCharSequence> list = new ArrayList<>((words.length + 11) / 12);

        for (int start = 0; start < words.length; start += 12) {
            int end = Math.min(start + 12, words.length);

            Component line = Component.literal(String.join(" ", java.util.Arrays.copyOfRange(words, start, end)))
                    .withStyle(display.getType().getChatColor());

            list.add(Language.getInstance().getVisualOrder(line));
        }
        return list;
    }

    // Draws the full progress bar using its three-part texture
    private static void drawProgressBox(
            GuiGraphics guiGraphics,
            AdvancementWidgetType type,
            int x,
            int y,
            int width
    ) {
        ResourceLocation texture = boxTexture(type);
        int centerWidth = width - 2 * 2;

        guiGraphics.blit(
                texture,
                x,
                y,
                2,
                26,
                0,
                0,
                2,
                26,
                200,
                26
        );

        guiGraphics.blit(
                texture,
                x + 2,
                y,
                centerWidth,
                26,
                2,
                0,
                200 - 2 * 2,
                26,
                200,
                26
        );

        guiGraphics.blit(
                texture,
                x + width - 2,
                y,
                2,
                26,
                200 - 2,
                0,
                2,
                26,
                200,
                26
        );
    }

    // Draws the completed portion of the progress bar
    private static void drawProgressFill(
            GuiGraphics guiGraphics,
            AdvancementWidgetType type,
            int x,
            int y,
            int width
    ) {
        if (width <= 0) {
            return;
        }

        ResourceLocation texture = boxTexture(type);
        int leftBorderWidth = Math.min(width, 2);

        guiGraphics.blit(
                texture,
                x,
                y,
                leftBorderWidth,
                26,
                0,
                0,
                leftBorderWidth,
                26,
                200,
                26
        );

        if (width > 2) {
            guiGraphics.blit(
                    texture,
                    x + 2,
                    y,
                    width - 2,
                    26,
                    2,
                    0,
                    200 - 2 * 2,
                    26,
                    200,
                    26
            );
        }
    }

    // Returns the texture matching the advancement's completion state
    private static ResourceLocation boxTexture(AdvancementWidgetType type) {
        if (type == AdvancementWidgetType.OBTAINED) {
            return BOX_OBTAINED;
        } else {
            return BOX_UNOBTAINED;
        }
    }
}