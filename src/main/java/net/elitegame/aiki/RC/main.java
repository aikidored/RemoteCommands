package net.elitegame.aiki.RC;


import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

public class main extends JavaPlugin
{
	//######################
	// Config Declarations #
	//######################
	boolean Debug = false; // Contains The Boolean for whether or not Debug Messages are Displayed 
	boolean[] FeatureToggles = new boolean[6]; // Creates Array for Feature Toggles set in Config
	double Version = 4.0; // Version of Plugin
	int Port = 4000; // Plugin Port. Set as 4000, Reset in Config Load
    int listCount = 100; // Defines Array Sizes for Servers (Set at 100 for Space Buffer. Can be increased)
    String serverName = " "; // Server Identifier. Defined in Config
	int Passkey = 123456;
	
	//#######################
    // Servers Declarations #
	//#######################
	String ServerList[]; //Array containing List of the names of servers listed in the Servers.yml
	String ServerAddresses[]; //Array Containing list of all Server IP's listed in Servers.yml
	int ServerPorts[];	//Array Containing list of all ports listed in Servers.yml
	
	//#####################
	// Class Declarations #
	//#####################
	Messages M = new Messages();
	Encryption t = new Encryption(); // Declares Encryption Class
	
	//###################
	//File Declarations #
	//###################
    public File configf;
    public static FileConfiguration config;
    public File configs;
    public static FileConfiguration Servers;
    public File configl;
    public static FileConfiguration Log;
    public File configm;
    public static FileConfiguration Messages;
	
	//#######################
	//Plugin Control Methods#
	//#######################	
	@Override
    public void onEnable() {    // Method Runs on Plugin Enable
		M.EnableMessage(); // Displays Start Message
		loadPlugin(); //Loads Plugin Files and Retrieves File Information
		startPlugin(); // Starts Various Plugin Systems
    }
    @Override
    public void onDisable() {  // Method Runs on Plugin Disable
    	M.DisableMessage(); //Displays Stop Message
    }
    public void loadPlugin() { //Loads Plugin Files and Retrieves File Information
		loadConfigFile(); 
		loadServersFile();
		loadMessageFile(); // Loads Messages.yml
		loadMessages(); // Gets Messages From Messages.yml
		LoadArrays(); // Loads Server Data from Servers.ym
   }
   public void startPlugin() {
	   
   }
   
   //#######################
   // loadPlugin Methods() #
   //#######################
	public void loadConfigFile() { //Loads Config File
    	configf = new File(getDataFolder(), "config.yml");    	
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();       
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        } 
		Debug = config.getBoolean("Debug"); // Gets Debug Value 
		debug("[RC][Debug] Debug Value: "+ Debug);
		
		if (Version == config.getDouble("Config-Version"))   {debug("[RC][Debug] Config Version Correct");}   else   { M.ConfigOutdated();}  // Checks if Config Version is Correct
		if (config.getBoolean("FirstLoad") == true) { M.FirstLoad();}

		serverName = config.getString("Server-Name");
		debug("[RC][Debug] Server Name: "+ serverName);
		
		Passkey = config.getInt("PassKey");
		debug("[RC][Debug] Passkey: "+ Passkey);
		
		Port = config.getInt("Port-Listener");
		debug("[RC][Debug] Port Retrieved from Config: "+Port);
		
		listCount = config.getInt("Server-Count");
		debug("[RC][Debug] Server List Count: "+ listCount);
		
        FeatureToggles[0] = config.getBoolean("T0"); // Gets Commands Received Value
		debug("[RC][Debug] Log Commands Received Value: "+ FeatureToggles[0]);		
        FeatureToggles[1] = config.getBoolean("T1"); // Gets Commands Sent Value
		debug("[RC][Debug] Log Commands Sent Value: "+ FeatureToggles[0]);		
        FeatureToggles[2] = config.getBoolean("T2"); // Gets Broadcasts Received Value
		debug("[RC][Debug] Log Broadcasts Received Value: "+ FeatureToggles[0]);		
        FeatureToggles[3] = config.getBoolean("T3"); // Gets Broadcasts Sent Value
		debug("[RC][Debug] Log Broadcasts Sent Value: "+ FeatureToggles[0]);		
        FeatureToggles[4] = config.getBoolean("T4"); // Gets Incoming/Outgoing Log Value
		debug("[RC][Debug] Log Incoming/OutGoing Value: "+ FeatureToggles[0]);		
        FeatureToggles[5] = config.getBoolean("T5"); //Gets Error Log Value
		debug("[RC][Debug] Log Errors Value: "+ FeatureToggles[0]);
		
		debug("[RC][Debug] Loaded Config.yml");
        System.out.println("[RC] Config has been Loaded");		
	}

	
	public void loadServersFile() { //Loads Config
    	configs = new File(getDataFolder(), "Servers.yml");    	
        if (!configs.exists()) {
            configs.getParentFile().mkdirs();
            saveResource("Servers.yml", false);
        }
        Servers = new YamlConfiguration();
        
        try {
            Servers.load(configs);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        } 
    	debug("[RC][Debug] Loading Servers.yml");
    	debug("[RC][Debug] Total Servers Found: "+listCount);
    	ServerList = new String[listCount]; 
    	ServerAddresses = new String[listCount]; 
    	ServerPorts = new int[listCount];
    	debug("[RC][Debug] Displaying Servers Found in Servers.yml");
    	for (int x = 0; x < listCount; x++) { 
    		String serverName = Servers.getString("Server-List."+ x +".Name");
    		String serverAddress = Servers.getString("Server-List."+x+".Address");
    		int serverPort = Servers.getInt("Server-List."+x+".Port");
    		ServerList[x] = serverName;
    		ServerAddresses[x] =  serverAddress;
    		ServerPorts[x] = serverPort; 
    		int y = x+1;
    		debug("[RC][Debug] |"+y+"| Name: "+serverName+" Host: "+ serverAddress+" Port: "+serverPort);
    	}
    	debug("[RC][Debug] Servers.yml Loaded");
    	
    	
        System.out.println("[RC] Servers List has been Loaded");		
	}
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   public void debug(String D) {
	   if (Debug == true) {
		   System.out.println("[RC][Debug]-> "+D);
	   }
   }
   
}

