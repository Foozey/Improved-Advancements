package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.util.AdvancementsScreenExpand;
import com.fooze.improvedadvancements.util.AdvancementsScreenSort;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(AdvancementsScreen.class)
abstract class AdvancementsScreenMixin extends Screen {
    // Texture used for the expanded advancements window
    private static final ResourceLocation IMPROVED_ADVANCEMENTS$WINDOW =
            ResourceLocation.withDefaultNamespace("textures/gui/advancements/window.png");

    // Gives access to the advancements
    @Shadow @Final
    private ClientAdvancements advancements;

    // Gives access to the tabs
    @Shadow @Final
    private Map<AdvancementHolder, AdvancementTab> tabs;

    // Gives access to the selected tab
    @Shadow @Nullable
    private AdvancementTab selectedTab;

    // Gives access to the tab page number
    @Shadow
    private static int tabPage;

    protected AdvancementsScreenMixin(Component title) {
        super(title);
    }

    // Sorts the advancement tabs and expands the advancement window
    @Inject(method = "init", at = @At("HEAD"))
    private void improvedadvancements$sortTabs(CallbackInfo callbackInfo) {
        AdvancementsScreenSort.sort(this.advancements.getTree().roots());
        AdvancementsScreenExpand.expand(this.width, this.height);
    }

    // Replaces the window width with the expanded value
    @ModifyConstant(method = {"init", "mouseClicked", "render"}, constant = @Constant(intValue = 252))
    private int improvedadvancements$windowWidth(int original) {
        return AdvancementsScreenExpand.windowWidth();
    }

    // Replaces the window height with the expanded value
    @ModifyConstant(method = {"init", "mouseClicked", "render"}, constant = @Constant(intValue = 140))
    private int improvedadvancements$windowHeight(int original) {
        return AdvancementsScreenExpand.windowHeight();
    }

    // Replaces the horizontal center with the expanded value
    @ModifyConstant(method = "render", constant = @Constant(intValue = 126))
    private int improvedadvancements$windowCenter(int original) {
        return AdvancementsScreenExpand.windowWidth() / 2;
    }

    // Replaces the inside width with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return AdvancementsScreenExpand.insideWidth();
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return AdvancementsScreenExpand.insideHeight();
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return AdvancementsScreenExpand.insideWidth() / 2;
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return AdvancementsScreenExpand.insideHeight() / 2;
    }

    // Renders the expanded advancements window
    @Inject(method = "renderWindow", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$renderExpandedWindow(
            GuiGraphics guiGraphics, int offsetX, int offsetY, CallbackInfo callbackInfo
    ) {
        // Only render the expanded window if it's expanded
        if (!AdvancementsScreenExpand.isExpanded()) {
            return;
        }

        // Stop the vanilla window from rendering, enable blending, and render the expanded window
        callbackInfo.cancel();
        RenderSystem.enableBlend();
        improvedadvancements$drawWindowFrame(guiGraphics, offsetX, offsetY);

        // Draw the tabs if there's more than one
        if (this.tabs.size() > 1) {
            for (AdvancementTab tab : this.tabs.values()) {
                if (tab.getPage() == tabPage) {
                    tab.drawTab(guiGraphics, offsetX, offsetY, tab == this.selectedTab);
                    tab.drawIcon(guiGraphics, offsetX, offsetY);
                }
            }
        }

        // Draw the title of the selected tab, otherwise use the screen title
        guiGraphics.drawString(
                this.font,
                this.selectedTab != null ? this.selectedTab.getTitle() : this.title,
                offsetX + 8,
                offsetY + 6,
                4210752,
                false
        );
    }

    // Draws the expanded advancement window frame
    private static void improvedadvancements$drawWindowFrame(GuiGraphics graphics, int x, int y) {
        int insideWidth = AdvancementsScreenExpand.insideWidth();
        int insideHeight = AdvancementsScreenExpand.insideHeight();
        int rightX = x + AdvancementsScreenExpand.HORIZONTAL_BORDER + insideWidth;
        int bottomY = y + AdvancementsScreenExpand.HEADER_HEIGHT + insideHeight;

        improvedadvancements$blit(graphics, x, y, 9, 18, 0, 0, 9, 18);
        improvedadvancements$blit(graphics, x + 9, y, insideWidth, 18, 9, 0, 234, 18);
        improvedadvancements$blit(graphics, rightX, y, 9, 18, 243, 0, 9, 18);
        improvedadvancements$blit(graphics, x, y + 18, 9, insideHeight, 0, 18, 9, 113);
        improvedadvancements$blit(graphics, rightX, y + 18, 9, insideHeight, 243, 18, 9, 113);
        improvedadvancements$blit(graphics, x, bottomY, 9, 9, 0, 131, 9, 9);
        improvedadvancements$blit(graphics, x + 9, bottomY, insideWidth, 9, 9, 131, 234, 9);
        improvedadvancements$blit(graphics, rightX, bottomY, 9, 9, 243, 131, 9, 9);
    }

    // Helper method for drawing a section of the expanded advancement window
    private static void improvedadvancements$blit(
            GuiGraphics graphics, int x, int y, int width, int height,
            int textureX, int textureY, int textureWidth, int textureHeight
    ) {
        graphics.blit(
                IMPROVED_ADVANCEMENTS$WINDOW,
                x, y, width, height,
                textureX, textureY, textureWidth, textureHeight,
                256, 256
        );
    }
}