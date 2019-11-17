package net.socialhangover.spreadplayers.command.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.UserData;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class IgnoreCommand extends AbstractCommand {

    public IgnoreCommand() {
        super(true, "tpignore");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        UserData userData = plugin.getUser(((Player) sender).getUniqueId());
        boolean ignore = args.length > 0 && Boolean.parseBoolean(args[0]) || !userData.isIgnoring();
        userData.setIgnoring(ignore);
        userData.save();

        sender.sendMessage((ignore ? Message.IGNORE_DISABLED : Message.IGNORE_ENABLED).asString(plugin.getLocaleManager()));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "spread.tpignore";
    }

    @Override
    public String getSyntax() {
        return "/tpignore";
    }
}
