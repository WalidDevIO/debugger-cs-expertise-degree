package commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class MethodCommand extends Command<Void> {

    public MethodCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        try {
            StackFrame frame = event.thread().frame(0);
            Method method = frame.location().method();

            System.out.println("Current method:");
            System.out.println("  Name: " + method.name());
            System.out.println("  Signature: " + method.signature());
            System.out.println("  Declaring type: " + method.declaringType().name());
            System.out.println("  Return type: " + method.returnTypeName());
            System.out.println("  Is static: " + method.isStatic());
            System.out.println("  Is constructor: " + method.isConstructor());
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting method: " + e.getMessage());
        }
        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "method";
    }

    @Override
    public String getDescription() {
        return "Affiche la méthode en cours d'exécution";
    }
}