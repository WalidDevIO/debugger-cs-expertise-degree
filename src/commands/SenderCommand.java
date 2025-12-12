package commands;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import dbg.ScriptableDebugger;

public class SenderCommand extends Command {

    public SenderCommand(ScriptableDebugger debugger) {
        super(debugger);
    }

    @Override
    public void execute(LocatableEvent event) {
        try {
            if (event.thread().frameCount() < 2) {
                System.out.println("No sender (top of call stack)");
                return;
            }

            StackFrame callerFrame = event.thread().frame(1);
            ObjectReference callerThis = callerFrame.thisObject();

            if (callerThis == null) {
                System.out.println("Sender: static context or no 'this' in caller");
            } else {
                System.out.println("Sender: " + callerThis.referenceType().name() + "@" + callerThis.uniqueID());
            }
            dbg.readCommand(event);
        } catch (IncompatibleThreadStateException e) {
            System.out.println("Error: Thread not suspended - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error getting sender: " + e.getMessage());
        }
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