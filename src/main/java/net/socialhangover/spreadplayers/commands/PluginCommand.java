package net.socialhangover.spreadplayers.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.Plugin;
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
public class PluginCommand extends BaseCommand {

    private final Plugin plugin;

    @Subcommand("reload")
    @CommandPermission("spread.reload")
    public void onReload(CommandSender sender) {
        plugin.getConfiguration().reload();
    }

    @Subcommand("reset")
    @CommandPermission("spread.reset")
    public void onReset(CommandSender sender, OfflinePlayer player) {
        File file = new File(plugin.getUserManager().getFolder(), player.getUniqueId() + ".yml");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (NoSuchFileException e) {
            sender.sendMessage(player.getName() + "'s userdata was missing or could not be found.");
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "", e);
            sender.sendMessage("Unexpected error: check console for details.");
        } finally {
            sender.sendMessage(player.getName() + "'s userdata was successfully reset.");
        }
    }

}
