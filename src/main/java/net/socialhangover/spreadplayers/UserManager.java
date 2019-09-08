package net.socialhangover.spreadplayers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserManager extends CacheLoader<UUID, UserData> {

    @Getter
    private final LoadingCache<UUID, UserData> users;

    private final SpreadPlugin plugin;

    public UserManager(SpreadPlugin plugin) {
        this.plugin = plugin;
        users = CacheBuilder.newBuilder() //
                .maximumSize(20) //
                .expireAfterAccess(10, TimeUnit.MINUTES) //
                .build(this);
    }

    public UserData getUser(UUID uuid) {
        try {
            return users.get(uuid);
        } catch (ExecutionException e) {
            // ignored
        }
        return null;
    }

    @Override
    public UserData load(UUID uuid) throws Exception {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null) {
            return new UserData(player, plugin);
        }
        throw new Exception("User was not found.");
    }
}
