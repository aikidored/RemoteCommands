package net.elitegame.aiki.RC;

import org.bukkit.command.CommandSender;

public class Messages {

	public void EnableMessage(){ //Message That Displays Upon Plugin Load
    	System.out.println("########################"); 
    	System.out.println("# Remote Commands v4.0 #");
    	System.out.println("########################");
    	System.out.println("[Remote Commands] Made by Aikidored");
    	System.out.println("[Remote Commands] If you Enjoy. Please Leave a Review on the Spigot Page");
    	System.out.println("[Remote Commands] Spigot: https://www.spigotmc.org/resources/remote-commands.74321/");
    	System.out.println("[Remote Commands] Any Bugs or Issues? Contact me on Discord or Github");
    	System.out.println("[Remote Commands] Discord: https://discord.gg/RYTfade");
    	System.out.println("[Remote Commands] Github / Wiki: https://github.com/aikidored/RemoteCommands");
        System.out.println("");	
        System.out.println("Loading Plugin . . . ");	
        System.out.println("");	
	}
	public void DisableMessage(){ //Message That Displays Upon Plugin Termination
    	System.out.println( "########################"); 
    	System.out.println( "# Remote Commands v4.0 #");
    	System.out.println( "########################");
    	System.out.println("[Remote Commands] Disabled");
	}
	public void ConfigOutdated() {
		System.out.println("[RC][Debug][WARNING] -> Config is Outdated");
		System.out.println("[RC][Debug][WARNING] -> Please Update Config");
		System.out.println("[RC][Debug][WARNING] -> Plugin May Not Function Correctly");
	}
	public void FirstLoad() {
		//TODO write Welcome Message
        System.out.println("");	
        System.out.println("");
        System.out.println("");
		System.out.println("########################################################################################################################################################");
        System.out.println("		THANK YOU for installing Remote Commands v4.0");
        System.out.println("		Ive noticed This is a fresh install of Remote Commands v4.0");
        System.out.println("		Ive got a Few Tips that may help you Install This Plugin.");
        System.out.println("");
        System.out.println("		1. Join my Discord https://discord.gg/RYTfade");
        System.out.println("		2. Check out the Wiki on Github https://github.com/aikidored/RemoteCommands");
        System.out.println("		3. Configure the Config.YML File [This File Holds Some of the Most Important Settings and Control of Certain Features!]");
        System.out.println("		4. Configure the Servers.YML File [This FIle is the Backbone of the Entire plugin. If not configured properly this plugin is USELESS!!]");	
        System.out.println("			4a. Make Sure ALL ports that servers are listening to are OPEN. If Ports arent open my plugin cant work.");	
        System.out.println("		5. Configure the Messages.YML File [This is going on your server. Make the Messages Yours!]");	
        System.out.println("		6. Do Some Testing. Make sure All servers can communicate. the List Command is Great for this!");	
        System.out.println("			6a. TroubleShooting help is Offered on the wiki and in the discord.");	
        System.out.println("		7. Be Careful Giving out Access!!! [This Plugin Runs Commands As CONSOLE not as the issuing Player]");	
        System.out.println("		8. Debug Mode will SPAM Console. use it sparingly when needed.");	
        System.out.println("");	
        System.out.println("#######################################################################################################################################################");	
        System.out.println("");	
        System.out.println("");	
        System.out.println("");	
	}
	public void Help(CommandSender sender) {
		sender.sendMessage(main.PM[4]);
		sender.sendMessage(main.PM[5]);
		sender.sendMessage(main.PM[6]);
		sender.sendMessage(main.PM[7]);
		sender.sendMessage(main.PM[8]);
		sender.sendMessage(main.PM[9]);
		sender.sendMessage(main.PM[10]);
		sender.sendMessage(main.PM[20]);
		sender.sendMessage(main.PM[26]);
	}
}
