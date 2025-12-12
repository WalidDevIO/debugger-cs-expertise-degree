package dbg;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

public class ScriptableDebugger {

    private Class<?> debugClass;
    private VirtualMachine vm;
    private final CommandRegistry commandRegistry = new CommandRegistry();

    public ScriptableDebugger() {
        commandRegistry.register(new ContinueCommand(this));
        commandRegistry.register(new StepCommand(this));
        commandRegistry.register(new StepOverCommand(this));
        commandRegistry.register(new HelpCommand(this, commandRegistry));
    }

    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        vm = launchingConnector.launch(arguments);
        return vm;
    }
    public void attachTo(Class<?> debuggeeClass) {

        this.debugClass = debuggeeClass;
        try {
            vm = connectAndLaunchVM();
            enableClassPrepareRequest(vm);
            startDebugger();
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e);
        } catch (Exception e) {
            e.printStackTrace()
            ;
        }
    }

    public void startDebugger() throws VMDisconnectedException, InterruptedException, AbsentInformationException, IOException {
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                System.out.println(event.toString());
                if(event instanceof VMDisconnectEvent) {
                    System.out.println("----- Fin du programme.");
                    InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
                    OutputStreamWriter writer = new OutputStreamWriter(System.out) ;
                    try {
                        reader.transferTo(writer);
                        writer.flush() ;
                    } catch(IOException e) {
                        System.out.println("Target VM input stream reading error.") ;
                    }
                    return;
                }

                if(event instanceof ClassPrepareEvent) {
                    setBreakPoint(debugClass.getName(), 6);
                    setBreakPoint(debugClass.getName(), 9);
                    vm.resume();
                }

                if (event instanceof BreakpointEvent be) {
                    readCommand(be);
                }

                if (event instanceof StepEvent se) {
                    readCommand(se);
                }
            }
        }
    }

    public VirtualMachine getVm() {
        return vm;
    }

    public void setBreakPoint(String className, int lineNumber) throws AbsentInformationException {
        for (ReferenceType targetClass : vm.allClasses()) {
            if (targetClass.name().equals(className)) {
                Location location = targetClass.locationsOfLine(lineNumber).getFirst();
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
            }
        }
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    private void readCommand(LocatableEvent event) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command = reader.readLine();

        if(commandRegistry.hasCommand(command)) {
            commandRegistry.getCommand(command).execute(event);
        } else {
            System.out.println("Commande " + command + " inexistante.");
        }
    }

}
