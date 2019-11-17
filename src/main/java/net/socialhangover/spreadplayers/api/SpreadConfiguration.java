package net.socialhangover.spreadplayers.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class SpreadConfiguration {

    private final int x;
    private final int z;
    private final int distance;
    private final int bounds;

    public SpreadConfiguration(ConfigurationSection c) {
        this.x = c.getInt("x", 0);
        this.z = c.getInt("z", 0);
        this.distance = c.getInt("distance", 192);
        this.bounds = c.getInt("bounds", 6144);
    }

    public String toCommandString(Player player) {
        return String.format("spreadplayers %d %d %d %d %b %s", x, z, distance, bounds, false, player.getName());
    }

}
