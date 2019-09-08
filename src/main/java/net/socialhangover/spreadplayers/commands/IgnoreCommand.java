package net.socialhangover.spreadplayers.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.UserData;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand extends BaseCommand {
    public IgnoreCommand(SpreadPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Message.ERROR_PLAYER_ONLY.asString(plugin.getLocaleManager()));
            return;
        }

        UserData userData = plugin.getUser(((Player) sender).getUniqueId());
        boolean ignore = args.length > 0 && Boolean.parseBoolean(args[0]) || !userData.isIgnoring();
        userData.setIgnoring(ignore);
        userData.save();

        sender.sendMessage((ignore ? Message.IGNORE_DISABLED : Message.IGNORE_ENABLED).asString(plugin.getLocaleManager()));
    }

}
