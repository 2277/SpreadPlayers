package net.socialhangover.spreadplayers.listeners;

import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.config.ConfigKeys;
import net.socialhangover.spreadplayers.storage.UserData;
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
        UserData userData = plugin.getUserManager().load(event.getPlayer());
        if (userData != null && (!event.getPlayer().hasPlayedBefore() || userData.SPAWN_LOCATION.get() == null)) {
            Bukkit.getServer()
                    .dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfiguration()
                            .get(ConfigKeys.SPREAD)
                            .getCommandString(event.getPlayer()));

            Location location = event.getPlayer().getLocation();
            event.getPlayer().setBedSpawnLocation(location, true);
            userData.SPAWN_LOCATION.set(location);
            userData.save();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) {
            return;
        }
        UserData userData = plugin.getUserManager().load(event.getPlayer());
        if (userData == null) { return; }
        Location location = userData.SPAWN_LOCATION.get();
        if (location != null) {
            event.setRespawnLocation(location);
        }
    }

}
