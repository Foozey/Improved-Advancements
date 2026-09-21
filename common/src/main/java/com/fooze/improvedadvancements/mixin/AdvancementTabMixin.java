package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.resources.Identifier;
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
    @Redirect(method = "extractContents", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
    ))
    private void improvedadvancements$drawBackground(
            GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier texture,
            int x, int y, float u, float v, int width, int height,
            int textureWidth, int textureHeight
    ) {
        ExpandScreen.drawBackground(
                graphics, pipeline, texture,
                x, y, u, v, width, height,
                textureWidth, textureHeight
        );
    }

    // Re-centers advancements when the window size changes
    @Inject(method = "extractContents", at = @At("HEAD"))
    private void improvedadvancements$recenter(
            GuiGraphicsExtractor guiGraphics, int x, int y, CallbackInfo callbackInfo
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
    @ModifyConstant(method = {"extractContents", "extractTooltips", "scroll"}, constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideWidth());
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = {"extractContents", "extractTooltips", "scroll"}, constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideHeight());
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "extractContents", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideWidth() / 2);
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "extractContents", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideHeight() / 2);
    }

    // Replaces the hover width with the expanded value
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 234))
    private int improvedadvancements$hoverWidth(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideWidth());
    }

    // Replaces the hover height with the expanded value
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 113))
    private int improvedadvancements$hoverHeight(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideHeight());
    }

    // Replaces the scroll width with the expanded value
    @ModifyConstant(method = "canScrollHorizontally", constant = @Constant(intValue = 234))
    private int improvedadvancements$scrollWidth(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideWidth());
    }

    // Replaces the scroll height with the expanded value
    @ModifyConstant(method = "canScrollVertically", constant = @Constant(intValue = 113))
    private int improvedadvancements$scrollHeight(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideHeight());
    }
}