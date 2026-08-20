package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
abstract class ScreenMixin {
    // Updates the advancement window size when the Minecraft window is resized
    @Inject(method = "resize", at = @At("HEAD"))
    private void improvedadvancements$resize(
            Minecraft minecraft, int width, int height, CallbackInfo callbackInfo
    ) {
        if ((Object) this instanceof AdvancementsScreen) {
            AdvancementsScreenExpand.expand(width, height);
        }
    }
}