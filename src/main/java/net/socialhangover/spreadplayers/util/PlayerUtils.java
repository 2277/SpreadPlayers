package net.socialhangover.spreadplayers.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public final class PlayerUtils {

    private PlayerUtils() {}

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(String nameOrUuid) {
        OfflinePlayer player = getOfflinePlayerFromUUID(nameOrUuid);
        if (player != null && (player.hasPlayedBefore() || player.isOnline())) { return player; }
        player = Bukkit.getServer().getPlayer(nameOrUuid);
        if (player == null) {
            player = Bukkit.getServer().getOfflinePlayer(nameOrUuid);
        }
        return player != null && (!player.hasPlayedBefore() && !player.isOnline()) ? null : player;
    }

    private static OfflinePlayer getOfflinePlayerFromUUID(String possibleUuid) {
        UUID uuid;
        try {
            uuid = UUID.fromString(possibleUuid);
        } catch (IllegalArgumentException e) {
            return null;
        }

        OfflinePlayer player = Bukkit.getServer().getPlayer(uuid);
        return player != null ? player : Bukkit.getServer().getOfflinePlayer(uuid);
    }

}
