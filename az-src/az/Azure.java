/**
 *
 */
package jtcom.lib.vm.az;
import jtcom.lib.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.json.simple.parser.JSONParser;

import java.io.File;

/**
 * @author fang 2024
 * Build Integration with Azure CLI and Azure Deployment[fx Bicep]
 *
 */
public class Azure {

    /**
     * @param args
     */
    private final static Logger log = Sys.getLogger("[Azure]");
    private String parameters="";
    private JSONObject paramJson=null;
    private String HOST="hb.35cloud.com"; //https://192.168.1.82
    private String WinIP="10.176.64."; // win IP
    private String vmIP="";
    private String rgName="";
    private String vmName="";
    private String vmTarget="-g %1$s -n %2$s";
    private String vmStatus="";
    private String vmDetails="";
    private String adminUsername="";
    private String adminPassword="";

    private String api="";	//RC agent or Master agent
    private String msg;
    private Requester H=null,S=null,C=null;
    public boolean isOK=false;
    public boolean isReady=false;
    public boolean isWin=true;
    public Azure(){
        loadInfo();
    }
    public Azure(String[] vm){
        load(vm);
    }
    public Azure(String vid, String sid, String vip){

    }
    public void load(String[] vm){
    }
    public void loadInfo(){
        try {
            File json = new File("bicep/parameters.json");
            if (json.exists()) {
                parameters = Sys.getContents("bicep/parameters.json");
                paramJson = (JSONObject) new JSONParser().parse(Sys.getJsonValue(parameters, "parameters"));
                rgName = Sys.getJsonValue(paramJson.get("rgName").toString(),"value");
                vmName = Sys.getJsonValue(paramJson.get("vmName").toString(),"value");
                adminUsername = Sys.getJsonValue(paramJson.get("adminUsername").toString(),"value");
                adminPassword = Sys.getJsonValue(paramJson.get("adminPassword").toString(),"value");
                vmTarget=String.format(vmTarget,rgName,vmName);
                //log.info(adminPassword);
            }
        }catch (Exception e){
            log.error("exception under loadInfo: "+e.getMessage());
        }

    }
    public void quit(){

    }
    public boolean isLoginOK(){
        String result=azCLI("az account show");
        String state= Sys.getJsonValue(result,"state");
        //log.info(state);
        if(state.equals("Enabled")) return true;
        return false;
    }
    public boolean isOn(){
        String statuses=Sys.getJsonSubValue(getState(),"instanceView->statuses");
        //log.info(statuses);
        if(statuses.contains("VM running")) return true;
        return false;
    }
    public String getState(){
        //az vm get-instance-view -g bicep -n myVM
        String cmd="az vm get-instance-view "+vmTarget;
        //log.info(cmd);
        return azCLI(cmd);
    }
    public boolean wait4Off(){
        if(!isOn()) return true;
        return false;
    }
    public boolean wait4Ready(){
        for(int i=0;i<12;i++){
            if(isOn()) return true;
            Sys.sleep("wait-4-ready,",10);
        }
        return isOn();
    }
    public String getPIP(){
        String IP="unknown";
        try{
            String cmd="az vm list-ip-addresses "+vmTarget;
            String ips=azCLI(cmd).trim();
            log.info(ips) ;
            String publicIP=Sys.getJsonSubValue(ips,"virtualMachine->network->publicIpAddresses->ipAddress");
            log.info(publicIP);
            String privateIP=Sys.getJsonSubValue(ips,"virtualMachine->network->privateIpAddresses");
            JSONArray plist = (JSONArray) new JSONParser().parse(privateIP);
            privateIP=plist.get(0).toString();
            if(publicIP.equals("null")||publicIP.isEmpty()) IP= privateIP;
            else IP=publicIP+":"+privateIP;

        }catch (Exception e){
            log.error("Exception during fetchIP"+e.getMessage());
        }
        return IP;
    }
    public String removePublicIP(){
        String cmd="az network nic ip-config update -g %1$s --nic-name %2$s-nic --name ipconfig1 --remove publicIpAddress";
        cmd=String.format(cmd,rgName,vmName);
        azCLI(cmd);
        return getPIP();
    }
    public void setIP(String ip){
        vmIP=ip;
    }
    public String getAllvms(){
        return H.SRMessage("vim-cmd vmsvc/getallvms").trim();
    }
    public String getMsg(){
        return msg;
    }
    public void getSnapshot(){

    }
    public String powerOn(){
        return azCLI("vm start "+vmTarget);
    }
    public String powerOff(){
        return azCLI("vm stop "+vmTarget);
    }
    public String deallocate(){
        return azCLI("vm deallocate "+vmTarget);
    }
    public String shutDown(){
        if(!isOn()) return "already";
        return azCLI("az vm stop "+vmTarget);
    }
    public String restart(int off){
        restart();
        for(int i=0;i<6;i++) {
            Sys.sleep(i+"wait-for-shutdown",10);
            if (isOn()) return "OK";
        }
        return "timeout FAIL";

    }
    public String restart(){
        //az vm restart
        if(!isOn()) return azCLI("az vm start "+vmTarget);
        return azCLI("az vm restart "+vmTarget);
    }
    public String azCLI(String cmd){
        String command=cmd;
        if(!cmd.startsWith("az ")) command="az "+cmd;
        log.info("[azCLI]"+command);
        String result= Sys.shell(command);
        //log.info(result);
        return result;
    }
    public String login(){
        if(isLoginOK()) return "Already";
        else azCLI("az login");
        return isLoginOK()?"OK":"FAIL";
    }
    public void revertToSnapshot(){
        powerOff();

        if(!isReady) isReady=wait4Ready();
    }
    public void revertTo(String id){
        if(id.isEmpty()) msg="missing snapshotId";

    }
    public String createVM(){
        //using Bicep for createVM
        //az deployment group create --mode Complete -g bicep -f .\createVM.bicep
        return deployment("createVM.bicep");
    }
    public String deleteVM(){
        //using Bicep for deleteVM
        //az deployment group create --mode Complete -g bicep -f .\deleteVM.bicep
        log.info("going to deleteVM using [--mode Complete]");
        return deployment("deleteVM.bicep");
    }
    public String deployment(String bicepFile){
        File bicep=new File("bicep/"+bicepFile);
        File json = new File("bicep/parameters.json");
        if(!bicep.exists()) return bicep.getName()+"_missing FAIL";
        String cmd= "az deployment group create --mode Complete -f \""+bicep.getAbsolutePath()+"\" -g "+rgName;
        if(bicepFile.contains("create"))
            cmd+=" --parameters=\""+json.getAbsolutePath()+"\"";
        return azCLI(cmd);
    }

    public boolean installHBSys(){
        if(!wait4Ready()) return false;
        String ip=getPIP().split(":")[0];
        Requester R=new Requester();
        R.SRMessage("ssh "+ip+ " "+adminUsername+" $"+ Encryptor.encrypt(adminPassword));
        R.SRMessage("sudo apt update");
        R.SRMessage("sudo apt install openjdk-8-jdk -y");
        R.SRMessage("git clone https://github.com/ts01soonr/HBsys.git");
        R.SRMessage("cp HBsys/*.* ./");
        R.SRMessage("sudo sh install.sh");
        R.SRMessage("rm *.*");
        R.SRMessage("exit");
        Sys.sleep(10);
        R.SRMessage("call "+ip+":2300 who");
        R.disconnect();
        Requester H=new Requester(ip+":2300");
        boolean result=H.isOK;
        if(!result) result=H.wait4on();
        H.disconnect();
        return result;
    }
    public String shell(String script){
        //shell-script-for-linux
        String cmd="az vm run-command invoke %1$s --command-id RunShellScript --scripts \"%2$s\"";
        cmd=String.format(cmd,vmTarget,script);
        return azCLI(cmd);
    }
    public String pshell(String script){
        //powershell-script-for-windows
        String cmd="az vm run-command invoke %1$s --command-id RunPowerShellScript  --scripts \"%2$s\"";
        cmd=String.format(cmd,vmTarget,script);
        return azCLI(cmd);
    }
    public String getValue() throws Exception{
        String js=Sys.getContents("az.txt");
        String IP=Sys.getJsonSubValue(js,"virtualMachine->network->publicIpAddresses->ipAddress");
        log.info(IP);
        if(IP==null||true){
            IP=Sys.getJsonSubValue(js,"virtualMachine->network->privateIpAddresses");
            JSONArray jA = (JSONArray) new JSONParser().parse(IP);
            log.info(jA.get(0).toString());

        }

        return IP;

    }

    public String exec(String command){
        Sys.println("exec "+command);
        String[] tokens = command.split(" ");
        String cmd=tokens[0];                       //vm-Command
        String p2 = tokens.length>1?tokens[1]:"";   //vm-Id
        String p3 = tokens.length>2?tokens[2]:"";   //vm-snapshotId | snapshotName
        String p4 = tokens.length>3?tokens[3]:"";   //describe text of vm
        String p5 = tokens.length>4?tokens[4]:"";   //pending
        String p6 = tokens.length>5?tokens[5]:"";
        String p7 = tokens.length>6?tokens[6]:"";
        String p27 = command.substring(cmd.length()).trim();
        String p37 = command.substring((cmd + " " + p2).trim().length()).trim();
        int off=0;
        if(!p3.isEmpty()) if(StringUtils.isNumeric(p3)) off=Integer.parseInt(p3);
        if(cmd.isEmpty()) return azHelp.print("help");
        else if(cmd.equals("s")||cmd.equals("start")) return powerOn();
        else if(cmd.equals("x")||cmd.equals("stop")) return powerOff();
        else if(cmd.equals("da")||cmd.equals("deallocate")) return deallocate();
        else if(cmd.equals("xd")) {powerOff();return deallocate();} //stop&deallocate
        else if(cmd.equals("da")||cmd.equals("deallocate")) return deallocate();
        else if(cmd.equals("rs")||cmd.equals("restart")) return restart();//restart
        else if(cmd.equals("d")||cmd.equals("delete")) return deleteVM();
        else if(cmd.equals("c")||cmd.equals("create")) return createVM();
        else if(cmd.equals("b")||cmd.equals("bicep")) return deployment(p27);
        else if(cmd.equals("sh")||cmd.equals("shell")) return shell(p27);
        else if(cmd.equals("ps")||cmd.equals("powershell")) return pshell(p27);

        else if(cmd.equals("ison")) return isOn()+""; //
        else if(cmd.equals("i")) return getState(); //vm status//getPIP
        else if(cmd.equals("ip")) return getPIP();
        else if(cmd.equals("rip")) return removePublicIP();

        else if(cmd.equals("hb")||cmd.equals("setup")) return installHBSys()+"";
            //vim off 98:GWin10-32a [1|2|3|4|5]
        else if(cmd.equals("login")) return login();
        else if(cmd.equals("help")) return azHelp.print(p2);
        else if(!cmd.isEmpty())return azCLI(command);

        return "unknown";
    }
    public static void main(String[] args) throws Exception {
        log.info(azHelp.print(""));
        log.info(azHelp.print("help"));
    }

}

