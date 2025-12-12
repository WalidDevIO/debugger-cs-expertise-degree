package commands;

import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

import java.util.HashMap;
import java.util.Map;

public class ReceiverVariablesCommand extends Command<Map<String, Value>> {

    public ReceiverVariablesCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Map<String, Value> execute(LocatableEvent event, String[] args) {
        Map<String, Value> variables = new HashMap<>();

        try {
            StackFrame frame = event.thread().frame(0);
            ObjectReference thisObject = frame.thisObject();

            if (thisObject == null) {
                System.out.println("No receiver (static method)");
                getDebugger().readCommand(event);
                return variables;
            }

            System.out.println("Receiver instance variables:");
            ReferenceType refType = thisObject.referenceType();

            for (Field field : refType.allFields()) {
                if (!field.isStatic()) {
                    Value value = thisObject.getValue(field);
                    variables.put(field.name(), value);
                    System.out.println("  " + field.name() + " → " + formatValue(value));
                }
            }
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting receiver variables: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return variables;
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
        return "receiver-variables";
    }

    @Override
    public String getDescription() {
        return "Affiche les variables d'instance du receveur courant";
    }
}