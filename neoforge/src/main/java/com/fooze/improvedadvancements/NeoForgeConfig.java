package com.fooze.improvedadvancements;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeConfig extends Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SORTING = BUILDER.define("enableSorting", true);
    private static final ModConfigSpec.ConfigValue<Boolean> EXPAND_SCREEN = BUILDER.define("expandScreen", true);
    private static final ModConfigSpec.ConfigValue<Integer> EXPAND_AMOUNT = BUILDER.defineInRange("expandAmount", 100, 0, 100);
    private static final ModConfigSpec.ConfigValue<Boolean> SHOW_CRITERIA = BUILDER.define("showCriteria", true);
    public static final ModConfigSpec SPEC = BUILDER.build();

    @Override
    public boolean enableSorting() {
        return ENABLE_SORTING.get();
    }

    @Override
    public boolean expandScreen() {
        return EXPAND_SCREEN.get();
    }

    @Override
    public int expandAmount() {
        return EXPAND_AMOUNT.get();
    }

    @Override
    public boolean showCriteria() {
        return SHOW_CRITERIA.get();
    }
}