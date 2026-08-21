package com.fooze.improvedadvancements;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_SORTING = BUILDER
            .comment("Whether to enable sorting of advancement tabs")
            .define("enableSorting", true);

    public static final ModConfigSpec.BooleanValue EXPAND_SCREEN = BUILDER
            .comment("Whether to expand the advancement screen to fill more space")
            .define("expandScreen", true);

    public static final ModConfigSpec.IntValue EXPAND_AMOUNT = BUILDER
            .comment("The percentage of screen space for the advancement screen to expand to")
            .defineInRange("expandAmount", 100, 0, 100);

    public static final ModConfigSpec.BooleanValue SHOW_CRITERIA = BUILDER
            .comment("Whether to show the advancement criteria progress in the tooltip")
            .define("showCriteria", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}