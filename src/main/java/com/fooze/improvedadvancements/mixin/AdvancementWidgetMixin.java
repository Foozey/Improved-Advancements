package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.CriteriaTooltip;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(AdvancementWidget.class)
abstract class AdvancementWidgetMixin {
    @Shadow @Final private AdvancementTab tab;
    @Shadow @Final private AdvancementNode advancementNode;
    @Shadow @Final private DisplayInfo display;
    @Shadow @Final private FormattedCharSequence title;
    @Shadow @Final private int width;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private AdvancementProgress progress;
    @Shadow @Final private int x;
    @Shadow @Final private int y;

    // Modifies advancement tooltips to show criteria progress
    @Inject(method = "drawHover", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$drawProgressCriteria(
            GuiGraphics guiGraphics,
            int x,
            int y,
            float fade,
            int screenOffsetX,
            int screenOffsetY,
            CallbackInfo callbackInfo
    ) {
        List<FormattedCharSequence> criteria = CriteriaTooltip.criteriaList(
                this.advancementNode,
                this.progress,
                this.minecraft
        );

        callbackInfo.cancel();

        // Render the tooltip with criteria
        CriteriaTooltip.render(
                guiGraphics,
                this.tab,
                this.advancementNode,
                this.display,
                this.title,
                this.width,
                this.minecraft,
                this.progress,
                this.x,
                this.y,
                x,
                y,
                screenOffsetX,
                screenOffsetY,
                criteria
        );
    }
}