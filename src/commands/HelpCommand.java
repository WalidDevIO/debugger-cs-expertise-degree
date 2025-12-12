package commands;

import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class HelpCommand extends Command {

    private final CommandRegistry cr;

    public HelpCommand(ScriptableDebugger debugger, CommandRegistry cr) {
        super(debugger);
        this.cr = cr;
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
    public void execute(LocatableEvent event) {
        System.out.println("JAVA Debugger help menu");
        System.out.println("-----------------------");
        for(Command command : cr.getCommands()) {
            System.out.println(command.getName() + " : " + command.getDescription());
        }
    }

}
