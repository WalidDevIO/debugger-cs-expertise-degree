package commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import dbg.ScriptableDebugger;

import java.util.List;

public class BreakCommand extends Command<Void> {

    public BreakCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        if (args.length < 2) {
            System.out.println("Error: filename and line number required");
            System.out.println("Usage: break <filename> <lineNumber>");
            getDebugger().readCommand(event);
            return null;
        }

        String filename = args[0];
        int lineNumber;

        try {
            lineNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error: line number must be an integer");
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
                            bpReq.enable();

                            System.out.println("Breakpoint set at " + filename + ":" + lineNumber);
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
                System.out.println("Make sure the file is loaded and compiled with debug info (-g)");
            }

        } catch (Exception e) {
            System.out.println("Error setting breakpoint: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "break";
    }

    @Override
    public String getDescription() {
        return "Installe un point d'arrêt (usage: break <filename> <lineNumber>)";
    }
}