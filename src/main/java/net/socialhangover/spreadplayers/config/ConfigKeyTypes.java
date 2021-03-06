package net.socialhangover.spreadplayers.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ConfigKeyTypes {
    private static final KeyFactory<Boolean> BOOLEAN = ConfigurationSection::getBoolean;
    private static final KeyFactory<String> STRING = ConfigurationSection::getString;

    private static final KeyFactory<String> LOWERCASE_STRING = (config, path, def) -> config.getString(path, def)
            .toLowerCase();

    private static final KeyFactory<Map<String, String>> STRING_MAP = (config, path, def) -> {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return ImmutableMap.copyOf(def);
        }

        for (String key : section.getKeys(false)) {
            map.put(key, section.getString(key));
        }
        return ImmutableMap.copyOf(map);
    };
    private static final KeyFactory<Integer> INT = ConfigurationSection::getInt;
    private static final KeyFactory<Long> LONG = ConfigurationSection::getLong;

    public static BaseConfigKey<Boolean> booleanKey(String path, boolean def) {
        return BOOLEAN.createKey(path, def);
    }

    public static BaseConfigKey<String> stringKey(String path, String def) {
        return STRING.createKey(path, def);
    }

    public static BaseConfigKey<String> lowercaseStringKey(String path, String def) {
        return LOWERCASE_STRING.createKey(path, def);
    }

    public static BaseConfigKey<Integer> intKey(String path, int def) {
        return INT.createKey(path, def);
    }

    public static BaseConfigKey<Long> longKey(String path, long def) {
        return LONG.createKey(path, def);
    }

    public static <T> CustomKey<T> customKey(Function<ConfigurationSection, T> function) {
        return new CustomKey<T>(function);
    }

    public static <T> CustomKey<T> section(String path, Function<ConfigurationSection, T> function) {
        return new CustomKey<T>((c) -> {
            ConfigurationSection section = c.getConfigurationSection(path);
            if (section == null) {
                throw new IllegalArgumentException("Missing section: " + path);
            }
            return function.apply(section);
        });
    }

    public static <T> EnduringKey<T> enduringKey(ConfigKey<T> delegate) {
        return new EnduringKey<>(delegate);
    }

    public interface KeyFactory<T> {
        T getValue(ConfigurationSection c, String path, T def);

        default BaseConfigKey<T> createKey(String path, T def) {
            return new FunctionalKey<>(this, path, def);
        }
    }

    public abstract static class BaseConfigKey<T> implements ConfigKey<T> {
        int ordinal = -1;

        @Override
        public int ordinal() {
            return this.ordinal;
        }
    }

    private static class FunctionalKey<T> extends BaseConfigKey<T> implements ConfigKey<T> {
        private final KeyFactory<T> factory;
        private final String path;
        private final T def;

        FunctionalKey(KeyFactory<T> factory, String path, T def) {
            this.factory = factory;
            this.path = path;
            this.def = def;
        }

        @Override
        public T get(ConfigurationSection config) {
            return this.factory.getValue(config, this.path, this.def);
        }
    }

    public static class CustomKey<T> extends BaseConfigKey<T> {
        private final Function<ConfigurationSection, T> function;

        private CustomKey(Function<ConfigurationSection, T> function) {
            this.function = function;
        }

        @Override
        public T get(ConfigurationSection config) {
            return this.function.apply(config);
        }
    }

    public static class EnduringKey<T> extends BaseConfigKey<T> {
        private final ConfigKey<T> delegate;

        private EnduringKey(ConfigKey<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get(ConfigurationSection config) {
            return this.delegate.get(config);
        }
    }
}
