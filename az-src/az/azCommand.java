package jtcom.lib.vm.az;

public enum azCommand {
	s,start,			//Power on VM
	x,stop,				//Stop VM
	da,deallocate,		//Deallocate
	b,bicep,			//deploy VM via using bicep
	xd,					//Stop+deallocate
	rs, restart,		//Restart VM
	d,delete,			//Delete previous VM
	c,create,			//Create new VM
	i,info,  			//Get-instance-view
	ison,				//Check whether VM is running or not
	sh,shell,			//Invoke shell script
	ps,powershell,		//Invoke powsershell script
	ip,					//Get IP info
	rip,				//Remove PublicIP
	hb,setup,			//Setup Hearbeat Control
	login,				//Perform azure login action
	help,all

	
}
