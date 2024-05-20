/**
 *
 */
package jtcom.lib.vm.az;

/**
 * @author fang
 *
 */

public class azHelp {

    /**
     ** output help message
     */
    public static boolean has(String txt) {
        for (azCommand cmd : azCommand.values())
            if (cmd.toString().equals(txt)) return true;
        return false;

    }

    public static String print() {
        String txt = "";
        for (azCommand cmd : azCommand.values())
            txt += cmd + "-";
        return txt + azCommand.values().length;
    }

    public static azCommand get(String txt) {
        for (azCommand cmd : azCommand.values())
            if (cmd.toString().equals(txt)) return cmd;
        return null;
    }
    public static boolean hasWildcard(String txt) {
        if (txt.contains("*")) return true;
        if (txt.contains("?")) return true;
        if (txt.contains("[")) return true;
        if (txt.contains("]")) return true;
        if (txt.contains("|")) return true;
        return false;
    }

    public static String search(String txt) {
        if (hasWildcard(txt)) {
            String list = "";
            for (azCommand cmd : azCommand.values()) {
                if (cmd.toString().matches(txt.replace("?", ".?").replace("*", ".*?")))
                    list += cmd.toString() + "-";
            }
            return list;
        }
        return txt;
    }
    public static String print(String command) {
        if (command.length() == 0) return print();
        String txt = "",cmd=command;
        if(cmd.equals("*")) cmd="all";
        azCommand C = get(cmd);
        if (C == null) return "Unknown command";
        switch (C) {
            //azHelp system
            case b:
            case bicep:
                txt = "[az deployment group create --mode Complete -f /bicep/$s.bicep]";
                break;
            case c:
            case create:
                txt = "[az deployment group create --mode Complete -f /bicep/createVM.bicep]";
                break;
            case d:
            case delete:
                txt = "[az deployment group create --mode Complete -f /bicep/deleteVM.bicep]";
                break;
            case s:
            case start:
                txt = "[az vm start -g %1$s -n %2$s]";
                break;
            case x:
            case stop:
                txt = "[az vm stop -g %1$s -n %2$s]";
                break;
            case rs:
            case restart:
                txt = "[az vm restart -g %1$s -n %2$s]";
                break;
            case da:
            case deallocate:
                txt = "[az vm deallocate -g %1$s -n %2$s]";
                break;
            case hb:
            case setup:
                txt = "add hearbeat control system to that vm";
                break;
            case ison:
                txt = "check whether VM is running or not";
                break;
            case ip:
                txt = "[az vm list-ip-addresses -g %1$s -n %2$s]";
                break;
            case rip:
                txt = "[az vm list-ip-addresses -g %1$s -n %2$s]";
                break;
            case ps:
            case powershell:
                txt = "[az vm run-command invoke $s --command-id RunPowerShellScript --script $s]";
            case sh:
            case shell:
                txt = "[az vm run-command invoke $s --command-id RunShellScript --script $s]";
                break;
            case all:
            case help:
                txt = "[b, c, d, da, hb , i, ip , ps, rip, r, rs, s, sh, x, xd]\r\n";
                txt += "  //fx az b|bicep script    - Run Deployment using bicep \r\n";
                txt += "  //fx az c|create          - Create a new VM\r\n";
                txt += "  //fx az x|stop            - Stop VM\r\n";
                txt += "  //fx az s|start           - Start VM\r\n";
                txt += "  //fx az rs|restart        - Restart VM\r\n";
                txt += "  //fx az d|delete          - Delete VM\r\n";
                txt += "  //fx az da|deallocate     - Deallocate VM\r\n";
                txt += "  //fx az xd                - Stop&Deallocate VM\r\n";
                txt += "  //fx az i|info            - Get-instance-view\r\n";
                txt += "  //fx az ip                - Return VM IP info\r\n";
                txt += "  //fx az rip               - Remove Public IP\r\n";
                txt += "  //fx az hb|setup          - Setup HearBeat Sys\r\n";
                txt += "  //fx az sh|shell scripts  - Invoke Shell commands\r\n";
                txt += "  //fx az ps|powershell     - Invoke Powershell commands\r\n";
                txt += "  //fx az vm --help         - Display Help from Azure CLI ";
                break;
            default:
                txt = "[run az help] for details from azure CLI";
                System.out.println("not implement yet");
                break;
        }
        return txt;
    }

    public static void main(String[] args) {
        //Help.print(Command.bye);
        System.out.println(azHelp.print("help"));
        System.out.println(azHelp.search("*k"));

        //System.out.println(Help.has("byes"));
    }

}
