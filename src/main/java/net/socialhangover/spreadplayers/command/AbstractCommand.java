package net.socialhangover.spreadplayers.command;

import lombok.Getter;
import net.socialhangover.spreadplayers.SpreadPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCommand {

    protected final List<String> commands = new ArrayList<>();
    @Getter
    private final boolean noConsole;
    private final boolean hasArgs;

    protected AbstractCommand(boolean noConsole, String... command) {
        this(noConsole, false, command);
    }

    protected AbstractCommand(boolean noConsole, boolean hasArgs, String... command) {
        this.noConsole = noConsole;
        this.hasArgs = hasArgs;
        this.commands.addAll(Arrays.asList(command));
    }

    public final List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    protected abstract ReturnType runCommand(SpreadPlugin plugin, CommandSender sender, String... args);

    protected abstract List<String> onTab(SpreadPlugin plugin, CommandSender sender, String... args);

    public abstract String getPermissionNode();

    protected List<String> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }

    protected Optional<? extends Player> getOnlinePlayer(String name) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getDisplayName().equalsIgnoreCase(name) || p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public abstract String getSyntax();

    public enum ReturnType {
        SUCCESS,
        FAILURE,
        SYNTAX_ERROR,
        NO_PERMISSION
    }


}
