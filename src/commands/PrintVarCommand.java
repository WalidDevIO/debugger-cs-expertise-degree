package commands;

import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class PrintVarCommand extends Command {

    public PrintVarCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event, String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Variable name required");
            System.out.println("Usage: print-var <varName>");
            getDebugger().readCommand(event);
            return;
        }

        String varName = args[0];

        try {
            StackFrame frame = event.thread().frame(0);

            // Chercher dans les variables locales
            try {
                LocalVariable localVar = frame.visibleVariableByName(varName);
                if (localVar != null) {
                    Value value = frame.getValue(localVar);
                    System.out.println(varName + " = " + formatValue(value));
                    return;
                }
            } catch (AbsentInformationException e) {
                // Pas d'info sur les variables locales, continuer avec les fields
            }

            // Chercher dans les variables d'instance
            ObjectReference thisObject = frame.thisObject();
            if (thisObject != null) {
                Field field = thisObject.referenceType().fieldByName(varName);
                if (field != null) {
                    Value value = thisObject.getValue(field);
                    System.out.println(varName + " = " + formatValue(value));
                    getDebugger().readCommand(event);
                    return;
                }
            }

            System.out.println("Variable '" + varName + "' not found");

        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error printing variable: " + e.getMessage());
        }

        getDebugger().readCommand(event);
    }

    private String formatValue(Value value) {
        if (value == null) return "null";
        if (value instanceof StringReference) {
            return "\"" + ((StringReference) value).value() + "\"";
        }
        if (value instanceof ArrayReference) {
            ArrayReference array = (ArrayReference) value;
            return "Array[" + array.length() + "]";
        }
        if (value instanceof ObjectReference) {
            ObjectReference obj = (ObjectReference) value;
            return obj.referenceType().name() + "@" + obj.uniqueID();
        }
        return value.toString();
    }

    @Override
    public String getName() {
        return "print-var";
    }

    @Override
    public String getDescription() {
        return "Imprime la valeur d'une variable (usage: print-var <varName>)";
    }
}