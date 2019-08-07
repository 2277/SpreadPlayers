package net.socialhangover.spreadplayers.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
import net.socialhangover.spreadplayers.locale.message.Message;
import net.socialhangover.spreadplayers.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class TpaCommand extends BaseCommand {

    private final SpreadPlugin plugin;

    @CommandAlias("tpa")
    @CommandPermission("spread.tpa")
    @CommandCompletion("@players")
    public void onTeleportRequested(Player sender, OnlinePlayer target) {
        TeleportManager.RequestResult result = plugin.getTeleportManager().makeRequest(sender, target.getPlayer());
        switch (result) {
            case SUCCESS:
                sender.sendMessage(Message.TELEPORT_REQUEST.asString(plugin.getLocaleManager(), target.getPlayer()
                        .getName()));
                TextComponent textComponent = TextComponent.of(Message.TELEPORT_REQUEST_OTHER.asString(plugin.getLocaleManager(), sender
                        .getName()))
                        .append(TextComponent.of(Message.TELEPORT_CLICK_ACCEPT.asString(plugin.getLocaleManager()))
                                .hoverEvent(HoverEvent.showText(TextComponent.of(Message.TELEPORT_HOVER_ACCEPT.asString(plugin
                                        .getLocaleManager()))))
                                .clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getUniqueId())))
                        .append(TextComponent.of(Message.TELEPORT_CLICK_DENY.asString(plugin.getLocaleManager()))
                                .hoverEvent(HoverEvent.showText(TextComponent.of(Message.TELEPORT_HOVER_ACCEPT.asString(plugin
                                        .getLocaleManager()))))
                                .clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getUniqueId())));
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

    @CommandAlias("tpaccept")
    public void onTeleportAccept(Player sender, @Optional UUID source) {
        if (plugin.getTeleportManager().has(sender.getUniqueId(), source)) {
            UUID request = plugin.getTeleportManager().get(sender.getUniqueId());
            OfflinePlayer recipient = Bukkit.getServer().getOfflinePlayer(request);
            plugin.getTeleportManager().killRequest(sender, TeleportManager.KillReason.ACCEPTED);
            if (recipient.isOnline()) {
                ((Player) recipient).teleport(sender);
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

    @CommandAlias("tpdeny")
    public void onTeleportDeny(Player sender, @Optional UUID source) {
        if (plugin.getTeleportManager().has(sender.getUniqueId(), source)) {
            UUID request = plugin.getTeleportManager().get(sender.getUniqueId());
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(request);
            plugin.getTeleportManager().killRequest(sender, TeleportManager.KillReason.DENIED);
            if (!player.isOnline()) {
                sender.sendMessage(Message.TELEPORT_ERROR_OFFLINE.asString(plugin.getLocaleManager(), player.getName()));
            }
        }
    }
}
