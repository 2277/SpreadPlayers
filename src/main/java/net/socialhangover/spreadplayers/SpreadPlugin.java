package net.socialhangover.spreadplayers;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.socialhangover.spreadplayers.commands.SpreadCommand;
import net.socialhangover.spreadplayers.commands.TpaCommand;
import net.socialhangover.spreadplayers.config.Configuration;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import net.socialhangover.spreadplayers.locale.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public class SpreadPlugin extends JavaPlugin {

    @Getter
    private static SpreadPlugin instance;

    @Getter
    private Configuration configuration;

    @Getter
    private LocaleManager localeManager;

    @Getter
    private TeleportManager teleportManager;

    @Getter
    private UserManager userManager;

    @Override
    public void onEnable() {
        instance = this;
        this.configuration = new Configuration(resolveConfig());

        this.localeManager = new LocaleManager();
        localeManager.tryLoad(this, getDataDirectory().resolve("locale.yml"));

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

        manager.registerCommand(new SpreadCommand(this));
        manager.registerCommand(new TpaCommand(this));
    }

    public Path getDataDirectory() {
        return getDataFolder().toPath().toAbsolutePath();
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
