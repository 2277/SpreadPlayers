package net.socialhangover.spreadplayers.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.locale.message.Message;
import net.socialhangover.spreadplayers.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SpreadCommand extends BaseCommand {

    public SpreadCommand(SpreadPlugin plugin) {
        super(plugin);
    }

    public void onReload(CommandSender sender) {
        plugin.getConfiguration().reload();
        sender.sendMessage(Message.RELOAD.asString(plugin.getLocaleManager()));
    }

    public void onReset(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Message.ERROR_MISSING_ARUGMENT.asString(plugin.getLocaleManager(), "/spread reset <player>"));
            return;
        }
        OfflinePlayer player = PlayerUtils.getOfflinePlayer(args[1]);
        if (player == null) {
            sender.sendMessage(Message.ERROR_PLAYER_NOT_FOUND.asString(plugin.getLocaleManager(), args[1]));
            return;
        }

        File file = new File(plugin.getUserManager().getFolder(), player.getUniqueId() + ".yml");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (NoSuchFileException e) {
            sender.sendMessage(Message.RESET_ERROR_MISSING.asString(plugin.getLocaleManager(), player.getName()));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "", e);
            sender.sendMessage(Message.ERROR_GENRIC.asString(plugin.getLocaleManager()));
        } finally {
            sender.sendMessage(Message.RESET.asString(plugin.getLocaleManager(), player.getName()));
        }
    }

    @Override
    public void onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("spread.reload")) {
                    onReload(sender);
                }
                break;
            case "reset":
                if (sender.hasPermission("spread.reset")) {
                    onReset(sender, args);
                }
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "reset")
                    .stream()
                    .filter(v -> sender.hasPermission("spread." + v))
                    .collect(Collectors.toList());
        } if (args.length > 1 && args[0].equals("reset") && sender.hasPermission("spread.reset")) {
            return Arrays.stream(Bukkit.getServer().getOfflinePlayers())
                    .map(p -> p.getName().toLowerCase())
                    .filter(v -> v.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
