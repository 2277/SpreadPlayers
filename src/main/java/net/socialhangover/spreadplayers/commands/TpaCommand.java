package net.socialhangover.spreadplayers.commands;

import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TpaCommand extends BaseCommand {

    public TpaCommand(SpreadPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.ERROR_PLAYER_ONLY.asString(plugin.getLocaleManager()));
            return;
        }
        if (!sender.hasPermission("spread.tpa")) {
            sender.sendMessage(Message.ERROR_PERMISSION.asString(plugin.getLocaleManager()));
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(Message.ERROR_MISSING_ARUGMENT.asString(plugin.getLocaleManager(), "/tpa <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.ERROR_PLAYER_NOT_FOUND.asString(plugin.getLocaleManager()));
            return;
        }
        Player self = (Player) sender;

        TeleportManager.RequestResult result = plugin.getTeleportManager().makeRequest(self, target.getPlayer());
        switch (result) {
            case SUCCESS:
                sender.sendMessage(Message.TELEPORT_REQUEST.asString(plugin.getLocaleManager(), target.getPlayer()
                        .getName()));
                TextComponent textComponent = TextComponent.of(Message.TELEPORT_REQUEST_OTHER.asString(plugin.getLocaleManager(), sender
                        .getName()))
                        .append(TextComponent.of(Message.TELEPORT_CLICK_ACCEPT.asString(plugin.getLocaleManager()))
                                .hoverEvent(HoverEvent.showText(TextComponent.of(Message.TELEPORT_HOVER_ACCEPT.asString(plugin
                                        .getLocaleManager()))))
                                .clickEvent(ClickEvent.runCommand("/tpaccept " + self.getUniqueId())))
                        .append(TextComponent.of(Message.TELEPORT_CLICK_DENY.asString(plugin.getLocaleManager()))
                                .hoverEvent(HoverEvent.showText(TextComponent.of(Message.TELEPORT_HOVER_DENY.asString(plugin
                                        .getLocaleManager()))))
                                .clickEvent(ClickEvent.runCommand("/tpdeny " + self.getUniqueId())));
                TextAdapter.sendComponent(target.getPlayer(), textComponent);
                break;
            case ERROR_SELF:
                sender.sendMessage(Message.TELEPORT_ERROR_SELF.asString(plugin.getLocaleManager()));
                break;
            case ERROR_LIMIT:
                sender.sendMessage(Message.TELEPORT_ERROR_LIMIT.asString(plugin.getLocaleManager()));
                break;
            case ERROR_PLAYTIME:
                sender.sendMessage(Message.TELEPORT_ERROR_PLAYTIME.asString(plugin.getLocaleManager()));
                break;
            default:
                sender.sendMessage(Message.TELEPORT_ERROR_GENERIC.asString(plugin.getLocaleManager()));
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? getPlayers() : Collections.emptyList();
    }
}
