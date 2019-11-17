package net.socialhangover.spreadplayers.command.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends AbstractCommand {
    public ReloadCommand() {
        super(false, "reload");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        plugin.getConfiguration().reload();
        sender.sendMessage(Message.RELOAD.asString(plugin.getLocaleManager()));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "spread.reload";
    }

    @Override
    public String getSyntax() {
        return "/spread reload";
    }
}
