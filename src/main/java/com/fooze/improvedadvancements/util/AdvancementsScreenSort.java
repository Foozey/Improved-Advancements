package com.fooze.improvedadvancements.util;

import com.fooze.improvedadvancements.Config;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class AdvancementsScreenSort {
    // Vanilla tab order
    private static final Map<ResourceLocation, Integer> VANILLA_TAB_ORDER = Map.of(
            ResourceLocation.withDefaultNamespace("story/root"), 0,
            ResourceLocation.withDefaultNamespace("adventure/root"), 1,
            ResourceLocation.withDefaultNamespace("husbandry/root"), 2,
            ResourceLocation.withDefaultNamespace("nether/root"), 3,
            ResourceLocation.withDefaultNamespace("end/root"), 4
    );

    // Comparator for sorting advancement tabs by vanilla tab order, then name, then ID
    private static final Comparator<AdvancementNode> TAB_COMPARATOR = Comparator
            .comparingInt(AdvancementsScreenSort::vanillaOrder)
            .thenComparing(AdvancementsScreenSort::tabName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(AdvancementsScreenSort::tabName)
            .thenComparing(node -> node.holder().id().toString());

    // Sorts the advancement tabs in ascending alphabetical order
    public static void sort(Iterable<AdvancementNode> roots) {
        // Only sort if sorting is enabled and the roots are in a set
        if (!Config.ENABLE_SORTING.get() || !(roots instanceof Set<?>)) {
            return;
        }

        // Cast the roots to a set and sort it
        Set<AdvancementNode> rootSet = (Set<AdvancementNode>) roots;
        List<AdvancementNode> sortedRoots = new ArrayList<>(rootSet);
        sortedRoots.sort(TAB_COMPARATOR);
        rootSet.clear();
        rootSet.addAll(sortedRoots);
    }

    // Returns the vanilla tab order of the advancement tab
    private static int vanillaOrder(AdvancementNode node) {
        return VANILLA_TAB_ORDER.getOrDefault(node.holder().id(), Integer.MAX_VALUE);
    }

    // Returns the name of the advancement tab
    private static String tabName(AdvancementNode node) {
        return node.advancement().display()
                .map(DisplayInfo::getTitle)
                .map(Component::getString)
                .orElse("");
    }
}