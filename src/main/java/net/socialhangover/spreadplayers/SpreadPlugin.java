package net.socialhangover.spreadplayers;

import lombok.Getter;
import net.socialhangover.spreadplayers.api.SpreadConfiguration;
import net.socialhangover.spreadplayers.command.CommandManager;
import net.socialhangover.spreadplayers.command.commands.ExpireCommand;
import net.socialhangover.spreadplayers.command.commands.IgnoreCommand;
import net.socialhangover.spreadplayers.command.commands.ReloadCommand;
import net.socialhangover.spreadplayers.command.commands.ResetCommand;
import net.socialhangover.spreadplayers.command.commands.SpreadCommand;
import net.socialhangover.spreadplayers.command.commands.TpaCommand;
import net.socialhangover.spreadplayers.command.commands.TpacceptCommand;
import net.socialhangover.spreadplayers.command.commands.TpdenyCommand;
import net.socialhangover.spreadplayers.config.ConfigKeys;
import net.socialhangover.spreadplayers.config.Configuration;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import net.socialhangover.spreadplayers.locale.LocaleManager;
import net.socialhangover.spreadplayers.util.CachedValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
    private CommandManager commandManager;

    @Getter
    private TeleportManager teleportManager;

    @Getter
    private UserManager userManager;

    @Getter
    private CachedValue<SpreadConfiguration> spreadCache;

    @Override
    public void onEnable() {
        instance = this;

        this.configuration = new Configuration(getBundledFile("config.yml"));
        this.configuration.load();

        this.localeManager = new LocaleManager();
        this.localeManager.tryLoad(this, getDataDirectory().resolve("locale.yml"));

        this.commandManager = new CommandManager(this);
        commandManager.addCommand(new TpacceptCommand());
        commandManager.addCommand(new TpaCommand());
        commandManager.addCommand(new TpdenyCommand());
        commandManager.addCommand(new IgnoreCommand());
        commandManager.addCommand(new SpreadCommand())
                .addSubCommand(new ReloadCommand())
                .addSubCommand(new ResetCommand())
                .addSubCommand(new ExpireCommand());

        this.teleportManager = new TeleportManager(this);
        this.userManager = new UserManager(this);

        this.spreadCache = new CachedValue<>(configuration.get(ConfigKeys.CLUSTER_TIMEOUT), configuration.get(ConfigKeys.FIRST_JOIN_SPREAD));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        for (UserData userData : getOnlineUsers()) {
            userData.save();
        }
    }

    public Path getDataDirectory() {
        return getDataFolder().toPath().toAbsolutePath();
    }

    private File getBundledFile(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) {
            getDataFolder().mkdir();
            try (InputStream in = getResource(name)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void spread(Player player) {
        SpreadConfiguration spread = configuration.get(ConfigKeys.CLUSTER_ENABLED) ? spreadCache.get() : spreadCache.getDefaultValue();
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), spread.toCommandString(player));

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Location location = player.getLocation();

            UserData userData = getUser(player.getUniqueId());
            userData.setSpawnLocation(location);
            userData.save();

            spreadCache.computeIfExpired(v -> new SpreadConfiguration(location.getBlockX(), location.getBlockZ(), configuration
                    .get(ConfigKeys.CLUSTER_DISTANCE), configuration.get(ConfigKeys.CLUSTER_BOUNDS)));
        });
    }

}
