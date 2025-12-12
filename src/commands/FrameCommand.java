package commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class FrameCommand extends Command {

    public FrameCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event, String[] args) {
        try {
            StackFrame frame = event.thread().frame(0);
            System.out.println("Current frame:");
            System.out.println("  Method: " + frame.location().method().name());
            System.out.println("  Class: " + frame.location().declaringType().name());
            try {
                System.out.println("  Line: " + frame.location().lineNumber());
                System.out.println("  Source: " + frame.location().sourceName());
            } catch (AbsentInformationException e) {
                System.out.println("  Line/Source info not available");
            }
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting frame: " + e.getMessage());
        }
        getDebugger().readCommand(event);
    }

    @Override
    public String getName() {
        return "frame";
    }

    @Override
    public String getDescription() {
        return "Affiche la frame courante";
    }
}