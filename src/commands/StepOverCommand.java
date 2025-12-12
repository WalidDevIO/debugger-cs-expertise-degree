package commands;

import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.StepRequest;
import dbg.ScriptableDebugger;

public class StepOverCommand extends Command<Void> {

    public StepOverCommand(ScriptableDebugger debugger) {
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
                    StepRequest.STEP_LINE,
                    StepRequest.STEP_OVER
                );
            stepRequest.enable();

            System.out.println("Step over enabled");
        } catch (Exception e) {
            System.out.println("Error executing step-over: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getName() {
        return "step-over";
    }

    @Override
    public String getDescription() {
        return "Mode pas à pas";
    }
}
