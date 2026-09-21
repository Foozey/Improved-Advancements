package com.fooze.improvedadvancements.feature;

import com.fooze.improvedadvancements.Config;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SortTabs {
    // Vanilla tab order
    private static final Map<Identifier, Integer> VANILLA_TAB_ORDER = Map.of(
            Identifier.withDefaultNamespace("story/root"), 0,
            Identifier.withDefaultNamespace("adventure/root"), 1,
            Identifier.withDefaultNamespace("husbandry/root"), 2,
            Identifier.withDefaultNamespace("nether/root"), 3,
            Identifier.withDefaultNamespace("end/root"), 4
    );

    // Comparator for sorting advancement tabs by vanilla tab order, then name, then ID
    private static final Comparator<AdvancementNode> TAB_COMPARATOR = Comparator
            .comparingInt(SortTabs::vanillaOrder)
            .thenComparing(SortTabs::tabName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(SortTabs::tabName)
            .thenComparing(node -> node.holder().id().toString());

    // Sorts the advancement tabs in ascending alphabetical order
    public static Iterable<AdvancementNode> sort(Iterable<AdvancementNode> roots) {
        // Only sort if the config option is enabled
        if (!Config.get().enableSorting()) {
            return roots;
        }

        // Add the roots to a list and sort it
        List<AdvancementNode> sortedRoots = new ArrayList<>();
        roots.forEach(sortedRoots::add);
        sortedRoots.sort(TAB_COMPARATOR);
        return sortedRoots;
    }

    // Returns the vanilla tab order of the advancement tab
    private static int vanillaOrder(AdvancementNode node) {
        return VANILLA_TAB_ORDER.getOrDefault(node.holder().id(), Integer.MAX_VALUE);
    }

    // Returns the name of the advancement tab
    private static String tabName(AdvancementNode node) {
        return node.advancement().display().map(DisplayInfo::title).map(Component::getString).orElse("");
    }
}