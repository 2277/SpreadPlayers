package net.socialhangover.spreadplayers.commands;

import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.SpreadPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final SpreadPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        onCommandExecute(sender, command, label, args);
        return true;
    }

    public abstract void onCommandExecute(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return getTabComplete(sender, command, alias, args).stream()
                .filter(s -> s.startsWith(args[args.length - 1]))
                .collect(Collectors.toList());
    }

    public List<String> getTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    public List<String> getPlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
    }
}
