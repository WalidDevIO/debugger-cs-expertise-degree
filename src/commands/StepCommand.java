package commands;

import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.StepRequest;
import dbg.ScriptableDebugger;

public class StepCommand extends Command<Void> {

    public StepCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public Void execute(LocatableEvent event, String[] args) {
        try {
            var vm = getVm();
            vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());

            StepRequest stepRequest = vm.eventRequestManager()
                .createStepRequest(
                    event.thread(),
                    StepRequest.STEP_MIN,
                    StepRequest.STEP_INTO
                );
            stepRequest.enable();

            System.out.println("Step into enabled");
        } catch (Exception e) {
            System.out.println("Error executing step: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getName() {
        return "step";
    }

    @Override
    public String getDescription() {
        return "Mode pas à pas détaillé";
    }
}
