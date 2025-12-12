package commands;

import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class ArgumentsCommand extends Command<Void> {

    public ArgumentsCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        try {
            StackFrame frame = event.thread().frame(0);
            Method method = frame.location().method();

            System.out.println("Method arguments:");

            try {
                for (LocalVariable arg : method.arguments()) {
                    Value value = frame.getValue(arg);
                    System.out.println("  " + arg.name() + " → " + formatValue(value));
                }
            } catch (AbsentInformationException e) {
                System.out.println("Argument information not available (compile with -g)");
            }

        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting arguments: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return null;
    }

    private String formatValue(Value value) {
        if (value == null) return "null";
        if (value instanceof StringReference) {
            return "\"" + ((StringReference) value).value() + "\"";
        }
        return value.toString();
    }

    @Override
    public String getName() {
        return "arguments";
    }

    @Override
    public String getDescription() {
        return "Affiche les arguments de la méthode courante";
    }
}