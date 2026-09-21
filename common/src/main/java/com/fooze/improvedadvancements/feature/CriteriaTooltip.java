package com.fooze.improvedadvancements.feature;

import com.fooze.improvedadvancements.Config;
import com.fooze.improvedadvancements.feature.tooltip.Criteria;
import com.fooze.improvedadvancements.feature.tooltip.Description;
import com.fooze.improvedadvancements.feature.tooltip.Progress;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class CriteriaTooltip {
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    private static final int ADVANCEMENT_SIZE = 32;
    private static final int TOOLTIP_MARGIN = 6;
    private static final int TOOLTIP_HEIGHT = 32;
    private static final int TOOLTIP_TITLE_HEIGHT = 26;
    private static final int TOOLTIP_CONTENT_OFFSET = 7;
    private static final int TITLE_OFFSET_Y = 9;
    private static final int TITLE_OFFSET_LEFT = 5;
    private static final int TITLE_COLOR = -1;
    private static final int ICON_OFFSET_X = 8;
    private static final int ICON_OFFSET_Y = 5;
    private static final int DESCRIPTION_PADDING = 10;
    private static final int DESCRIPTION_COLOR = -5592406;
    private static final int CRITERIA_HEADER_LINES = 2;
    private static final int LINE_HEIGHT = 9;
    private static final int FRAME_OFFSET_X = 3;

    // Background texture for the tooltip body
    private static final Identifier TITLE_BOX = Identifier.withDefaultNamespace(
            "advancements/title_box"
    );

    // Renders the tooltip with criteria
    public static boolean render(
            Minecraft minecraft, GuiGraphicsExtractor graphics,
            DisplayInfo display, FormattedCharSequence title,
            @Nullable AdvancementProgress progress, AdvancementNode node,
            int screenWidth, int screenHeight, int width, int x, int y,
            int offsetX, int offsetY, int widgetX, int widgetY
    ) {
        // Only render if the config option is enabled
        if (!Config.get().showCriteria()) {
            return false;
        }

        // Get the criteria
        List<FormattedCharSequence> criteria = Criteria.criteriaList(progress, node);

        // Calculate the advancement position on the screen
        int screenX = offsetX + WINDOW_INSIDE_X + x + widgetX;
        int screenY = offsetY + WINDOW_INSIDE_Y + y + widgetY;

        // Calculate the space above and below the advancement
        int topSpace = Math.max(0, (screenY - TOOLTIP_MARGIN) / LINE_HEIGHT);
        int bottomSpace = Math.max(0, (screenHeight - screenY - ADVANCEMENT_SIZE) / LINE_HEIGHT);
        boolean drawAbove = topSpace > bottomSpace;

        // Get the advancement progress text
        Component progressText = Progress.text(progress);
        int progressWidth = 0;

        // Calculate the progress text width
        if (progressText != null) {
            progressWidth = minecraft.font.width(progressText);
        }

        // Calculate the space available on either side of the advancement
        int leftSpace = screenX + ADVANCEMENT_SIZE;
        int rightSpace = screenWidth - screenX;

        // Calculate the description
        int descriptionSpace = Math.max(rightSpace, leftSpace);
        int maxDescriptionWidth = Math.max(1, descriptionSpace - DESCRIPTION_PADDING);
        List<FormattedCharSequence> description = Description.lines(display, minecraft, maxDescriptionWidth);
        int descriptionWidth = description.stream().mapToInt(minecraft.font::width).max().orElse(0);

        // Calculate the space available for the criteria
        int criteriaSpace = Math.max(1, Math.max(topSpace, bottomSpace) - description.size());

        // Only include lines for the criteria header when criteria exists
        if (!criteria.isEmpty()) {
            criteriaSpace = Math.max(1, criteriaSpace - CRITERIA_HEADER_LINES);
        }

        // Get the criteria layout
        Criteria.Layout criteriaLayout = Criteria.layout(criteria, criteriaSpace, minecraft);
        int criteriaWidth = 0;
        int criteriaLines = 0;

        // Calculate the criteria width and lines
        if (criteriaLayout.isVisible()) {
            criteriaWidth = criteriaLayout.columns() * criteriaLayout.columnWidth();
            criteriaLines = CRITERIA_HEADER_LINES + criteriaLayout.rows();
        }

        // Calculate the tooltip position and width
        int tooltipX = x + widgetX;
        int tooltipY = y + widgetY;
        int tooltipWidth = Math.max(width, Math.max(criteriaWidth, descriptionWidth + DESCRIPTION_PADDING));
        boolean drawLeft = tooltipWidth > rightSpace && (tooltipWidth <= leftSpace || leftSpace > rightSpace);

        // Calculate the tooltip position when drawn to the left
        if (drawLeft) {
            tooltipX = x + widgetX - tooltipWidth + TOOLTIP_TITLE_HEIGHT + TOOLTIP_MARGIN;
        }

        // Calculate the tooltip height and content Y position
        int lines = description.size() + criteriaLines;
        int tooltipHeight = TOOLTIP_HEIGHT + lines * LINE_HEIGHT;
        int contentY = tooltipY + TOOLTIP_TITLE_HEIGHT;

        // Calculate the tooltip content Y position when drawn above
        if (drawAbove) {
            contentY = tooltipY + TOOLTIP_TITLE_HEIGHT - tooltipHeight + TOOLTIP_CONTENT_OFFSET;
        }

        // Draw the tooltip above or below the advancement depending on space
        if (drawAbove) {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED, TITLE_BOX,
                    tooltipX, tooltipY + TOOLTIP_TITLE_HEIGHT - tooltipHeight, tooltipWidth, tooltipHeight
            );
        } else {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED, TITLE_BOX,
                    tooltipX, tooltipY, tooltipWidth, tooltipHeight
            );
        }

        // Render the progress bar
        Progress.render(
                graphics, display, progress,
                tooltipX, tooltipY, tooltipWidth,
                x + widgetX + FRAME_OFFSET_X, y + widgetY
        );

        // Draw the title and progress text depending on the tooltip position
        if (drawLeft) {
            graphics.text(
                    minecraft.font, title,
                    tooltipX + TITLE_OFFSET_LEFT,
                    tooltipY + TITLE_OFFSET_Y,
                    TITLE_COLOR
            );

            if (progressText != null) {
                graphics.text(
                        minecraft.font, progressText,
                        x + widgetX - progressWidth,
                        tooltipY + TITLE_OFFSET_Y,
                        TITLE_COLOR
                );
            }
        } else {
            graphics.text(
                    minecraft.font, title,
                    tooltipX + ADVANCEMENT_SIZE,
                    tooltipY + TITLE_OFFSET_Y,
                    TITLE_COLOR
            );

            if (progressText != null) {
                graphics.text(
                        minecraft.font, progressText,
                        tooltipX + tooltipWidth - progressWidth - TITLE_OFFSET_LEFT,
                        tooltipY + TITLE_OFFSET_Y,
                        TITLE_COLOR
                );
            }
        }

        // Draw the description
        for (int index = 0; index < description.size(); index = index + 1) {
            graphics.text(
                    minecraft.font, description.get(index),
                    tooltipX + TITLE_OFFSET_LEFT,
                    contentY + index * LINE_HEIGHT,
                    DESCRIPTION_COLOR, false
            );
        }

        // Render the criteria tooltip
        Criteria.render(
                graphics, minecraft, criteriaLayout,
                tooltipX, contentY, description.size()
        );

        // Render the icon
        graphics.fakeItem(
                display.icon().create(),
                x + widgetX + ICON_OFFSET_X,
                y + widgetY + ICON_OFFSET_Y
        );

        return true;
    }
}