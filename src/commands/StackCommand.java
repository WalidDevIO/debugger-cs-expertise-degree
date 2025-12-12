package commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

import java.util.ArrayList;
import java.util.List;

public class StackCommand extends Command<List<StackFrame>> {

    public StackCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public List<StackFrame> execute(LocatableEvent event, String[] args) {
        List<StackFrame> stackFrames = new ArrayList<>();

        try {
            System.out.println("Call stack:");
            int depth = 0;
            for (StackFrame frame : event.thread().frames()) {
                stackFrames.add(frame);
                String methodName = frame.location().method().name();
                String className = frame.location().declaringType().name();

                int lineNumber = frame.location().lineNumber();
                System.out.println("  #" + depth + " " + className + "." + methodName + "() line " + lineNumber);
                depth++;
            }
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting stack: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return stackFrames;
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