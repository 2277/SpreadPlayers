package net.socialhangover.spreadplayers.listeners;

import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.Plugin;
import net.socialhangover.spreadplayers.config.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final Plugin plugin;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            Bukkit.getServer()
                    .dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfiguration().get(ConfigKeys.SPREAD).getCommandString(event.getPlayer()));
            event.getPlayer().setBedSpawnLocation(event.getPlayer().getLocation(), true);
        }
    }

}
