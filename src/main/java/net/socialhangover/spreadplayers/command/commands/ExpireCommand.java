package net.socialhangover.spreadplayers.command.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ExpireCommand extends AbstractCommand {

    public ExpireCommand() {
        super(false, "expire");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        plugin.getSpreadCache().reset();
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "spread.expire";
    }

    @Override
    public String getSyntax() {
        return "/spread expire";
    }
}
