package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.CriteriaTooltip;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementWidget.class)
abstract class AdvancementWidgetMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private DisplayInfo display;
    @Shadow @Nullable private AdvancementProgress progress;
    @Shadow @Final private AdvancementNode advancementNode;
    @Shadow @Final private int width;
    @Shadow @Final private int x;
    @Shadow @Final private int y;

    // Modifies advancement tooltips to show criteria progress
    @Inject(method = "extractHover", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$showCriteria(
            GuiGraphicsExtractor guiGraphics, int x, int y, float fade,
            int screenOffsetX, int screenOffsetY, int screenWidth,
            CallbackInfo callbackInfo
    ) {
        if (CriteriaTooltip.render(
                this.minecraft, guiGraphics,
                this.display, this.display.title().getVisualOrderText(),
                this.progress, this.advancementNode,
                screenWidth, guiGraphics.guiHeight(), this.width, this.x, this.y,
                screenOffsetX, screenOffsetY, x, y
        )) {
            callbackInfo.cancel();
        }
    }
}