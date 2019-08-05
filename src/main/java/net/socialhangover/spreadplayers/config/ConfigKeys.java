package net.socialhangover.spreadplayers.config;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.socialhangover.spreadplayers.config.ConfigKeyTypes.customKey;

public final class ConfigKeys {

    public static final ConfigKey<SpreadConfiguration> SPREAD = customKey(c -> new SpreadConfiguration(
            new Vector2(
                    c.getDouble("spread.origin.x", 0),
                    c.getDouble("spread.origin.y", 0)),
            c.getDouble("spread.distance", 256),
            c.getDouble("spread.bounds", 16384))
    );

    private static final Map<String, ConfigKey<?>> KEYS;
    private static final int SIZE;

    static {
        Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();
        Field[] values = ConfigKeys.class.getFields();
        int i = 0;

        for (Field f : values) {
            if (!Modifier.isStatic(f.getModifiers())) {
                continue;
            }

            if (!ConfigKey.class.equals(f.getType())) {
                continue;
            }

            try {
                ConfigKeyTypes.BaseConfigKey<?> key = (ConfigKeyTypes.BaseConfigKey<?>) f.get(null);
                key.ordinal = i++;
                keys.put(f.getName(), key);
            } catch (Exception e) {
                throw new RuntimeException("Exception processing field: " + f, e);
            }
        }

        KEYS = ImmutableMap.copyOf(keys);
        SIZE = i;
    }

    private ConfigKeys() {
    }

    public static Map<String, ConfigKey<?>> getKeys() {
        return KEYS;
    }

    public static int size() {
        return SIZE;
    }

}
