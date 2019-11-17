package net.socialhangover.spreadplayers.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.socialhangover.spreadplayers.SpreadPlugin;
import net.socialhangover.spreadplayers.locale.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {

    private final SpreadPlugin plugin;

    @Getter
    private final HashMap<String, NestedCommand> commands = new HashMap<>();
    private final boolean allowLooseCommands = false;

    public NestedCommand addCommand(AbstractCommand command) {
        NestedCommand nested = new NestedCommand(command);
        command.getCommands().forEach(c -> {
            commands.put(c.toLowerCase(), nested);
            PluginCommand pluginCommand = plugin.getCommand(c);
            if (pluginCommand != null) {
                pluginCommand.setExecutor(this);
                pluginCommand.setTabCompleter(this);
            } else {
                plugin.getLogger().warning("Failed to register command: /" + c);
            }
        });
        return nested;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        NestedCommand nested = commands.get(command.getName().toLowerCase());
        if (nested != null) {
            if (args.length != 0 && !nested.getChildren().isEmpty()) {
                String subCmd = getSubCommand(nested, args);
                if (subCmd != null) {
                    AbstractCommand sub = nested.getChildren().get(subCmd);
                    int i = subCmd.indexOf(' ') == -1 ? 1 : 2;
                    String[] newArgs = new String[args.length - i];
                    System.arraycopy(args, i, newArgs, 0, newArgs.length);
                    processRequirements(sub, sender, newArgs);
                    return true;
                }
            }
            if (nested.getParent() != null) {
                processRequirements(nested.getParent(), sender, args);
                return true;
            }
        }
        return true;
    }

    private String getSubCommand(NestedCommand nested, String[] args) {
        String cmd = args[0].toLowerCase();
        if (nested.getChildren().containsKey(cmd)) {
            return cmd;
        }

        String match = null;
        if (args.length >= 2 && nested.getChildren().keySet().stream().anyMatch(k -> k.indexOf(' ') != -1)) {
            cmd = String.join(" ", args[0], args[1]);
            if (nested.getChildren().containsKey(cmd)) return cmd;
        }
        if (allowLooseCommands) {
            int count = 0;
            for (String c : nested.getChildren().keySet()) {
                if (c.startsWith(cmd)) {
                    match = c;
                    if (++count > 1) {
                        match = null;
                        break;
                    }
                }
            }
        }
        return match;

    }

    private void processRequirements(AbstractCommand command, CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && command.isNoConsole()) {
            sender.sendMessage(plugin.getLocaleManager().get(Message.ERROR_PLAYER_ONLY));
            return;
        }
        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
            AbstractCommand.ReturnType type = command.runCommand(plugin, sender, args);
            if (type == AbstractCommand.ReturnType.SYNTAX_ERROR) {
                sender.sendMessage(plugin.getLocaleManager().get(Message.ERROR_MISSING_ARUGMENT, command.getSyntax()));
            } else if (type == AbstractCommand.ReturnType.NO_PERMISSION) {
                sender.sendMessage(plugin.getLocaleManager().get(Message.ERROR_PERMISSION));
            }
            return;
        }
        sender.sendMessage(plugin.getLocaleManager().get(Message.ERROR_PERMISSION));
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        NestedCommand nested = commands.get(command.getName().toLowerCase());
        if (nested != null) {
            if (args.length == 0) {
                return nested.getParent().onTab(plugin, sender, args);
            }
            boolean op = sender.isOp();
            boolean console = !(sender instanceof Player);

            if (nested.getChildren().isEmpty()) {
                AbstractCommand ac = nested.getParent();
                if ((!console || !ac.isNoConsole()) && (op || ac.getPermissionNode() == null || sender.hasPermission(ac.getPermissionNode()))) {
                    return fetchList(ac, args, sender);
                }
            } else if (args.length == 1) {
                String arg = args[0].toLowerCase();
                return nested.getChildren()
                        .entrySet()
                        .stream()
                        .filter(e -> !console || !e.getValue().isNoConsole())
                        .filter(e -> arg.isEmpty() || e.getKey().startsWith(arg))
                        .filter(e -> op || e.getValue().getPermissionNode() == null || sender.hasPermission(e.getValue()
                                .getPermissionNode()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            } else {
                String arg = getSubCommand(nested, args);
                AbstractCommand sub;
                if (arg != null && (sub = nested.getChildren()
                        .get(arg)) != null && (!console || !sub.isNoConsole()) && (op || sub.getPermissionNode() == null || sender
                        .hasPermission(sub.getPermissionNode()))) {
                    int i = arg.indexOf(' ') == -1 ? 1 : 2;
                    String[] newArgs = new String[args.length - i];
                    System.arraycopy(args, i, newArgs, 0, newArgs.length);
                    return fetchList(sub, newArgs, sender);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> fetchList(AbstractCommand abstractCommand, String[] args, CommandSender sender) {
        List<String> list = abstractCommand.onTab(plugin, sender, args);
        String str = args[args.length - 1];
        if (list != null && str != null && str.length() >= 1) {
            try {
                list.removeIf(s -> !s.toLowerCase().startsWith(str.toLowerCase()));
            } catch (UnsupportedOperationException ignored) {
            }
        }
        return list;
    }


}
