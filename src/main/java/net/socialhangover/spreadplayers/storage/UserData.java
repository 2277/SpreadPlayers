package net.socialhangover.spreadplayers.storage;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class UserData {

    private final File file;
    private final YamlConfiguration config;

    public final StorageKey<Location> SPAWN_LOCATION = new StorageKey<Location>() {
        @Override
        public Location get() {
            ConfigurationSection section = config.getConfigurationSection("location");
            return section == null ? null : new Location(Bukkit.getWorld(section.getString("world", "world")), section.getDouble("x"), section
                    .getDouble("y"), section.getDouble("z"));
        }

        @Override
        public void set(Location location) {
            ConfigurationSection section = config.getConfigurationSection("location");
            if (section == null) section = config.createSection("location");
            section.set("world", location.getWorld().getName());
            section.set("x", location.getX());
            section.set("y", location.getY());
            section.set("z", location.getZ());
            config.set("location", section);
        }
    };

    public final StorageKey<Integer> TELEPORTS = new StorageKey<Integer>() {
        @Override
        public Integer get() {
            return config.getInt("teleports", 0);
        }

        @Override
        public void set(Integer value) {
            config.set("teleports", value);
        }
    };

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save " + file, e);
        }
    }
}
