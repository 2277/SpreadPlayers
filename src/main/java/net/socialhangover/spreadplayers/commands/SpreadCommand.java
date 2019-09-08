package net.socialhangover.spreadplayers.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpreadCommand extends BaseCommand {

    public SpreadCommand(SpreadPlugin plugin) {
        super(plugin);
    }

    public void onReload(CommandSender sender) {
        plugin.getConfiguration().reload();
        sender.sendMessage(Message.RELOAD.asString(plugin.getLocaleManager()));
    }

    @Override
    public void onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Message.ERROR_MISSING_ARUGMENT.asString(plugin.getLocaleManager(), "/spread <reload>"));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("spread.reload")) {
                    onReload(sender);
                }
                break;
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload")
                    .stream()
                    .filter(v -> sender.hasPermission("spread." + v))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
