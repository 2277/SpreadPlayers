package net.socialhangover.spreadplayers;

import lombok.Getter;
import net.socialhangover.spreadplayers.commands.*;
import net.socialhangover.spreadplayers.config.Configuration;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import net.socialhangover.spreadplayers.locale.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public void onDisable() {
        for (UserData userData : getOnlineUsers()) {
            userData.save();
        }
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("spread").setExecutor(new SpreadCommand(this));
        Bukkit.getPluginCommand("tpa").setExecutor(new TpaCommand(this));
        Bukkit.getPluginCommand("tpaccept").setExecutor(new TpacceptCommand(this));
        Bukkit.getPluginCommand("tpdeny").setExecutor(new TpdenyCommand(this));
        Bukkit.getPluginCommand("tpignore").setExecutor(new IgnoreCommand(this));
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

    public UserData getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public UserData getUser(UUID uuid) {
        return userManager.getUser(uuid);
    }

    public Iterable<UserData> getOnlineUsers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(this::getUser).collect(Collectors.toList());
    }
}
