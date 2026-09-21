package com.fooze.improvedadvancements.feature.tooltip;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class Description {
    public static final int MAX_WORDS = 8;

    // Wraps the description at 8 words or the available width
    public static List<FormattedCharSequence> lines(
            DisplayInfo display, Minecraft minecraft, int width
    ) {
        // Get the description text
        String text = display.description().getString().trim();

        // Don't create additional lines if there's no description
        if (text.isEmpty()) {
            return List.of();
        }

        // Split lines by word
        String[] words = text.split("\\s+");

        // Use a max number of words per line
        List<FormattedCharSequence> lines = new ArrayList<>(
                (words.length + (MAX_WORDS - 1)) / MAX_WORDS
        );

        StringBuilder line = new StringBuilder();
        int wordCount = 0;

        // Build wrapped lines from each word
        for (String word : words) {
            String candidate = word;

            // Add the next word to the current line
            if (!line.isEmpty()) {
                candidate = line + " " + word;
            }

            // Start a new line when the word limit or width is reached
            if (!line.isEmpty() && (
                    wordCount == MAX_WORDS || minecraft.font.width(candidate) > width)
            ) {
                lines.add(Language.getInstance().getVisualOrder(
                        Component.literal(line.toString()).withStyle(
                                display.type().getChatColor()
                        )
                ));

                line.setLength(0);
                wordCount = 0;
            }

            // Add spacing between words
            if (!line.isEmpty()) {
                line.append(' ');
            }

            line.append(word);
            wordCount = wordCount + 1;
        }

        // Add any remaining words to the final line
        if (!line.isEmpty()) {
            lines.add(Language.getInstance().getVisualOrder(
                    Component.literal(line.toString()).withStyle(
                            display.type().getChatColor()
                    )
            ));
        }

        return lines;
    }
}