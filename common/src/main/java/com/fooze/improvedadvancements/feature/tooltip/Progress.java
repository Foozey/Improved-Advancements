package com.fooze.improvedadvancements.feature.tooltip;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class Progress {
    private static final int WIDTH = 26;
    private static final int HEIGHT = 26;
    private static final int MIN_COMPLETED_WIDTH = 200;
    private static final int BORDER_WIDTH = 2;

    // Stores the textures and width needed to draw the progress bar
    private record Style(
            AdvancementWidgetType completedType,
            AdvancementWidgetType remainingType,
            AdvancementWidgetType frameType,
            int completedWidth
    ) {
    }

    // Draws the progress bar and advancement frame
    public static void render(
            GuiGraphicsExtractor graphics, DisplayInfo display,
            @Nullable AdvancementProgress progress,
            int tooltipX, int tooltipY, int tooltipWidth, int frameX, int frameY
    ) {
        Style style = style(progress, tooltipWidth);

        // Draw the remaining progress bar
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                style.remainingType().boxSprite(),
                tooltipX, tooltipY,
                tooltipWidth, HEIGHT
        );

        // Draw the completed progress bar
        if (style.completedWidth() > 0) {
            graphics.enableScissor(
                    tooltipX, tooltipY,
                    tooltipX + style.completedWidth(), tooltipY + HEIGHT
            );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    style.completedType().boxSprite(),
                    tooltipX, tooltipY,
                    Math.max(style.completedWidth(), MIN_COMPLETED_WIDTH), HEIGHT
            );

            graphics.disableScissor();
        }

        // Draw the progress bar frame
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                style.frameType().frameSprite(display.type()),
                frameX, frameY,
                WIDTH, HEIGHT
        );
    }

    // Returns the progress text when the advancement has progress
    @Nullable
    public static Component text(@Nullable AdvancementProgress progress) {
        if (progress == null) {
            return null;
        }

        return progress.getProgressText();
    }

    // Sets the progress bar style based on the percentage completed
    private static Style style(@Nullable AdvancementProgress progress, int width) {
        float percent;

        // Only get the progress percentage if progress has been made
        if (progress == null) {
            percent = 0.0F;
        } else {
            percent = progress.getPercent();
        }

        int completedWidth = Mth.floor(percent * (float) width);

        // Use the obtained style when the advancement is complete
        if (percent >= 1.0F) {
            return new Style(
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.OBTAINED,
                    width / 2
            );
        }

        // Use the unobtained style when there is no progress
        if (completedWidth < BORDER_WIDTH) {
            return new Style(
                    AdvancementWidgetType.UNOBTAINED,
                    AdvancementWidgetType.UNOBTAINED,
                    AdvancementWidgetType.UNOBTAINED,
                    width / 2
            );
        }

        // Use the obtained style when only the ending border remains
        if (completedWidth > width - BORDER_WIDTH) {
            return new Style(
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.OBTAINED,
                    AdvancementWidgetType.UNOBTAINED,
                    width / 2
            );
        }

        // Use the standard style for partially completed advancements
        return new Style(
                AdvancementWidgetType.OBTAINED,
                AdvancementWidgetType.UNOBTAINED,
                AdvancementWidgetType.UNOBTAINED,
                completedWidth
        );
    }
}