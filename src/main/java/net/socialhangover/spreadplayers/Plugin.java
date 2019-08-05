package net.socialhangover.spreadplayers;

import lombok.Getter;
import net.socialhangover.spreadplayers.config.Configuration;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Plugin extends JavaPlugin {

    @Getter
    private static Plugin instance;

    @Getter
    private Configuration configuration;

    @Override
    public void onEnable() {
        instance = this;
        this.configuration = new Configuration(resolveConfig());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private File resolveConfig() {
        File folder = getDataFolder();
        File file = new File(folder, "config.yml");
        if (!file.exists()) {
            folder.mkdirs();
            saveResource("config.yml", false);
        }
        return file;
    }
}
