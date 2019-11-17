package net.socialhangover.spreadplayers.command.commands;

import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
import net.socialhangover.spreadplayers.UserData;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import net.socialhangover.spreadplayers.config.ConfigKeys;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TpacceptCommand extends AbstractCommand {

    public TpacceptCommand() {
        super(true, "tpaccept");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        UUID source = null;
        if (args.length > 0) {
            try {
                source = UUID.fromString(args[0]);
            } catch (Exception e) { /* ignore */ }
        }

        Player self = (Player) sender;
        if (plugin.getTeleportManager().has(self.getUniqueId(), source)) {
            UUID request = plugin.getTeleportManager().get(self.getUniqueId());
            OfflinePlayer recipient = Bukkit.getServer().getOfflinePlayer(request);
            plugin.getTeleportManager().killRequest(self, TeleportManager.KillReason.ACCEPTED);
            if (recipient.isOnline()) {
                Player player = (Player) recipient;
                Bukkit.getServer()
                        .dispatchCommand(Bukkit.getConsoleSender(), String.format("spreadplayers %d %d %d %d %b %s", self
                                .getLocation()
                                .getBlockX(), self.getLocation().getBlockZ(), 0, plugin.getConfiguration()
                                .get(ConfigKeys.TELEPORT_SPREAD), false, player.getName()));

                UserData userData = plugin.getUser(player.getUniqueId());
                if (plugin.getConfiguration().get(ConfigKeys.OVERWRITE_SPAWN_LOCATION_ON_TELEPORT)) {
                    userData.setSpawnLocation(player.getLocation());
                }
                userData.setTeleports(userData.getTeleports() + 1);
                userData.save();

                sender.sendMessage(Message.TELEPORT_ACCEPT_TARGET.asString(plugin.getLocaleManager(), recipient.getName()));
                player.sendMessage(Message.TELEPORT_ACCEPT_RECIPIENT.asString(plugin.getLocaleManager(), sender.getName()));
            } else {
                sender.sendMessage(Message.TELEPORT_ERROR_OFFLINE.asString(plugin.getLocaleManager(), recipient.getName()));
            }
            return ReturnType.SUCCESS;
        }

        sender.sendMessage(Message.TELEPORT_ERROR_EXPIRED.asString(plugin.getLocaleManager()));
        return ReturnType.FAILURE;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "spread.tpaccept";
    }

    @Override
    public String getSyntax() {
        return "/tpaccept [id]";
    }
}
