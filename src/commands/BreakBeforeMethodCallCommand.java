package commands;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import dbg.ScriptableDebugger;

import java.util.List;

public class BreakBeforeMethodCallCommand extends Command<Void> {

    public BreakBeforeMethodCallCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        if (args.length == 0) {
            System.out.println("Error: method signature required");
            System.out.println("Usage: break-before-method-call <ClassName.methodName>");
            getDebugger().readCommand(event);
            return null;
        }

        String methodSignature = args[0];

        // Parser ClassName.methodName
        if (!methodSignature.contains(".")) {
            System.out.println("Error: Invalid format. Use ClassName.methodName");
            getDebugger().readCommand(event);
            return null;
        }

        int lastDotIndex = methodSignature.lastIndexOf(".");
        String className = methodSignature.substring(0, lastDotIndex);
        String methodName = methodSignature.substring(lastDotIndex + 1);

        try {
            var vm = getVm();
            boolean found = false;

            // Chercher la classe spécifique
            for (ReferenceType refType : vm.allClasses()) {
                // Matcher le nom de classe (avec ou sans package)
                if (refType.name().equals(className) || refType.name().endsWith("." + className)) {
                    // Chercher la méthode dans cette classe
                    for (Method method : refType.methods()) {
                        if (method.name().equals(methodName)) {
                            try {
                                List<Location> locations = method.allLineLocations();

                                if (!locations.isEmpty()) {
                                    Location firstLocation = locations.get(0);

                                    BreakpointRequest bpReq = vm.eventRequestManager()
                                            .createBreakpointRequest(firstLocation);
                                    bpReq.enable();

                                    System.out.println("Breakpoint set on entry to " +
                                            refType.name() + "." + methodName +
                                            " at line " + firstLocation.lineNumber());
                                    found = true;
                                }
                            } catch (AbsentInformationException e) {
                                System.out.println("Method " + refType.name() + "." + methodName +
                                        " found but has no debug info");
                            }
                        }
                    }
                }
            }

            if (!found) {
                System.out.println("Method '" + className + "." + methodName +
                        "' not found in loaded classes");
            }

        } catch (Exception e) {
            System.out.println("Error setting method entry breakpoint: " + e.getMessage());
        }

        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "break-before-method-call";
    }

    @Override
    public String getDescription() {
        return "S'arrête au début de l'exécution d'une méthode (usage: break-before-method-call <ClassName.methodName>)";
    }
}