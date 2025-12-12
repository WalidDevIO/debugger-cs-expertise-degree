package commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command<?>> commands = new HashMap<>();

    public void register(Command<?> command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public Command<?> getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }

    public List<Command<?>> getCommands() {
        return  commands.values().stream().toList();
    }
}
