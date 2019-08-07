package net.socialhangover.spreadplayers;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.socialhangover.spreadplayers.commands.PluginCommand;
import net.socialhangover.spreadplayers.commands.TpaCommand;
import net.socialhangover.spreadplayers.config.Configuration;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public class Plugin extends JavaPlugin {

    @Getter
    private static Plugin instance;

    @Getter
    private Configuration configuration;

    @Getter
    private TeleportManager teleportManager;

    @Getter
    private UserManager userManager;

    @Override
    public void onEnable() {
        instance = this;
        this.configuration = new Configuration(resolveConfig());
        this.teleportManager = new TeleportManager(this);
        this.userManager = new UserManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        registerCommands();
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.getCommandReplacements().addReplacement("rootcommand", "spread");

        manager.getCommandContexts().registerOptionalContext(UUID.class, c -> {
            String arg = c.popFirstArg();
            try {
                return arg == null ? null : UUID.fromString(arg);
            } catch(Exception e) {
                return null;
            }
        });

        manager.registerCommand(new PluginCommand(this));
        manager.registerCommand(new TpaCommand(this));
    }

    public Path getRoot() {
        return getDataFolder().toPath();
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
