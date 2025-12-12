package commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class StackCommand extends Command {

    public StackCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event) {
        try {
            System.out.println("Call stack:");
            int depth = 0;
            for (StackFrame frame : event.thread().frames()) {
                String methodName = frame.location().method().name();
                String className = frame.location().declaringType().name();

                int lineNumber = frame.location().lineNumber();
                System.out.println("  #" + depth + " " + className + "." + methodName + "() line " + lineNumber);
                depth++;
            }
            dbg.readCommand(event);
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting stack: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "stack";
    }

    @Override
    public String getDescription() {
        return "Affiche la pile d'appel";
    }
}