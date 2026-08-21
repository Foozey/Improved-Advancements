package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
abstract class ScreenMixin {
    @Shadow protected abstract void rebuildWidgets();

    // Updates the advancement window size when the Minecraft window is resized
    @Inject(method = "resize", at = @At("TAIL"))
    private void improvedadvancements$resize(
            Minecraft minecraft, int width, int height, CallbackInfo callbackInfo
    ) {
        if ((Object) this instanceof AdvancementsScreen) {
            ExpandScreen.expand(width, height);
            this.rebuildWidgets();
        }
    }
}