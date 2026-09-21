package com.fooze.improvedadvancements;

public abstract class Config {
    private static Config instance;

    public static Config get() {
        if (instance == null) {
            throw new IllegalStateException("Config has not been initialised");
        }

        return instance;
    }

    public static void setInstance(Config config) {
        instance = config;
    }

    // Config values
    public abstract boolean enableSorting();
    public abstract boolean expandScreen();
    public abstract int expandAmount();
    public abstract boolean showCriteria();
}
