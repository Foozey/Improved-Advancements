package com.fooze.improvedadvancements;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ENABLE_SORTING = BUILDER.define("enableSorting", true);
    public static final ModConfigSpec.BooleanValue EXPAND_SCREEN = BUILDER.define("expandScreen", true);
    public static final ModConfigSpec.IntValue EXPAND_AMOUNT = BUILDER.defineInRange("expandAmount", 100, 0, 100);
    public static final ModConfigSpec.BooleanValue SHOW_CRITERIA = BUILDER.define("showCriteria", true);
    static final ModConfigSpec SPEC = BUILDER.build();
}