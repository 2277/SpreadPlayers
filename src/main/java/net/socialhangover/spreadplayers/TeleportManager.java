package net.socialhangover.spreadplayers;

import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.config.ConfigKeys;
import net.socialhangover.spreadplayers.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class TeleportManager {

    private final Map<UUID, UUID> requests = new HashMap<>();
    private final SpreadPlugin plugin;

    public UUID get(UUID key) {
        return requests.get(key);
    }

    public boolean has(UUID key, @Nullable UUID value) {
        return requests.containsKey(key) && (value == null || requests.get(key).equals(value));
    }

    public RequestResult makeRequest(Player sender, Player recipient) {
        if (sender.equals(recipient)) { return RequestResult.ERROR_SELF; }
        if (sender.getStatistic(Statistic.PLAY_ONE_MINUTE) > plugin.getConfiguration()
                .get(ConfigKeys.TELEPORT_PLAYTIME)) {
            return RequestResult.ERROR_PLAYTIME;
        }
        UserData userData = plugin.getUserManager().load(sender.getUniqueId());
        if (userData == null) {
            return RequestResult.ERROR_UNKNOWN;
        }
        if (userData.TELEPORTS.get() >= plugin.getConfiguration().get(ConfigKeys.TELEPORT_LIMIT)) {
            return RequestResult.ERROR_LIMIT;
        }
        requests.put(recipient.getUniqueId(), sender.getUniqueId());
        Bukkit.getServer()
                .getScheduler()
                .scheduleSyncDelayedTask(plugin, () -> killRequest(recipient.getUniqueId(), KillReason.TIMEOUT), plugin.getConfiguration()
                        .get(ConfigKeys.TELEPORT_TIMEOUT));
        return RequestResult.SUCCESS;
    }

    public boolean killRequest(UUID uuid, KillReason reason) {
        if (requests.containsKey(uuid)) {
            OfflinePlayer sender = Bukkit.getPlayer(requests.get(uuid));
            if (sender.isOnline()) {
                OfflinePlayer recipient = Bukkit.getPlayer(uuid);
                switch (reason) {
                    case TIMEOUT:
                        ((Player) sender).sendMessage("Your teleport request to " + recipient.getName() + " has timed out.");
                        break;
                    case DENIED:
                        ((Player) sender).sendMessage("Your teleport request to " + recipient.getName() + " has been denied.");
                        break;
                    default:
                        break;
                }
            }
            requests.remove(uuid);
            return true;
        }
        return false;
    }

    public boolean killRequest(Player sender, KillReason reason) {
        return killRequest(sender.getUniqueId(), reason);
    }

    public enum RequestResult {
        SUCCESS, ERROR_UNKNOWN, ERROR_SELF, ERROR_PLAYTIME, ERROR_LIMIT,
    }

    public enum KillReason {
        ACCEPTED, TIMEOUT, DENIED
    }


}
