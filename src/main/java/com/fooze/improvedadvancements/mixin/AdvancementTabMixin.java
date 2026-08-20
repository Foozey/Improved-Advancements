package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AdvancementTab.class)
abstract class AdvancementTabMixin {
    // Replaces the inside width with the expanded value
    @ModifyConstant(method = {"drawContents", "drawTooltips", "scroll"}, constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return AdvancementsScreenExpand.insideWidth();
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = {"drawContents", "drawTooltips", "scroll"}, constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return AdvancementsScreenExpand.insideHeight();
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return AdvancementsScreenExpand.insideWidth() / 2;
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return AdvancementsScreenExpand.insideHeight() / 2;
    }

    // Replaces the number of background columns with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 15))
    private int improvedadvancements$backgroundColumns(int original) {
        return AdvancementsScreenExpand.insideWidth() / 16 + 1;
    }

    // Replaces the number of background rows with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 8))
    private int improvedadvancements$backgroundRows(int original) {
        return AdvancementsScreenExpand.insideHeight() / 16 + 1;
    }
}