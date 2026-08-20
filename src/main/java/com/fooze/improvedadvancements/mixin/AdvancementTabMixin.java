package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementTab.class)
abstract class AdvancementTabMixin {
    // Tracks whether advancements are centered
    @Shadow
    private boolean centered;

    // Stores the last inside width
    @Unique
    private int improvedadvancements$lastInsideWidth = -1;

    // Stores the last inside height
    @Unique
    private int improvedadvancements$lastInsideHeight = -1;

    // Re-centers advancements when the window size changes
    @Inject(method = "drawContents", at = @At("HEAD"))
    private void improvedadvancements$refreshLayout(
            GuiGraphics guiGraphics, int x, int y, CallbackInfo callbackInfo
    ) {
        int insideWidth = AdvancementsScreenExpand.insideWidth();
        int insideHeight = AdvancementsScreenExpand.insideHeight();

        // Check whether the available advancement area changed
        if (insideWidth != this.improvedadvancements$lastInsideWidth
                || insideHeight != this.improvedadvancements$lastInsideHeight) {

            // Remember the new dimensions
            this.improvedadvancements$lastInsideWidth = insideWidth;
            this.improvedadvancements$lastInsideHeight = insideHeight;

            // Recalculate the centering
            this.centered = false;
        }
    }

    // Replaces the inside width with the expanded value
    @ModifyConstant(method = {"drawContents", "drawTooltips", "scroll"}, constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return AdvancementsScreenExpand.insideWidth();
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = {"drawContents", "drawTooltips", "scroll"}, constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return AdvancementsScreenExpand.insideHeight();
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return AdvancementsScreenExpand.insideWidth() / 2;
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return AdvancementsScreenExpand.insideHeight() / 2;
    }

    // Replaces the number of background columns with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 15))
    private int improvedadvancements$backgroundColumns(int original) {
        return AdvancementsScreenExpand.insideWidth() / 16 + 1;
    }

    // Replaces the number of background rows with the expanded value
    @ModifyConstant(method = "drawContents", constant = @Constant(intValue = 8))
    private int improvedadvancements$backgroundRows(int original) {
        return AdvancementsScreenExpand.insideHeight() / 16 + 1;
    }
}