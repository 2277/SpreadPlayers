package net.socialhangover.spreadplayers.command.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TpdenyCommand extends AbstractCommand {

    public TpdenyCommand() {
        super(true, "tpdeny");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        UUID source = null;
        if (args.length > 0) {
            try {
                source = UUID.fromString(args[0]);
            } catch (Exception ignored) { }
        }

        Player self = (Player) sender;
        if (plugin.getTeleportManager().has(self.getUniqueId(), source)) {
            UUID request = plugin.getTeleportManager().get(self.getUniqueId());
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(request);
            plugin.getTeleportManager().killRequest(self, TeleportManager.KillReason.DENIED);
            if (!player.isOnline()) {
                sender.sendMessage(Message.TELEPORT_ERROR_OFFLINE.asString(plugin.getLocaleManager(), player.getName()));
                return ReturnType.FAILURE;
            }
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "spread.tpdeny";
    }

    @Override
    public String getSyntax() {
        return "/tpadeny [id]";
    }
}
