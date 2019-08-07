package net.socialhangover.spreadplayers;

import lombok.Getter;
import net.socialhangover.spreadplayers.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class UserManager {

    private final Plugin plugin;

    @Getter
    private final File folder;

    public UserManager(Plugin plugin) {
        this.plugin = plugin;

        folder = new File(plugin.getDataFolder(), "userdata");
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    public UserData load(Player player) {
        return load(player.getUniqueId());
    }

    public UserData load(UUID uuid) {
        File file = new File(folder, uuid + ".yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot create " + file, e);
                return null;
            }
        }
        return new UserData(file, YamlConfiguration.loadConfiguration(file));
    }

}
