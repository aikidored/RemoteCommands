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
		System.out.println("First Load Message");
        System.out.println("");	
	}
	public void Help(CommandSender sender) {
		//TODO Display Help Messages
	}
}
