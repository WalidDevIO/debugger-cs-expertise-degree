package commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import dbg.ScriptableDebugger;

import java.util.List;

public class BreakOnCountCommand extends Command<Void> {

    public BreakOnCountCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        if (args.length < 3) {
            System.out.println("Error: filename, line number and count required");
            System.out.println("Usage: break-on-count <filename> <lineNumber> <count>");
            getDebugger().readCommand(event);
            return null;
        }

        String filename = args[0];
        int lineNumber;
        int count;

        try {
            lineNumber = Integer.parseInt(args[1]);
            count = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Error: line number and count must be integers");
            getDebugger().readCommand(event);
            return null;
        }

        if (count <= 0) {
            System.out.println("Error: count must be positive");
            getDebugger().readCommand(event);
            return null;
        }

        try {
            var vm = getVm();
            boolean found = false;

            for (ReferenceType refType : vm.allClasses()) {
                try {
                    String sourceName = refType.sourceName();
                    if (sourceName.equals(filename) || sourceName.endsWith("/" + filename)) {
                        List<Location> locations = refType.locationsOfLine(lineNumber);

                        if (!locations.isEmpty()) {
                            Location location = locations.get(0);
                            BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                            bpReq.addCountFilter(count); // S'active après count passages
                            bpReq.enable();

                            System.out.println("Count-based breakpoint set at " + filename + ":" + lineNumber +
                                    " (will break after " + count + " hits)");
                            found = true;
                            break;
                        }
                    }
                } catch (AbsentInformationException e) {
                    continue;
                }
            }

            if (!found) {
                System.out.println("Could not set breakpoint at " + filename + ":" + lineNumber);
            }

        } catch (Exception e) {
            System.out.println("Error setting count-based breakpoint: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "break-on-count";
    }

    @Override
    public String getDescription() {
        return "Installe un point d'arrêt conditionnel (usage: break-on-count <filename> <lineNumber> <count>)";
    }
}