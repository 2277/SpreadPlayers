package net.socialhangover.spreadplayers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class UserData {

    @Getter
    private final Player player;

    private final File file;

    private final YamlConfiguration config;

    public UserData(Player player, SpreadPlugin plugin) {
        this.player = player;

        File folder = new File(plugin.getDataFolder(), "userdata");
        if (!folder.exists()) { folder.mkdirs(); }

        this.file = new File(folder, player.getUniqueId().toString() + ".yml");
        config = YamlConfiguration.loadConfiguration(this.file);
    }

    public Location getSpawnLocation() {
        ConfigurationSection s = config.getConfigurationSection("location");
        return s == null ? null : new Location(Bukkit.getWorld(s.getString("world", "world")), s.getDouble("x"), s.getDouble("y"), s
                .getDouble("z"));
    }

    public void setSpawnLocation(Location l) {
        ConfigurationSection s = config.getConfigurationSection("location");
        if (s == null) s = config.createSection("location");
        s.set("world", l.getWorld().getName());
        s.set("x", l.getX());
        s.set("y", l.getY());
        s.set("z", l.getZ());
        config.set("location", s);
    }

    public int getTeleports() {
        return config.getInt("teleports", 0);
    }

    public void setTeleports(int value) {
        config.set("teleports", value);
    }

    public void incrementTeleports() {
        setTeleports(getTeleports() + 1);
    }

    public boolean isIgnoring() {
        return config.getBoolean("ignore", false);
    }

    public void setIgnoring(boolean value) {
        config.set("ignore", value);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save " + file, e);
        }
    }

}
