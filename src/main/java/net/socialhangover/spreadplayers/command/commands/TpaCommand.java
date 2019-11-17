package net.socialhangover.spreadplayers.command.commands;

import lombok.Data;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
import net.socialhangover.spreadplayers.command.AbstractCommand;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class TpaCommand extends AbstractCommand {

    private static final Map<UUID, UUID> requests = new HashMap<>();

    public TpaCommand() {
        super(true, "tpa");
    }

    @Override
    protected ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args) {
        Player self = (Player) sender;

        Player target;
        if (args.length < 1 && requests.containsKey(self.getUniqueId())) {
            target = Bukkit.getPlayer(requests.get(self.getUniqueId()));
        } else if (args.length < 1) {
            return ReturnType.SYNTAX_ERROR;
        } else {
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            sender.sendMessage(Message.ERROR_PLAYER_NOT_FOUND.asString(plugin.getLocaleManager()));
            return ReturnType.FAILURE;
        }

        if (plugin.getUser(target.getUniqueId()).isIgnoring() && !sender.hasPermission("spread.ignore.bypass")) {
            sender.sendMessage(Message.ERROR_PLAYER_IGNORING.asString(plugin.getLocaleManager(), target.getName()));
            return ReturnType.FAILURE;
        }

        if (!requests.containsKey(self.getUniqueId()) || !requests.get(self.getUniqueId())
                .equals(target.getUniqueId())) {
            self.sendMessage(Message.TELEPORT_WARNING.asString(plugin.getLocaleManager(), target.getName(), target.getName()));
            requests.put(self.getUniqueId(), target.getUniqueId());
            return ReturnType.SUCCESS;
        } else {
            requests.remove(self.getUniqueId());
        }

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
            case ERROR_ONLY_OVERWORLD:
                sender.sendMessage(Message.TELEPORT_ERROR_OVERWORLD.asString(plugin.getLocaleManager(), target.getName()));
                break;
            default:
                sender.sendMessage(Message.TELEPORT_ERROR_GENERIC.asString(plugin.getLocaleManager()));
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args) {
        return args.length == 1 ? getOnlinePlayers() : Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "spread.tpa";
    }

    @Override
    public String getSyntax() {
        return "/tpa <player>";
    }

    @Data
    public static class RequestConfirmation {
        public UUID recipient;
        public long timestamp;
    }

}
