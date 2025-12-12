package commands;

import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.MethodEntryRequest;
import dbg.ScriptableDebugger;

public class BreakBeforeMethodCallCommand extends Command {

    public BreakBeforeMethodCallCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event, String[] args) {
        if (args.length == 0) {
            System.out.println("Error: method name required");
            System.out.println("Usage: break-before-method-call <methodName>");
            getDebugger().readCommand(event);
            return;
        }

        String methodName = args[0];

        try {
            var vm = getVm();

            // Créer une MethodEntryRequest globale avec filtre sur les classes
            MethodEntryRequest methodEntry = vm.eventRequestManager().createMethodEntryRequest();

            // Chercher les classes qui ont cette méthode pour créer des filtres
            boolean found = false;
            for (ReferenceType refType : vm.allClasses()) {
                for (Method method : refType.methods()) {
                    if (method.name().equals(methodName)) {
                        methodEntry.addClassFilter(refType);
                        found = true;
                        System.out.println("Will break on entry to " + refType.name() + "." + methodName);
                    }
                }
            }

            if (found) {
                methodEntry.enable();
                System.out.println("Method entry breakpoint enabled for: " + methodName);
            } else {
                System.out.println("Method '" + methodName + "' not found in loaded classes");
                vm.eventRequestManager().deleteEventRequest(methodEntry);
            }

        } catch (Exception e) {
            System.out.println("Error setting method entry breakpoint: " + e.getMessage());
        }

        getDebugger().readCommand(event);
    }

    @Override
    public String getName() {
        return "break-before-method-call";
    }

    @Override
    public String getDescription() {
        return "S'arrête au début de l'exécution d'une méthode (usage: break-before-method-call <methodName>)";
    }
}