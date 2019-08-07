package net.socialhangover.spreadplayers.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
import net.socialhangover.spreadplayers.locale.message.Message;
import net.socialhangover.spreadplayers.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpacceptCommand extends BaseCommand {

    public TpacceptCommand(SpreadPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.ERROR_PLAYER_ONLY.asString(plugin.getLocaleManager()));
            return;
        }
        if (!sender.hasPermission("spread.tpaccept")) {
            sender.sendMessage(Message.ERROR_PERMISSION.asString(plugin.getLocaleManager()));
            return;
        }

        UUID source = null;
        if (args.length > 0) {
            try {
                source = UUID.fromString(args[0]);
            } catch (Exception e) {
                // ignore
            }
        }

        Player self = (Player) sender;
        if (plugin.getTeleportManager().has(self.getUniqueId(), source)) {
            UUID request = plugin.getTeleportManager().get(self.getUniqueId());
            OfflinePlayer recipient = Bukkit.getServer().getOfflinePlayer(request);
            plugin.getTeleportManager().killRequest(self, TeleportManager.KillReason.ACCEPTED);
            if (recipient.isOnline()) {
                ((Player) recipient).teleport(self);
                UserData userData = plugin.getUserManager().load(recipient.getUniqueId());
                if (userData == null) {
                    return;
                }
                userData.TELEPORTS.set(userData.TELEPORTS.get() + 1);
                userData.save();
            } else {
                sender.sendMessage(Message.TELEPORT_ERROR_OFFLINE.asString(plugin.getLocaleManager(), recipient.getName()));
            }
        }
    }
}
