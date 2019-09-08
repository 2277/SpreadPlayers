package net.socialhangover.spreadplayers.listeners;

import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.UserData;
import net.socialhangover.spreadplayers.config.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final SpreadPlugin plugin;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        UserData userData = plugin.getUser(event.getPlayer().getUniqueId());
        if (!event.getPlayer().hasPlayedBefore() || userData.getSpawnLocation() == null) {
            Bukkit.getServer()
                    .dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfiguration()
                            .get(ConfigKeys.FIRST_JOIN_SPREAD)
                            .getCommandString(event.getPlayer()));
            userData.setSpawnLocation(event.getPlayer().getLocation());
            userData.save();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) { return; }
        UserData userData = plugin.getUser(event.getPlayer().getUniqueId());
        Location location = userData.getSpawnLocation();
        if (location != null) { event.setRespawnLocation(location); }
    }

}
