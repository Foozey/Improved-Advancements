package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementTab.class)
abstract class AdvancementTabMixin {
    @Shadow private boolean centered;
    @Unique private int improvedadvancements$previousWidth = -1;
    @Unique private int improvedadvancements$previousHeight = -1;

    // Replaces the tiled background loop with one repeated texture
    @Redirect(method = "drawContents", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
    ))
    private void improvedadvancements$drawBackground(
            GuiGraphics guiGraphics, ResourceLocation texture, int x, int y,
            float textureX, float textureY, int width, int height,
            int textureWidth, int textureHeight
    ) {
        guiGraphics.blit(
                texture, x, y, textureX, textureY,
                ExpandScreen.insideWidth() + 32, ExpandScreen.insideHeight() + 32,
                textureWidth, textureHeight
        );
    }

    // Replaces the maximum tabs per page with the expanded capacity
    @Redirect(method = "create", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTabType;MAX_TABS:I"
    ))
    private static int improvedadvancements$tabsPerPage() {
        return ExpandScreen.tabsPerPage();
    }

    // Re-centers advancements when the window size changes
    @Inject(method = "drawContents", at = @At("HEAD"))
    private void improvedadvancements$refreshLayout(
            GuiGraphics guiGraphics, int x, int y, CallbackInfo callbackInfo
    ) {
        if (ExpandScreen.sizeChanged(
                this.improvedadvancements$previousWidth,
                this.improvedadvancements$previousHeight
        )) {
            this.improvedadvancements$previousWidth = ExpandScreen.insideWidth();
            this.improvedadvancements$previousHeight = ExpandScreen.insideHeight();
            this.centered = false;
        }
    }

    // Replaces the inside width with the expanded value
    @ModifyConstant(method = {"drawContents", "drawTooltips", "scroll"}, constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return ExpandScreen.insideWidth();
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = {"drawContents", "drawTooltips", "scroll"}, constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return ExpandScreen.insideHeight();
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return ExpandScreen.insideWidth() / 2;
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return ExpandScreen.insideHeight() / 2;
    }
}