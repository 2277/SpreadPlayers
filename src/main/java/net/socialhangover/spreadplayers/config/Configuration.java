package net.socialhangover.spreadplayers.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Configuration {

    private final File file;
    private Object[] values = null;
    private YamlConfiguration config;

    public Configuration(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized void load() {
        boolean reload = true;

        if (this.values == null) {
            this.values = new Object[ConfigKeys.size()];
            reload = false;
        }

        for (ConfigKey<?> key : ConfigKeys.getKeys().values()) {
            if (reload && key instanceof ConfigKeyTypes.EnduringKey) {
                continue;
            }

            Object value = key.get(this.config);
            this.values[key.ordinal()] = value;
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigKey<T> key) {
        return (T) this.values[key.ordinal()];
    }

}
