package net.socialhangover.spreadplayers;

import lombok.Getter;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Getter
    private static Plugin instance = null;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
