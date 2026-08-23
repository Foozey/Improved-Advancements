package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import com.fooze.improvedadvancements.feature.SortTabs;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(AdvancementsScreen.class)
abstract class AdvancementsScreenMixin extends Screen {
    @Shadow @Final private ClientAdvancements advancements;
    @Shadow @Final private Map<AdvancementHolder, AdvancementTab> tabs;
    @Shadow @Nullable private AdvancementTab selectedTab;
    @Shadow private static int tabPage;

    // Syncs the page controls with the expanded tab layout
    @Redirect(method = "init", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTabType;MAX_TABS:I"
    ))
    private static int improvedadvancements$tabsPerPage() {
        return ExpandScreen.tabsPerPage();
    }

    protected AdvancementsScreenMixin(Component title) {
        super(title);
    }

    // Renders the expanded advancements window
    @Inject(method = "renderWindow", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$renderExpandedWindow(
            GuiGraphics guiGraphics, int offsetX, int offsetY, CallbackInfo callbackInfo
    ) {
        if (ExpandScreen.render(
                guiGraphics, offsetX, offsetY,
                this.tabs, tabPage, this.selectedTab,
                this.font, this.title
        )) {
            callbackInfo.cancel();
        }
    }

    // Sorts the advancement tabs and expands the advancement window
    @Inject(method = "init", at = @At("HEAD"))
    private void improvedadvancements$init(CallbackInfo callbackInfo) {
        SortTabs.sort(this.advancements.getTree().roots());
        ExpandScreen.expand(this.width, this.height);
    }

    // Replaces the window width with the expanded value
    @ModifyConstant(method = {"init", "mouseClicked", "render"}, constant = @Constant(intValue = 252))
    private int improvedadvancements$windowWidth(int original) {
        return ExpandScreen.windowWidth();
    }

    // Replaces the window height with the expanded value
    @ModifyConstant(method = {"init", "mouseClicked", "render"}, constant = @Constant(intValue = 140))
    private int improvedadvancements$windowHeight(int original) {
        return ExpandScreen.windowHeight();
    }

    // Replaces the window center with the expanded value
    @ModifyConstant(method = "render", constant = @Constant(intValue = 126))
    private int improvedadvancements$windowCenter(int original) {
        return ExpandScreen.windowWidth() / 2;
    }

    // Replaces the inside width with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return ExpandScreen.insideWidth();
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return ExpandScreen.insideHeight();
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return ExpandScreen.insideWidth() / 2;
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "renderInside", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return ExpandScreen.insideHeight() / 2;
    }
}