package commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class SenderCommand extends Command<ObjectReference> {

    public SenderCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public ObjectReference execute(LocatableEvent event, String[] args) {
        try {
            if (event.thread().frameCount() < 2) {
                System.out.println("No sender (top of call stack)");
                getDebugger().readCommand(event);
                return null;
            }

            StackFrame callerFrame = event.thread().frame(1);
            ObjectReference callerThis = callerFrame.thisObject();

            if (callerThis == null) {
                System.out.println("Sender: static context or no 'this' in caller");
            } else {
                System.out.println("Sender: " + callerThis.referenceType().name() + "@" + callerThis.uniqueID());
            }

            getDebugger().readCommand(event);
            return callerThis;
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting sender: " + e.getMessage());
        }
        getDebugger().readCommand(event);
        return null;
    }

    @Override
    public String getName() {
        return "sender";
    }

    @Override
    public String getDescription() {
        return "Affiche l'objet qui a appelé la méthode courante";
    }
}