package net.socialhangover.spreadplayers.command.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import net.socialhangover.spreadplayers.listeners.PlayerListener;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ResetCommand extends AbstractCommand {

    public ResetCommand() {
        super(false, "reset");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        if (args.length == 0) {
            return ReturnType.SYNTAX_ERROR;
        }

        OfflinePlayer offlinePlayer = getPlayer(args[0]);
        if (offlinePlayer == null) {
            sender.sendMessage(plugin.getLocaleManager().get(Message.ERROR_PLAYER_NOT_FOUND));
            return ReturnType.FAILURE;
        }
        if (offlinePlayer.isOnline()) {
            plugin.spread(offlinePlayer.getPlayer());
            sender.sendMessage(plugin.getLocaleManager().get(Message.RESET_ONLINE, offlinePlayer.getName()));
        } else {
            PlayerListener.getReset().add(offlinePlayer.getUniqueId());
            sender.sendMessage(plugin.getLocaleManager().get(Message.RESET_OFFLINE, offlinePlayer.getName()));
        }

        return ReturnType.SUCCESS;
    }

    public OfflinePlayer getPlayer(String name) {
        OfflinePlayer player = Bukkit.getServer().getPlayer(name);
        if (player == null) {
            player = Bukkit.getServer().getOfflinePlayer(name);
        }
        return player.hasPlayedBefore() ? player : null;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return args.length == 1 ? getOnlinePlayers() : Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "spread.reset";
    }

    @Override
    public String getSyntax() {
        return "/spread reset";
    }
}
