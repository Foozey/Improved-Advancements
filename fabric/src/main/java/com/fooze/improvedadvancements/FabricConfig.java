package com.fooze.improvedadvancements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FabricConfig extends Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Config path
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(
            ImprovedAdvancements.MOD_ID + "-client.json"
    );

    // Config values
    private boolean enableSorting = true;
    private boolean expandScreen = true;
    private int expandAmount = 100;
    private boolean showCriteria = true;

    public static FabricConfig load() {
        FabricConfig config = new FabricConfig();

        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                FabricConfig loadedConfig = GSON.fromJson(reader, FabricConfig.class);

                if (loadedConfig != null) {
                    config = loadedConfig;
                }
            } catch (IOException | RuntimeException ignored) {
            }
        }

        // Config ranges
        config.expandAmount = Math.clamp(config.expandAmount, 0, 100);

        config.save();
        return config;
    }

    private void save() {
        try {
            Files.createDirectories(PATH.getParent());

            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean enableSorting() {
        return enableSorting;
    }

    @Override
    public boolean expandScreen() {
        return expandScreen;
    }

    @Override
    public int expandAmount() {
        return expandAmount;
    }

    @Override
    public boolean showCriteria() {
        return showCriteria;
    }
}