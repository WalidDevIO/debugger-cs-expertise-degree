package commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import dbg.ScriptableDebugger;

public class BreakpointsCommand extends Command<Void> {

    public BreakpointsCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        try {
            var breakpoints = getVm().eventRequestManager().breakpointRequests();

            if (breakpoints.isEmpty()) {
                System.out.println("No active breakpoints");
                getDebugger().readCommand(event);
                return null;
            }

            System.out.println("Active breakpoints:");
            int index = 1;

            for (BreakpointRequest bp : breakpoints) {
                Location location = bp.location();
                String className = location.declaringType().name();
                String methodName = location.method().name();

                try {
                    String sourceName = location.sourceName();
                    int lineNumber = location.lineNumber();
                    System.out.println("  #" + index + " " + sourceName + ":" + lineNumber +
                            " (" + className + "." + methodName + ")");
                } catch (AbsentInformationException e) {
                    System.out.println("  #" + index + " " + className + "." + methodName +
                            " (line info not available)");
                }
                index++;
            }
        } catch (Exception e) {
            System.out.println("Error listing breakpoints: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "breakpoints";
    }

    @Override
    public String getDescription() {
        return "Liste les points d'arrêt actifs";
    }
}