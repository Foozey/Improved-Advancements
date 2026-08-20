package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AdvancementWidget.class)
abstract class AdvancementWidgetMixin {
    // Replaces the inside height with the expanded value
    @ModifyConstant(method = "drawHover", constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return AdvancementsScreenExpand.insideHeight();
    }
}