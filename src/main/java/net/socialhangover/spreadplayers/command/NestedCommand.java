package net.socialhangover.spreadplayers.command;

import lombok.Getter;

import java.util.HashMap;
import java.util.stream.Stream;

public class NestedCommand {

    @Getter
    private final AbstractCommand parent;

    @Getter
    private final HashMap<String, AbstractCommand> children = new HashMap<>();

    public NestedCommand(AbstractCommand parent) {
        this.parent = parent;
    }

    public NestedCommand addSubCommand(final AbstractCommand command) {
        command.getCommands().forEach(c -> children.put(c.toLowerCase(), command));
        return this;
    }

    public NestedCommand addSubCommands(AbstractCommand... commands) {
        Stream.of(commands)
                .forEach(command -> command.getCommands().forEach(c -> children.put(c.toLowerCase(), command)));
        return this;
    }

}
