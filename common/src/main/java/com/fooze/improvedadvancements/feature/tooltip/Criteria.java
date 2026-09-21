package com.fooze.improvedadvancements.feature.tooltip;

import com.fooze.improvedadvancements.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class Criteria {
    private static final String CRITERIA_TITLE = "Criteria:";
    private static final String COMPLETE_ICON = "✔ ";
    private static final String INCOMPLETE_ICON = "✘ ";
    private static final int PADDING = 10;
    private static final int TEXT_OFFSET_X = 5;
    private static final int TEXT_COLOR = -1;
    private static final int LINE_HEIGHT = 9;
    private static final int TITLE_LINE_OFFSET = 1;
    private static final int CRITERIA_LINE_OFFSET = 2;

    // Stores the layout needed to draw criteria in the tooltip
    public record Layout(
            List<FormattedCharSequence> criteria, int columns, int rows, int columnWidth
    ) {
        public boolean isVisible() {
            return !this.criteria.isEmpty();
        }
    }

    // Returns a list of criteria that will be displayed in the tooltip
    public static List<FormattedCharSequence> criteriaList(
            @Nullable AdvancementProgress progress, AdvancementNode node
    ) {
        // Only add criteria to the list if the config option is enabled and criteria exists
        if (!Config.get().showCriteria() || progress == null) {
            return List.of();
        }

        // Store the criteria names
        Set<String> names = new TreeSet<>();
        progress.getCompletedCriteria().forEach(names::add);
        progress.getRemainingCriteria().forEach(names::add);

        // Only keep criteria that must be completed individually
        names.removeIf(name ->
                node.advancement().requirements().requirements().stream().noneMatch(requirement ->
                        requirement.size() == 1 && requirement.getFirst().equals(name)
                )
        );

        // Only use the criteria list when there is multiple criteria
        if (names.size() <= 1) {
            return List.of();
        }

        // Create the criteria list
        List<FormattedCharSequence> list = new ArrayList<>(names.size());

        // Add a colored status icon for each criterion
        for (String name : names) {
            CriterionProgress criterion = progress.getCriterion(name);
            MutableComponent icon;

            // If the criterion is complete, show a green tick, otherwise show a red cross
            if (criterion != null && criterion.isDone()) {
                icon = Component.literal(COMPLETE_ICON).withStyle(ChatFormatting.GREEN);
            } else {
                icon = Component.literal(INCOMPLETE_ICON).withStyle(ChatFormatting.RED);
            }

            icon.append(Component.literal(formatCriteria(name)).withStyle(ChatFormatting.GRAY));
            list.add(Language.getInstance().getVisualOrder(icon));
        }

        return list;
    }

    // Calculates the layout used to draw criteria
    public static Layout layout(
            List<FormattedCharSequence> criteria, int maxRows, Minecraft minecraft
    ) {
        if (criteria.isEmpty()) {
            return new Layout(criteria, 0, 0, 0);
        }

        int columns = (criteria.size() + maxRows - 1) / maxRows;
        int rows = (criteria.size() + columns - 1) / columns;
        int width = criteria.stream().mapToInt(minecraft.font::width).max().orElse(0) + PADDING;
        return new Layout(criteria, columns, rows, width);
    }

    // Draws criteria across multiple columns depending on space
    public static void render(
            GuiGraphicsExtractor graphics, Minecraft minecraft, Layout layout,
            int x, int y, int descriptionLines
    ) {
        // Don't draw criteria if the layout isn't visible
        if (!layout.isVisible()) {
            return;
        }

        // Draw the criteria text
        graphics.text(
                minecraft.font, Component.literal(CRITERIA_TITLE),
                x + TEXT_OFFSET_X,
                y + (descriptionLines + TITLE_LINE_OFFSET) * LINE_HEIGHT,
                TEXT_COLOR, false
        );

        // Calculate the criteria columns
        int column = 0;
        int row = 0;
        int criteriaPerColumn = layout.criteria().size() / layout.columns();
        int columnsWithExtraCriteria = layout.criteria().size() % layout.columns();

        // Draw criteria at the calculated column and row position
        for (FormattedCharSequence criterion : layout.criteria()) {
            // Draw criteria within the current column and row
            graphics.text(
                    minecraft.font, criterion,
                    x + TEXT_OFFSET_X + column * layout.columnWidth(),
                    y + (descriptionLines + CRITERIA_LINE_OFFSET + row) * LINE_HEIGHT,
                    TEXT_COLOR, false
            );

            // Move to the next row
            row = row + 1;
            int extraCriteria = 0;

            // Add criteria to the previous column when the criteria cannot be divided evenly
            if (column < columnsWithExtraCriteria) {
                extraCriteria = 1;
            }

            // Move to the next column after filling the current column
            if (row == criteriaPerColumn + extraCriteria) {
                column = column + 1;
                row = 0;
            }
        }
    }

    // Formats criteria keys into readable names
    private static String formatCriteria(String name) {
        int separator = name.indexOf(':');
        StringBuilder result = new StringBuilder();

        // Omit the namespace
        if (separator >= 0) {
            name = name.substring(separator + 1);
        }

        // Remove symbols
        String[] words = name.replace('_', ' ').replace('-', ' ').trim().split("\\s+");

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
}