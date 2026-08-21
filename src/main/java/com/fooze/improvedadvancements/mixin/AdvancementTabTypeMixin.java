package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.screens.advancements.AdvancementTabType")
abstract class AdvancementTabTypeMixin {
    @Shadow @Final @Mutable private int max;

    // Replaces the tab capacity with the expanded value
    @Inject(method = "getMax", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$tabCapacity(CallbackInfoReturnable<Integer> callbackInfo) {
        String tabType = ((Enum<?>) (Object) this).name();

        // Above and below tabs use horizontal spacing, left and right use vertical spacing
        boolean horizontal = switch (tabType) {
            case "ABOVE", "BELOW" -> true;
            case "LEFT", "RIGHT" -> false;
            default -> throw new IllegalArgumentException("Unknown advancement tab type: " + tabType);
        };

        int capacity;
        boolean reachesEdge;

        // Calculate the capacity and whether its final tab reaches the edge
        if (horizontal) {
            capacity = ExpandScreen.horizontalTabCapacity();
            reachesEdge = (capacity - 1) * 32 + 28 == ExpandScreen.windowWidth();
        } else {
            capacity = ExpandScreen.verticalTabCapacity();
            reachesEdge = capacity * 28 == ExpandScreen.windowHeight();
        }

        // Only use the edge texture when the final tab reaches the edge
        if (reachesEdge) {
            this.max = capacity;
        } else {
            this.max = Integer.MAX_VALUE;
        }

        callbackInfo.setReturnValue(capacity);
    }

    // Moves right tabs to the expanded edge
    @ModifyConstant(method = "getX", constant = @Constant(intValue = 248))
    private int improvedadvancements$rightTabX(int original) {
        return ExpandScreen.windowWidth() - 4;
    }

    // Moves bottom tabs to the expanded edge
    @ModifyConstant(method = "getY", constant = @Constant(intValue = 136))
    private int improvedadvancements$bottomTabY(int original) {
        return ExpandScreen.windowHeight() - 4;
    }
}