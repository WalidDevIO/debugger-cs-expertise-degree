package commands;

import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class HelpCommand extends Command<Void> {

    public HelpCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Affiche l'aide du debugger";
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        System.out.println("JAVA Debugger help menu");
        System.out.println("-----------------------");
        for(Command<?> command : getDebugger().getCommandRegistry().getCommands()) {
            System.out.println(command.getName() + " : " + command.getDescription());
        }
        getDebugger().readCommand(event);
        return null;
    }

}
