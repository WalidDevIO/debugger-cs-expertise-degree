package commands;

import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class ContinueCommand extends Command<Void> {

    public ContinueCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        try {
            var vm = getVm();
            vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());
            System.out.println("Continue - execution will resume until next breakpoint");
        } catch (Exception e) {
            System.out.println("Error executing continue: " + e.getMessage());
        }
        return null;
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
