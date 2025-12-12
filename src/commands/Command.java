package commands;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public abstract class Command {

    protected final ScriptableDebugger dbg;

    public Command(ScriptableDebugger debugger) {
        this.dbg = debugger;
    }

    VirtualMachine getVm() {
        return dbg.getVm();
    }
    public abstract void execute(LocatableEvent event);
    public abstract String getName();
    public abstract String getDescription();
}
