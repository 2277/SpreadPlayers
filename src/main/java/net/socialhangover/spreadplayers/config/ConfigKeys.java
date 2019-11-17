package net.socialhangover.spreadplayers.config;

import com.google.common.collect.ImmutableMap;
import net.socialhangover.spreadplayers.api.SpreadConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.socialhangover.spreadplayers.config.ConfigKeyTypes.booleanKey;
import static net.socialhangover.spreadplayers.config.ConfigKeyTypes.intKey;
import static net.socialhangover.spreadplayers.config.ConfigKeyTypes.longKey;
import static net.socialhangover.spreadplayers.config.ConfigKeyTypes.section;

public final class ConfigKeys {

    public static final ConfigKey<SpreadConfiguration> FIRST_JOIN_SPREAD = section("spread", SpreadConfiguration::new);

    public static final ConfigKey<Boolean> CLUSTER_ENABLED = booleanKey("cluster.enabled", true);
    public static final ConfigKey<Long> CLUSTER_TIMEOUT = longKey("cluster.timeout", 21600000);
    public static final ConfigKey<Integer> CLUSTER_DISTANCE = intKey("cluster.distance", 64);
    public static final ConfigKey<Integer> CLUSTER_BOUNDS = intKey("cluster.bounds", 256);

    public static final ConfigKey<Boolean> OVERWRITE_SPAWN_LOCATION_ON_TELEPORT = booleanKey("teleport.overwrite", true);
    public static final ConfigKey<Long> TELEPORT_PLAYTIME = longKey("teleport.playtime", 432000);
    public static final ConfigKey<Integer> TELEPORT_LIMIT = intKey("teleport.limit", 1);
    public static final ConfigKey<Long> TELEPORT_TIMEOUT = longKey("teleport.timeout", 60000);
    public static final ConfigKey<Integer> TELEPORT_SPREAD = intKey("teleport.spread", 32);

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
