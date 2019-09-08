package net.socialhangover.spreadplayers.locale.message;

import lombok.Getter;
import net.socialhangover.spreadplayers.locale.LocaleManager;

public enum Message {
    RELOAD("&7&oConfiguration reloaded."),
    ERROR_GENRIC("&cUnexpected error: check console for details."),
    ERROR_PLAYER_NOT_FOUND("&cPlayer was not found."),
    ERROR_PERMISSION("&cInsufficient permission."),
    ERROR_PLAYER_ONLY("&cYou must be a player to use this command."),
    ERROR_MISSING_ARUGMENT("&cMissing argument(s); usage \"{}\""),
    RESET("&7&o{}'s userdata was successfully reset."),
    RESET_ERROR_MISSING("&7&o{}'s userdata was missing or could not be found."),
    TELEPORT_REQUEST("&7&oTeleport request sent to {}."),
    TELEPORT_REQUEST_OTHER("&7&o{} wishes to teleport to you."),
    TELEPORT_CLICK_ACCEPT("&a&l [ ✔ ]"),
    TELEPORT_CLICK_DENY("&c&l [ ✘ ]"),
    TELEPORT_HOVER_ACCEPT("/tpaccept"),
    TELEPORT_HOVER_DENY("/tpdeny"),
    TELEPORT_ERROR_GENERIC("&cYou can not longer teleport to other players."),
    TELEPORT_ERROR_SELF("&cYou cannot teleport to yourself."),
    TELEPORT_ERROR_LIMIT("&cYou can not longer teleport to other players."), // TODO: Introduce limit + times teleported arguments.
    TELEPORT_ERROR_PLAYTIME("&cYou can not longer teleport to other players."), // TODO: Introduce a human readable playtime argument.
    TELEPORT_ERROR_OFFLINE("&c{} is not online."),
    TELEPORT_ERROR_TIMEOUT("&cYour teleport request to {} has timed out."),
    TELEPORT_ERROR_DENIED("&cYour teleport request to {} has been denied."),
    TELEPORT_ERROR_OVERWORLD("&c{} appears to be in another dimension."),
    TELEPORT_ERROR_EXPIRED("&cThat teleport request has expired."),
    ERROR_PLAYER_IGNORING("&c{} is not accepting teleport requests."),
    IGNORE_ENABLED("&7&oYou will now receive teleport requests."),
    IGNORE_DISABLED("&7&oYou will no longer receive teleport requests."),
    TELEPORT_WARNING("&cWARNING: This teleport will bring you closer to {} but you may arrive up to 32 blocks away from their current location. Please make sure {} is in a suitable location before continuing. This action is irreversible and will modify your spawn location. If you have a bed, please break it before continuing. You are allowed ONE teleport in your first six hours on &lSOCIALHANGOVER&c, use it wisely.\n\n If you really wish to continue, type \"/tpa\" again. This cannot be undone; you have been warned.");

    @Getter
    private final String message;

    Message(String message) {
        this.message = rewritePlaceholders(message);
    }

    public static String colorize(String s) {
        char[] b = s.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    private static String format(String s, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            s = s.replace("{" + i + "}", String.valueOf(o));
        }
        return s;
    }

    public static String rewritePlaceholders(String input) {
        int i = 0;
        while (input.contains("{}")) {
            input = input.replaceFirst("\\{\\}", "{" + i++ + "}");
        }
        return input;
    }

    public static void main(String[] args) {
        for (Message message : values()) {
            String key = message.name().replace('_', '-').toLowerCase();
            String value = message.message;

            if (!value.contains("\n")) {
                System.out.println(key + ": \"" + value.replace("\"", "\\\"") + "\"");
            } else {
                System.out.println(key + ": >");
                String[] parts = value.split("\n");

                for (int i = 0; i < parts.length; i++) {
                    String s = parts[i].replace("\"", "\\\"");
                    System.out.println("  " + s + (i == (parts.length - 1) ? "" : "\\n"));
                }
            }
        }
    }

    public String asString(LocaleManager localeManager, Object... objects) {
        return colorize(format(localeManager, objects));
    }

    private String format(LocaleManager localeManager, Object... objects) {
        return format(this.getTranslatedMessage(localeManager).replace("\\n", "\n"), objects);
    }

    private String getTranslatedMessage(LocaleManager localeManager) {
        String prefix = null;
        if (localeManager != null) {
            prefix = localeManager.getTranslation(this);
        }
        if (prefix == null) {
            prefix = this.getMessage();
        }
        return prefix;
    }
}
