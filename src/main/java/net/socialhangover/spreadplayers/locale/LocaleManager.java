package net.socialhangover.spreadplayers.locale;

import com.google.common.collect.ImmutableMap;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public final class LocaleManager {
    private Map<Message, String> messages = ImmutableMap.of();

    public void tryLoad(SpreadPlugin plugin, Path file) {
        if (Files.exists(file)) {
            plugin.getLogger().info("Found locale.yml - loading messages...");
            try {
                loadFromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            EnumMap<Message, String> messages = new EnumMap<>(Message.class);

            Map<String, Object> data = (Map<String, Object>) new Yaml().load(reader);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getKey() == null || entry.getKey().isEmpty() || entry.getValue() == null) {
                    continue;
                }

                if (entry.getValue() instanceof String) {
                    String key = entry.getKey().toUpperCase().replace('-', '_');
                    String value = (String) entry.getValue();

                    try {
                        messages.put(Message.valueOf(key), value);
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }
                }
            }

            this.messages = ImmutableMap.copyOf(messages);
        }
    }

    public String getTranslation(Message key) {
        return this.messages.get(key);
    }
}
