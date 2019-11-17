package net.socialhangover.spreadplayers.listeners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.UserData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    @Getter
    private static final Set<UUID> reset = new HashSet<>();
    private final SpreadPlugin plugin;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore() || reset.contains(player.getUniqueId())) {
            plugin.spread(event.getPlayer());
            reset.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (!event.isBedSpawn()) {
            UserData userData = plugin.getUser(event.getPlayer().getUniqueId());
            Location location = userData.getSpawnLocation();
            if (location != null) { event.setRespawnLocation(location); }
        }
    }

}
