package commands;

import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.StepRequest;
import dbg.ScriptableDebugger;

public class StepCommand extends Command {

    public StepCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event) {
        try {
            var vm = getVm();
            vm.eventRequestManager().deleteEventRequests(vm.eventRequestManager().stepRequests());

            StepRequest stepRequest = vm.eventRequestManager()
                .createStepRequest(
                    event.thread(),
                    StepRequest.STEP_LINE,
                    StepRequest.STEP_INTO
                );
            stepRequest.enable();

            System.out.println("Step into enabled");
            vm.resume();
        } catch (Exception e) {
            System.out.println("Error executing step: " + e.getMessage());
        }
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
