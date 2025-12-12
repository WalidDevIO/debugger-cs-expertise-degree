package commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class ReceiverCommand extends Command<ObjectReference> {

    public ReceiverCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public ObjectReference execute(LocatableEvent event, String[] args) {
        try {
            StackFrame frame = event.thread().frame(0);
            ObjectReference thisObject = frame.thisObject();

            if (thisObject == null) {
                System.out.println("No receiver (static method or no 'this' available)");
            } else {
                System.out.println("Receiver (this): " + thisObject.referenceType().name() + "@" + thisObject.uniqueID());
            }

            getDebugger().readCommand(event);
            return thisObject;
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting receiver: " + e.getMessage());
        }
        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "receiver";
    }

    @Override
    public String getDescription() {
        return "Affiche le receveur de la méthode courante (this)";
    }
}