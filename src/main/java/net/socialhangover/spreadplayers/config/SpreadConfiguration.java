package net.socialhangover.spreadplayers.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class SpreadConfiguration {

    private final Vector2 origin;

    private final int distance;

    private final int bounds;

    public String getCommandString(Player player) {
        return String.format("spreadplayers %d %d %d %d %b %s", origin.getX(), origin.getY(), distance,
                bounds, false, player.getName());
    }

}
