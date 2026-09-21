package com.fooze.improvedadvancements;

import net.fabricmc.api.ClientModInitializer;

public class ImprovedAdvancementsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.setInstance(FabricConfig.load());
    }
}