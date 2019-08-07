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
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.TeleportManager;
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
                sender.sendMessage("Teleport request sent to " + target.getPlayer().getName());
                TextComponent textComponent = TextComponent.of(sender.getName() + " wishes to teleport to you.")
                        .append(TextComponent.of(" [ ✔ ]")
                                .hoverEvent(HoverEvent.showText(TextComponent.of("/tpaccept")))
                                .clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getUniqueId()))
                                .color(TextColor.GREEN)
                                .decoration(TextDecoration.BOLD, true))
                        .append(TextComponent.of(" [ ✘ ]")
                                .hoverEvent(HoverEvent.showText(TextComponent.of("/tpdeny")))
                                .clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getUniqueId()))
                                .color(TextColor.RED)
                                .decoration(TextDecoration.BOLD, true));
                TextAdapter.sendComponent(target.getPlayer(), textComponent);
                break;
            case ERROR_SELF:
                sender.sendMessage("You cannot teleport to you self.");
                break;
            default:
                sender.sendMessage("You can not longer teleport to other players.");
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
                sender.sendMessage(recipient.getName() + " is not online.");
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
                sender.sendMessage(player.getName() + " is not online.");
            }
        }
    }
}
