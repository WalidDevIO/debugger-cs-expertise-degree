package commands;

import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class ReceiverVariablesCommand extends Command<Void> {

    public ReceiverVariablesCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        try {
            StackFrame frame = event.thread().frame(0);
            ObjectReference thisObject = frame.thisObject();

            if (thisObject == null) {
                System.out.println("No receiver (static method)");
                getDebugger().readCommand(event);
                return null;
            }

            System.out.println("Receiver instance variables:");
            ReferenceType refType = thisObject.referenceType();

            for (Field field : refType.allFields()) {
                if (!field.isStatic()) {
                    Value value = thisObject.getValue(field);
                    System.out.println("  " + field.name() + " → " + formatValue(value));
                }
            }
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting receiver variables: " + e.getMessage());
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
        return "receiver-variables";
    }

    @Override
    public String getDescription() {
        return "Affiche les variables d'instance du receveur courant";
    }
}