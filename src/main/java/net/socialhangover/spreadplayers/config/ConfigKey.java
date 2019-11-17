package net.socialhangover.spreadplayers.config;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigKey<T> {
    int ordinal();

    T get(ConfigurationSection c);
}
