package com.fooze.improvedadvancements.mixin;

import com.fooze.improvedadvancements.feature.ExpandScreen;
import com.fooze.improvedadvancements.feature.SortTabs;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
abstract class AdvancementsScreenMixin extends Screen {
    @Shadow @Final private ClientAdvancements advancements;
    @Shadow @Final private Map<AdvancementHolder, AdvancementTab> tabs;
    @Shadow @Nullable private AdvancementTab selectedTab;
    @Shadow private int leftPos;
    @Shadow private int topPos;
    @Unique @Nullable private AdvancementTab improvedadvancements$selectedTab;

    protected AdvancementsScreenMixin(Component title) {
        super(title);
    }

    // Renders the expanded advancements window
    @Inject(method = "extractWindow", at = @At("HEAD"), cancellable = true)
    private void improvedadvancements$renderExpandedWindow(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY,
            CallbackInfo callbackInfo
    ) {
        if (ExpandScreen.render(
                guiGraphics, this.leftPos, this.topPos,
                this.tabs, this.selectedTab, this.font, this.title
        )) {
            callbackInfo.cancel();
        }
    }

    // Expands the advancement window
    @Inject(method = "init", at = @At("HEAD"))
    private void improvedadvancements$expand(CallbackInfo callbackInfo) {
        this.improvedadvancements$selectedTab = this.selectedTab;
        ExpandScreen.expand(this.width, this.height);
    }

    // Restores the selected tab after the tabs are rebuilt
    @Inject(method = "init", at = @At("TAIL"))
    private void improvedadvancements$restoreSelectedTab(CallbackInfo callbackInfo) {
        ExpandScreen.restoreTab(this.advancements, this.tabs, this.improvedadvancements$selectedTab);
        this.improvedadvancements$selectedTab = null;
    }

    // Sorts the advancement tabs
    @Redirect(method = "onAdvancementsUpdated", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/AdvancementTree;roots()Ljava/lang/Iterable;"
    ))
    private Iterable<AdvancementNode> improvedadvancements$sortedRoots(AdvancementTree tree) {
        return SortTabs.sort(tree.roots());
    }

    // Replaces the window width with the expanded value
    @ModifyConstant(method = {"repositionElements", "mouseClicked"}, constant = @Constant(intValue = 252))
    private int improvedadvancements$windowWidth(int original) {
        return ExpandScreen.value(original, ExpandScreen.windowWidth());
    }

    // Replaces the window height with the expanded value
    @ModifyConstant(method = {"repositionElements", "mouseClicked"}, constant = @Constant(intValue = 140))
    private int improvedadvancements$windowHeight(int original) {
        return ExpandScreen.value(original, ExpandScreen.windowHeight());
    }

    // Replaces the inside width with the expanded value
    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 234))
    private int improvedadvancements$insideWidth(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideWidth());
    }

    // Replaces the inside height with the expanded value
    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 113))
    private int improvedadvancements$insideHeight(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideHeight());
    }

    // Replaces the inside vertical center with the expanded value
    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 117))
    private int improvedadvancements$insideCenterX(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideWidth() / 2);
    }

    // Replaces the inside horizontal center with the expanded value
    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 56))
    private int improvedadvancements$insideCenterY(int original) {
        return ExpandScreen.value(original, ExpandScreen.insideHeight() / 2);
    }
}