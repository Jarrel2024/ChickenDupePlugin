package org.zzzzzzz.chickenDupe;

import org.bukkit.plugin.java.JavaPlugin;


public final class ChickenDupe extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new DupeListener(this),this);
        reloadConfig();
    }

    @Override
    public void onDisable() {
    }
}

