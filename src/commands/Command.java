package commands;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public abstract class Command<T> {

    private final ScriptableDebugger dbg;

    public Command(ScriptableDebugger debugger) {
        dbg = debugger;
    }

    VirtualMachine getVm() {
        return dbg.getVm();
    }

    ScriptableDebugger getDebugger() {
        return dbg;
    }

    public abstract T execute(LocatableEvent event, String[] args);
    public abstract String getName();
    public abstract String getDescription();
}
