package commands;

import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

import java.util.HashMap;
import java.util.Map;

public class TemporariesCommand extends Command<Map<String, Value>> {

    public TemporariesCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Map<String, Value> execute(LocatableEvent event, String[] args) {
        Map<String, Value> temporaries = new HashMap<>();

        try {
            StackFrame frame = event.thread().frame(0);
            System.out.println("Temporary variables:");

            for (LocalVariable var : frame.visibleVariables()) {
                Value value = frame.getValue(var);
                temporaries.put(var.name(), value);
                System.out.println("  " + var.name() + " → " + formatValue(value));
            }
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (AbsentInformationException e) {
            System.out.println("Local variable information not available");
        } catch (Exception e) {
            System.out.println("Error getting temporaries: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return temporaries;
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
        return "temporaries";
    }

    @Override
    public String getDescription() {
        return "Affiche les variables temporaires de la frame courante";
    }
}