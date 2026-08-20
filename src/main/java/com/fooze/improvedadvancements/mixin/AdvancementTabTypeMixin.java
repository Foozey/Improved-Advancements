package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.client.gui.screens.advancements.AdvancementTabType")
abstract class AdvancementTabTypeMixin {
    // Replaces the horizontal position of the tabs on the right side of the window with the expanded values
    @ModifyConstant(method = "getX", constant = @Constant(intValue = 248))
    private int improvedadvancements$rightTabX(int original) {
        return AdvancementsScreenExpand.windowWidth() - 4;
    }

    // Replaces the vertical position of the tabs on the bottom side of the window with the expanded values
    @ModifyConstant(method = "getY", constant = @Constant(intValue = 136))
    private int improvedadvancements$bottomTabY(int original) {
        return AdvancementsScreenExpand.windowHeight() - 4;
    }
}