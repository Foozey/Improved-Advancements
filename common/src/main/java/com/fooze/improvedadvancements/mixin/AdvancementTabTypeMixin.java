package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.screens.advancements.AdvancementTabType")
abstract class AdvancementTabTypeMixin {
    // Expands the tab capacity based on the window size
    @Inject(method = "getMax", at = @At("RETURN"), cancellable = true)
    private void improvedadvancements$tabCapacity(CallbackInfoReturnable<Integer> callbackInfo) {
        callbackInfo.setReturnValue(ExpandScreen.value(
                callbackInfo.getReturnValue(), ExpandScreen.tabCapacity((Enum<?>) (Object) this)
        ));
    }

    // Uses the edge texture for edge tabs
    @Redirect(method = "extractRenderState", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTabType;max:I"
    ))
    private int improvedadvancements$edgeTabCapacity(AdvancementTabType tabType) {
        return ExpandScreen.value(tabType.getMax(), ExpandScreen.edgeTabCapacity(tabType));
    }

    // Moves right tabs to the expanded edge
    @ModifyConstant(method = "getX", constant = @Constant(intValue = 248))
    private int improvedadvancements$rightTabX(int original) {
        return ExpandScreen.value(original, ExpandScreen.windowWidth() - 4);
    }

    // Moves bottom tabs to the expanded edge
    @ModifyConstant(method = "getY", constant = @Constant(intValue = 136))
    private int improvedadvancements$bottomTabY(int original) {
        return ExpandScreen.value(original, ExpandScreen.windowHeight() - 4);
    }
}