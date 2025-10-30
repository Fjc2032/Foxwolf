package dev.Fjc.foxwolf;

import dev.Fjc.foxwolf.listener.TargetMob;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Foxwolf extends JavaPlugin {

    @Override
    public void onEnable() {
        setListener(new TargetMob(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setListener(@NotNull Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
