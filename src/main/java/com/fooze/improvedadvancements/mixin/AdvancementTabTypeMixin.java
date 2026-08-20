package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
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
    @Shadow @Final @Mutable
    private int max;

    // Replaces the tab capacity with the expanded value
    @Inject(method = "getMax", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$tabCapacity(CallbackInfoReturnable<Integer> callbackInfo) {
        String tabType = ((Enum<?>) (Object) this).name();
        int capacity = improvedadvancements$tabCapacity(tabType);

        // Only use the edge tab texture if a tab reaches the edge of the window
        if (improvedadvancements$reachesWindowEdge(tabType, capacity)) {
            this.max = capacity;
        } else {
            this.max = Integer.MAX_VALUE;
        }

        callbackInfo.setReturnValue(capacity);
    }

    // Moves right tabs to the expanded edge
    @ModifyConstant(method = "getX", constant = @Constant(intValue = 248))
    private int improvedadvancements$rightTabX(int original) {
        return AdvancementsScreenExpand.windowWidth() - 4;
    }

    // Moves bottom tabs to the expanded edge
    @ModifyConstant(method = "getY", constant = @Constant(intValue = 136))
    private int improvedadvancements$bottomTabY(int original) {
        return AdvancementsScreenExpand.windowHeight() - 4;
    }

    // Returns how many tabs fit an edge
    private static int improvedadvancements$tabCapacity(String tabType) {
        return switch (tabType) {
            case "ABOVE", "BELOW" -> AdvancementsScreenExpand.horizontalTabCapacity();
            case "LEFT", "RIGHT" -> AdvancementsScreenExpand.verticalTabCapacity();
            default -> throw new IllegalArgumentException("Unknown advancement tab type: " + tabType);
        };
    }

    // Returns whether the final tab on an edge reaches the window edge
    private static boolean improvedadvancements$reachesWindowEdge(String tabType, int capacity) {
        return switch (tabType) {
            case "ABOVE", "BELOW" -> (capacity - 1) * 32 + 28 == AdvancementsScreenExpand.windowWidth();
            case "LEFT", "RIGHT" -> capacity * 28 == AdvancementsScreenExpand.windowHeight();
            default -> throw new IllegalArgumentException("Unknown advancement tab type: " + tabType);
        };
    }
}