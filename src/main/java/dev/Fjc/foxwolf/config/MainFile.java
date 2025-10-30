package dev.Fjc.foxwolf.config;

import dev.Fjc.foxwolf.Foxwolf;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Not in use at the moment
 */
public class MainFile extends YamlConfiguration {

    private final Foxwolf plugin;

    static FileConfiguration file;

    public MainFile(@NotNull Foxwolf plugin) {
        this.plugin = plugin;
        file = plugin.getConfig();
    }
    public void reload() {
        plugin.saveConfig();
        plugin.reloadConfig();
    }
    public void loadDefaults() {
        addDefault("radius", 40);
        addDefault("isEnabled", true);
    }


}
