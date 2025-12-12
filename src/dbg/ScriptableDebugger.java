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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ScriptableDebugger {

    private Class<?> debugClass;
    private VirtualMachine vm;
    private final CommandRegistry commandRegistry = new CommandRegistry();

    public ScriptableDebugger() {
        List<Command> commandList = List.of(
                new ContinueCommand(this),
                new StepCommand(this),
                new StepOverCommand(this),
                new HelpCommand(this, commandRegistry),
                new FrameCommand(this),
                new TemporariesCommand(this),
                new StackCommand(this),
                new ReceiverCommand(this),
                new SenderCommand(this),
                new ReceiverVariablesCommand(this),
                new MethodCommand(this),
                new ArgumentsCommand(this),
                new PrintVarCommand(this),
                new BreakCommand(this),
                new BreakpointsCommand(this),
                new BreakOnceCommand(this),
                new BreakOnCountCommand(this),
                new BreakBeforeMethodCallCommand(this)
        );
        commandList.forEach(commandRegistry::register);
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

                if(event instanceof ClassPrepareEvent cpe) {
                    // Vérifier si c'est la classe principale qu'on débugge
                    if(cpe.referenceType().name().equals(debugClass.getName())) {
                        // Trouver la méthode main
                        try {
                            for(Method method : cpe.referenceType().methods()) {
                                if(method.name().equals("main") && method.isStatic()) {
                                    // Trouver la première ligne exécutable du main
                                    List<Location> locations = method.allLineLocations();
                                    if(!locations.isEmpty()) {
                                        Location firstLocation = locations.get(0);
                                        BreakpointRequest bpReq = vm.eventRequestManager()
                                                .createBreakpointRequest(firstLocation);
                                        bpReq.enable();
                                        System.out.println("Breakpoint set at main() line " +
                                                firstLocation.lineNumber());
                                    }
                                    break;
                                }
                            }
                        } catch(AbsentInformationException e) {
                            System.out.println("Could not set breakpoint on main: " + e.getMessage());
                        }
                    }
                }

                if (event instanceof BreakpointEvent be) {
                    readCommand(be);
                }

                if (event instanceof StepEvent se) {
                    readCommand(se);
                }

                vm.resume();
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

    public void readCommand(LocatableEvent event) {
        System.out.println("Debugger is at: " + event.toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String[] parts = reader.readLine().strip().split(" ");
            String command = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);
            if(commandRegistry.hasCommand(command)) {
                commandRegistry.getCommand(command).execute(event, args);
            } else {
                System.out.println("Commande " + command + " inexistante.");
                readCommand(event);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
