package net.socialhangover.spreadplayers.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.logging.Level;

@RequiredArgsConstructor
@CommandAlias("%rootcommand")
public class SpreadCommand extends BaseCommand {

    private final SpreadPlugin plugin;

    @Subcommand("reload")
    @CommandPermission("spread.reload")
    public void onReload(CommandSender sender) {
        plugin.getConfiguration().reload();
        sender.sendMessage(Message.RELOAD.asString(plugin.getLocaleManager()));
    }

    @Subcommand("reset")
    @CommandPermission("spread.reset")
    public void onReset(CommandSender sender, OfflinePlayer player) {
        File file = new File(plugin.getUserManager().getFolder(), player.getUniqueId() + ".yml");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (NoSuchFileException e) {
            sender.sendMessage(Message.RESET_ERROR_MISSING.asString(plugin.getLocaleManager(), player.getName()));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "", e);
            sender.sendMessage(Message.ERROR_GENRIC.asString(plugin.getLocaleManager()));
        } finally {
            sender.sendMessage(Message.RESET.asString(plugin.getLocaleManager(), player.getName()));
        }
    }

}
