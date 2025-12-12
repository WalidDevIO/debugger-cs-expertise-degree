package commands;

import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class ContinueCommand extends Command {

    public ContinueCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event) {
        try {
            var vm = getVm();
            vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());
            System.out.println("Continue - execution will resume until next breakpoint");
            vm.resume();
        } catch (Exception e) {
            System.out.println("Error executing continue: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "continue";
    }

    @Override
    public String getDescription() {
        return "Reprendre l'execution jusqu'au prochain point d'arrêt";
    }
}
