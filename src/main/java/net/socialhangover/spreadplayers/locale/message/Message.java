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
    TELEPORT_REQUEST_OTHER("&7&o{} wishes wishes to teleport to you."),
    TELEPORT_CLICK_ACCEPT("&a&l [ ✔ ]"),
    TELEPORT_CLICK_DENY("&c&l [ ✘ ]"),
    TELEPORT_HOVER_ACCEPT("/tpaccept"),
    TELEPORT_HOVER_DENY("/tpdeny"),
    TELEPORT_ERROR_GENERIC("&7&oYou can not longer teleport to other players."),
    TELEPORT_ERROR_SELF("&7&oYou cannot teleport to yourself."),
    TELEPORT_ERROR_LIMIT("&7&oYou can not longer teleport to other players."), // TODO: Introduce limit + times teleported arguments.
    TELEPORT_ERROR_PLAYTIME("&7&oYou can not longer teleport to other players."), // TODO: Introduce a human readable playtime argument.
    TELEPORT_ERROR_OFFLINE("&7&o{} is not online."),
    TELEPORT_ERROR_TIMEOUT("&7&oYour teleport request to {} has timed out."),
    TELEPORT_ERROR_DENIED("&7&oYour teleport request to {} has been denied."),
    ;

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
