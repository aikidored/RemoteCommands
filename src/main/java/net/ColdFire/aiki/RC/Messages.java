package net.ColdFire.aiki.RC;

import org.bukkit.command.CommandSender;

public class Messages {

	public void EnableMessage(){ //Message That Displays Upon Plugin Load
    	System.out.println("########################"); 
    	System.out.println("# Remote Commands v4.1 #");
    	System.out.println("########################");
    	System.out.println("[Remote Commands] Made by Aikidored");
    	System.out.println("[Remote Commands] If you Enjoy. Please Leave a Review on the Spigot Page");
    	System.out.println("[Remote Commands] Spigot: https://www.spigotmc.org/resources/remote-commands.74321/");
    	System.out.println("[Remote Commands] Any Bugs or Issues? Contact me on Discord or Github");
    	System.out.println("[Remote Commands] Discord: https://discord.gg/RYTfade");
    	System.out.println("[Remote Commands] Github / Wiki: https://github.com/aikidored/RemoteCommands");
        System.out.println("");	
        System.out.println("[Remote Commands] Loading Plugin . . . ");	
        System.out.println("");	
	}
	public void DisableMessage(){ //Message That Displays Upon Plugin Termination
    	System.out.println( "########################"); 
    	System.out.println( "# Remote Commands v4.1 #");
    	System.out.println( "########################");
    	System.out.println("[Remote Commands] Disabled");
	}
	public void ConfigOutdated() {
		System.out.println("[RC][Debug][WARNING] -> Config is Outdated");
		System.out.println("[RC][Debug][WARNING] -> Please Update Config");
		System.out.println("[RC][Debug][WARNING] -> Plugin May Not Function Correctly");
	}
	public void FirstLoad() {
        System.out.println(" ");	
        System.out.println("########### ");
        System.out.println(" ");	
        System.out.println("	THANK YOU for installing Remote Commands v4.0");
        System.out.println("	Ive noticed This is a fresh install of Remote Commands v4.0");
        System.out.println("	Ive got a Few Tips that may help you Install This Plugin.");	
        System.out.println("	This Entire Message is also located on the WIKI");
        System.out.println(" ");
        System.out.println("	1. Join my Discord https://discord.gg/RYTfade");
        System.out.println("	2. Check out the Wiki on Github https://github.com/aikidored/RemoteCommands");
        System.out.println("	3. Configure the Config.YML File [This File Holds Some of the Most Important Settings and Control of Certain Features!]");
        System.out.println(" 	4. DO NOT MODIFY Config-Info This is for server side information and Stats");
        System.out.println("	5. Configure the Servers.YML File [This FIle is the Backbone of the Entire plugin.");	
        System.out.println("		4a. Make Sure ALL ports that servers are listening to are OPEN. If Ports arent open my plugin cant work.");	
        System.out.println("		4b.  If not configured properly this plugin is USELESS!!]");	
        System.out.println("	6. Configure the Messages.YML File [This is going on your server. Make the Messages Yours!]");	
        System.out.println("	7. Do Some Testing. Make sure All servers can communicate. the List Command is Great for this!");	
        System.out.println("		6a. TroubleShooting help is Offered on the wiki and in the discord.");	
        System.out.println("	8. Be Careful Giving out Access!!! [This Plugin Runs Commands As CONSOLE not as the issuing Player]");	
        System.out.println("	9. Debug Mode will SPAM Console. use it sparingly when needed.");
        System.out.println("########### ");
        System.out.println("	WARNINGS and Disclaimers");	
        System.out.println(" ");	
        System.out.println("	1. Remote Commands v4.1 is NOT Compatible with v3.1 and Below");	
        System.out.println("	2. Remote Commands v4.1 Should Work on ALL Versions of Spigot. ");	
        System.out.println("	3. Remote Commands v4.1 Will NOT work on a Bungeecord instance. ");	
        System.out.println("		3a. It can span across the network but NOT on the Bungee Server itself");	
        System.out.println("	4. The Passkey System is Basic. It will Stop a Basic Attack. But a Focused attack may penetrate it.");		
        System.out.println("		4a. Keep your IP Secret. and Keep the Ports used for Remote Commands Super Secret");	
        System.out.println("	5. Due to the nature of this plugin. Connection Issues Between Servers are hard to troubleshoot. ");	
        System.out.println("	6. Self-Hosting is Advised. Hosting through an Online-Host may present issues with Connecting servers.");	
        System.out.println("		6a. Certain issues may be between you and online hosts.");	
        System.out.println(" ");	
        System.out.println("########### ");
        System.out.println(" ");	
	}
	public void UpdateAvailable(){
        System.out.println(" ");	
        System.out.println("############");	
        System.out.println("#  NOTICE  #");	
        System.out.println("####################");	
        System.out.println("#  Remote Commands #");	
        System.out.println("#############################################################");	
        System.out.println("# An Update is Available                                    #");	
        System.out.println("# Please Check our Spigot Page:                             #");	
        System.out.println("# https://www.spigotmc.org/resources/remote-commands.74321/ #");	
        System.out.println("#############################################################");	
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
