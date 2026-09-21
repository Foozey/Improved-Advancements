package com.fooze.improvedadvancements;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ImprovedAdvancements.MOD_ID, dist = Dist.CLIENT)
public class ImprovedAdvancementsNeoForge {
    public ImprovedAdvancementsNeoForge(ModContainer modContainer) {
        Config.setInstance(new NeoForgeConfig());
        modContainer.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}