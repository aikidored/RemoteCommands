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
	//##############
	// Misc Values #
	//##############
    boolean pluginStarted = false; // Prevents Port Listener from re-enabling upon reload
	String[] argArray; // Array to Hold Commands Sent to this Plugin
	
	//######################
	// Config Declarations #
	//######################
	boolean LSD = false;  // Startup Log Information Dump Value
	static boolean Debug = false; // Contains The Boolean for whether or not Debug Messages are Displayed 
	static boolean[] FeatureToggles = new boolean[6]; // Creates Array for Feature Toggles set in Config
	double Version = 4.1; // Version of Plugin
	int Port = 4000; // Plugin Port. Set as 4000, Reset in Config Load
    int listCount = 100; // Defines Array Sizes for Servers (Set at 100 for Space Buffer. Can be increased)
    String serverName = " "; // Server Identifier. Defined in Config
	static int Passkey = 123456;
	
	//#######################
    // Servers Declarations #
	//#######################
	String ServerList[]; //Array containing List of the names of servers listed in the Servers.yml
	String ServerAddresses[]; //Array Containing list of all Server IP's listed in Servers.yml
	int ServerPorts[];	//Array Containing list of all ports listed in Servers.yml
	
	//###########
	// Messages #
	//###########
    static String[] PM = new String[27]; // Array Contains All Messages Defined in Messages.yml
    
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
    public File configi;
    public static FileConfiguration config2;
    public File configs;
    public static FileConfiguration Servers;
    public File configl;
    public static FileConfiguration Log;
    public File configm;
    public static FileConfiguration Messages;
	
    //####################
    // Command Variables #
    //####################
    static String[] WCL = new String[20]; //WCL = Waiting Command List. Stores Commands in queue 
    static String[] LSL = new String[40]; //LSL = Logged String List. Stores Log Messages in queue
    static int WCLIndex = 0; // Count of Waiting Commands to be Issued
    static int LSLIndex = 0; // Count of waiting Strings to be Logged
    static boolean commandWaiting = false; // Declares whether a command was received and is waiting
    static boolean LSLWaiting = false; //Declares whether a log string is waiting to be added to log
    static String receivedCommand = " "; // Stores Received Command
    static String LoggedString = " "; // Stores Received String 
    
    
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
		loadMessageFile();
		loadBstats();
   }
    public void startPlugin() { // Starts Plugin Internals     
	   if (pluginStarted == false) 
   			startListenerSocket(Port);
	   startRepeatingTask();
   }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws IndexOutOfBoundsException{  // Accepts Player Commands
    	if(args.length != 0) {
    		if (label.equalsIgnoreCase("RC") == true) {
        		String Argument0 = args[0];
               	String Command;
           		boolean checkBool = false;
           		argArray = new String[args.length];	
           		for(int h = 0; h < args.length-1; h++) {
           			argArray[h] = args[h+1];
           		}
           		argArray[argArray.length-1] = " ";
               	Command = String.join(" ", argArray);
           		String DebugMessage = String.join(" ", args); 
           		String DebugServerlist = String.join(" ", ServerList); 
           		String DebugServerAddress = String.join(" ", ServerAddresses);
           		String DebugServerPorts = StringUtils.join(ArrayUtils.toObject(ServerPorts), " - ");
           		debug("CommandArgument: "+ Argument0);
            	debug("Label: "+ label);
            	debug("Server List:" + DebugServerlist);
           		debug("Server Address:" + DebugServerAddress);
           		debug("Server Ports:" + DebugServerPorts);
           		debug("Message: "+DebugMessage);
           		boolean checkServerWithinArray = checkArray(Argument0);
           		if(Argument0.equalsIgnoreCase("Reload") == true) {
           			checkBool = Reload(sender);
           		}
           		else if (Argument0.equalsIgnoreCase("Help")) {
          			checkBool = true;
           			M.Help(sender);
           		}
            	else if (Argument0.equalsIgnoreCase("List")) {
            		checkBool = list(sender);
           		}
            	else if (Argument0.equalsIgnoreCase("Debug")) {
            		checkBool = debugCommand(sender);
           		}
           		else if (argArray.length < 2) {
           			sender.sendMessage(PM[0]+PM[11]);
           			checkBool = true;
           			M.Help(sender);
           		}
           		else if (Argument0.equalsIgnoreCase(serverName) == true) {
           			if (FeatureToggles[1] == true) {
               			checkBool = true;
           				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Command);
               	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
               	    	logToFile("["+timeStamp+"][Send] Player ["+sender+"] Sent Command:["+Command+"] to ["+Argument0+"]");
            				
            		}
            	}
            	else if (Argument0.equalsIgnoreCase("SendAll") == true) {
           			if (FeatureToggles[1] == true) 
           				checkBool = CSendAll(sender, Command);
           		}
           		else if(checkServerWithinArray == true) {
           			if (FeatureToggles[1] == true) 
           				checkBool = sendCommand(sender, Argument0, Command);
           		} else {
           			sender.sendMessage(PM[0]+PM[12]);
           			checkBool = true;
           			M.Help(sender);
           		} 
           		return checkBool;
    				
    			
    		} else if (label.equalsIgnoreCase("RB") == true) {
        		String Argument0 = args[0];
               	String Command;
           		boolean checkBool = false;
           		argArray = new String[args.length];	
           		for(int h = 0; h < args.length-1; h++) {
           			argArray[h] = args[h+1];
           		}
           		argArray[argArray.length-1] = " ";
               	Command = String.join(" ", argArray);
           		String DebugMessage = String.join(" ", args); 
           		String DebugServerlist = String.join(" ", ServerList); 
           		String DebugServerAddress = String.join(" ", ServerAddresses);
           		String DebugServerPorts = StringUtils.join(ArrayUtils.toObject(ServerPorts), " - ");
           		debug("CommandArgument: "+ Argument0);
            	debug("Label: "+ label);
            	debug("Server List:" + DebugServerlist);
           		debug("Server Address:" + DebugServerAddress);
           		debug("Server Ports:" + DebugServerPorts);
           		debug("Message: "+DebugMessage);
           		boolean checkServerWithinArray = checkArray(Argument0);
           		if(Argument0.equalsIgnoreCase("Reload") == true) {
           			checkBool = Reload(sender);
           		}
           		else if (Argument0.equalsIgnoreCase("Help")) {
           			checkBool = true;
           			M.Help(sender);
           		}
           		else if (Argument0.equalsIgnoreCase("List")) {
            		checkBool = list(sender);
            	}
            	else if (Argument0.equalsIgnoreCase("Debug")) {
            		checkBool = debugCommand(sender);
           		}
           		else if (argArray.length < 2) {
           			sender.sendMessage(PM[0]+PM[11]);
           			checkBool = true;
           			M.Help(sender);
           		}
           		else if (Argument0.equalsIgnoreCase(serverName) == true) {
           			if (FeatureToggles[2] == true) {
               			checkBool = true;
               			Bukkit.broadcastMessage(main.PM[18] + ChatColor.translateAlternateColorCodes('&', Command));
               	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
               	    	logToFile("["+timeStamp+"][Send] Player ["+sender+"] Sent Command:["+Command+"] to ["+Argument0+"]");
           				
            		}
            	}
           		else if (Argument0.equalsIgnoreCase("SendAll") == true) {
           			if (FeatureToggles[2] == true) 
           				checkBool = BSendAll(sender, Command);
           		}
           		else if(checkServerWithinArray == true) {
           			if (FeatureToggles[2] == true) 
           				checkBool = sendBroadcast(sender, Argument0, Command);
           		} else {
           			sender.sendMessage(PM[0]+PM[12]);
           			checkBool = true;
           			M.Help(sender);
           		} 
           		return checkBool;
    				
    			
    		} else {
    			return false;
    		}
    	} else {
    		M.Help(sender);
    	}
    	return true;
    }
    
    //###################
    //onCommand Methods #
    //###################
    public boolean checkArray(String Server) { //Checks for Server within Array        
    	boolean check = false;
    	String TempString;
    	for (int i=0;i<ServerList.length;i++) { 
    		TempString = ServerList[i]; 
			if (Server.equalsIgnoreCase(TempString) == true) { 
				check = true; 
			} 
    	}
    	return check; 
    }
    public boolean Reload(CommandSender sender) {	// Reloads Plugin				
    	loadPlugin();
    	debug("[Remote Commands][Client] Reload Completed.");   
    	sender.sendMessage(PM[0]+PM[17]);
    	return true;
    }
    public boolean list(CommandSender sender) { 	// Lists all servers and performs Status Check			
    	sender.sendMessage(PM[0]+PM[16]);
    	return ListStatus(sender);
    }
    public boolean debugCommand(CommandSender sender) {  // Toggles Debug Mode Status				
    	if (Debug == true) {
    		Debug = false;
    		sender.sendMessage(PM[0]+PM[2]);
    	} else {
    		Debug = true;
    		sender.sendMessage(PM[0]+PM[1]);
    	}
    	return true;
    }
    public static void addCommand(String Command) {
    	commandWaiting = true;
    	WCL[WCLIndex] = Command;
    	WCLIndex++;
    	debug("Command ["+ Command+"] Loaded To Memory. Awaiting repeating Task.");
    }
    //#######################
    // Get Location Methods #
    //#######################
    public String GetServerIp(int index)
    {
    	String ReturnIp = ServerAddresses[index+1];  
    	return ReturnIp;
    }
    public int GetServerPort(int index) //Returns port for server named in command from config
    {
    	int returnPort = ServerPorts[index+1];
    	return returnPort;
    }
    public int getIndex(String Server) { //Used to get location of Server in Array
    	int index = 0;
    	String TempIndex;
		for (int i=0;i<ServerAddresses.length;i++) {
			TempIndex = ServerList[i];
		    if (Server.equalsIgnoreCase(TempIndex)) {
		    	index = i;
		        break;
		    }
		}
		index -= 1;
		return index;
    }
    

    //######################
    // Send Command Methods#
    //######################
    public boolean ListStatus(CommandSender sender) {
    	boolean check = false;
    	for (int u = 0; u< ServerList.length; u++) {
    		check = sendStatusCheck(sender, ServerList[u]);
    	}
    	return check;
    }
    public boolean sendStatusCheck(CommandSender sender, String Server) {
		String Sender = sender.toString();
		int serverIndex = getIndex(Server); 
		String PassKey = t.getEncryptedKey(Passkey);
	    String ServerIPString = GetServerIp(serverIndex); 
	   	int serverPort = GetServerPort(serverIndex);
	   	InetAddress ServerIP = null; 
		try {
			ServerIP = InetAddress.getByName(ServerIPString); 
		} catch (UnknownHostException e) {
			e.printStackTrace();   //Catches Error
		}
	   	debug("[Client] Variables Registered:||Player:"+Sender+" ||Status Check ||Server:"+ Server +"  ||IP:"+ ServerIP  +":"+ serverPort); 
   		startStatusCheckClientSocket(ServerIP, serverPort, sender, Sender, Server,  PassKey);
    	return true;
    }
    public void startStatusCheckClientSocket(InetAddress Server, int port, CommandSender sender, String Sender, String remoteServer, String PassKey){ //This Method Starts the Greeting Server and allows Greeting Clients to Send Message  to this plugin to be Ran.
    	new Thread(() -> {
    	    StatusCheckClient(Server, port, sender, Sender, remoteServer, PassKey);
    	}).start();
    }
    public void StatusCheckClient(InetAddress Server, int port, CommandSender sender, String Sender, String remoteServer, String PassKey) { //Connects to GreetingServer listener on other server. issues Command. Displays response from GreetingServer.
    	//## Method Variable Declaration ##
		String Incoming;
		
		//## Attempting Connection ##
    	try {
    		//## Declaring Data In/Out Streams ##
    		Socket client = new Socket(Server, port);    		
    		debug( "[Remote Commands][Debug][Client] Established Connection to "+remoteServer+" at IP: "+ client.getRemoteSocketAddress());
    		OutputStream outToServer = client.getOutputStream(); //declares output Stream    		
    		DataOutputStream out = new DataOutputStream(outToServer); //declares output stream Variable
    		InputStream inFromServer = client.getInputStream(); //declares input stream
    		DataInputStream in = new DataInputStream(inFromServer); //declares input stream variable 
    		
    		//## Communicating with Remote Server ##
    		
    		out.writeUTF(PassKey); //Converts Command to Data and Outputs Stream
    		Incoming = in.readUTF();
    		if (Incoming.equals("True")) {
    			out.writeUTF("Test");
    			sender.sendMessage(PM[0]+remoteServer+PM[24]);
    		} else if (Incoming.equalsIgnoreCase("Invalid Passkey")){
    			sender.sendMessage(PM[0]+remoteServer+ PM[25]); 
    			System.out.println("[Remote Commands][Client][Error] A Command Was Rejected by "+remoteServer+" For an Invalid Passkey");

    		} else {
    			sender.sendMessage(PM[0]+PM[21]);
    			System.out.println("[Remote Commands][Client][Error] An Error Occured at "+remoteServer);  
    			System.out.println("[Remote Commands][Client][Error] Please Contact Aikidored at https://discord.gg/RYTfade. Your Error Code is [01]");  			
    		}
    		//## Closes Connection ##
    		client.close(); //Closes Client Socket to allow for remote server to accept new connections
    	} catch (IOException e) {
			sender.sendMessage(PM[0]+remoteServer+ PM[25]); 
    	}
    }
   
    //######################
    // Send Command Methods#
    //######################
    public boolean sendCommand(CommandSender sender, String Server, String Command) {
		String Sender = sender.toString();
		int serverIndex = getIndex(Server); 
		String PassKey = t.getEncryptedKey(Passkey);
	    String ServerIPString = GetServerIp(serverIndex); 
	   	int serverPort = GetServerPort(serverIndex);
	   	InetAddress ServerIP = null; 
		try {
			ServerIP = InetAddress.getByName(ServerIPString); 
		} catch (UnknownHostException e) {
			e.printStackTrace();   //Catches Error
		}
	   	debug("[Remote Commands][Debug][Client] Variables Registered:||Player:"+Sender+" ||Command:" + Command +"  ||Server:"+ Server +"  ||IP:"+ ServerIP  +":"+ serverPort); 
   		startCommandClientSocket(ServerIP, serverPort, sender, Sender, Server,  PassKey, Command);
    	return true;
    }
    public boolean CSendAll(CommandSender sender, String Command) {
    	boolean check = false;
    	for (int u = 0; u< ServerList.length; u++) {
    		check = sendCommand(sender, ServerList[u], Command);
    	}
    	return check;
    }
    public void startCommandClientSocket(InetAddress Server, int port, CommandSender sender, String Sender, String remoteServer, String PassKey, String Command){ //This Method Starts the Greeting Server and allows Greeting Clients to Send Message  to this plugin to be Ran.
    	new Thread(() -> {
    	    CommandClient(Server, port, sender, Sender, remoteServer, PassKey, Command);
    	}).start();
    }
    public void CommandClient(InetAddress Server, int port, CommandSender sender, String Sender, String remoteServer, String PassKey, String Command) { //Connects to GreetingServer listener on other server. issues Command. Displays response from GreetingServer.
    	//## Method Variable Declaration ##
		String Incoming;
		
		//## Attempting Connection ##
    	try {
    		//## Declaring Data In/Out Streams ##
    		Socket client = new Socket(Server, port);    		
    		debug( "[Remote Commands][Debug][Client] Established Connection to "+remoteServer+" at IP: "+ client.getRemoteSocketAddress());
    		OutputStream outToServer = client.getOutputStream(); //declares output Stream    		
    		DataOutputStream out = new DataOutputStream(outToServer); //declares output stream Variable
    		InputStream inFromServer = client.getInputStream(); //declares input stream
    		DataInputStream in = new DataInputStream(inFromServer); //declares input stream variable 
    		
    		//## Communicating with Remote Server ##
    		
    		out.writeUTF(PassKey); //Converts Command to Data and Outputs Stream
    		Incoming = in.readUTF();
    		if (Incoming.equals("True")) {
    			out.writeUTF("Command");
    			out.writeUTF(serverName);
    			out.writeUTF(Sender);
    			out.writeUTF(Command);
    			debug("[Remote Commands][Debug][Client] "+Sender+" Sent "+Command+" to "+remoteServer);
    	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	    	logToFile("["+timeStamp+"][Send] Player ["+Sender+"] Sent ["+Command+"] to ["+remoteServer+"]");
    		} else if (Incoming.equalsIgnoreCase("Invalid Passkey")){
    			sender.sendMessage(PM[0]+PM[14]);
    			System.out.println("[Remote Commands][Client][Error] A Command Was Rejected by "+remoteServer+" For an Invalid Passkey");

    		} else if (Incoming.equalsIgnoreCase("OFF")){
    			sender.sendMessage(PM[18]+PM[14]);
    			System.out.println("[Remote Commands][Client][Error] A Command Was Rejected by "+remoteServer+" - Remote Server is Not Accepting Commands");
    		} else {
    			sender.sendMessage(PM[0]+PM[21]);
    			System.out.println("[Remote Commands][Client][Error] A Command Was Rejected by "+remoteServer);  
    			System.out.println("[Remote Commands][Client][Error] Please Contact Aikidored at https://discord.gg/RYTfade. Your Error Code is [01]");  			
    		}
    		//## Closes Connection ##
    		client.close(); //Closes Client Socket to allow for remote server to accept new connections
    	} catch (IOException e) {
			sender.sendMessage(PM[0]+PM[15]);    		
    		System.out.println("[Remote Commands][Client][Error] Could not connect to "+remoteServer);
    		System.out.println("[Remote Commands][Client][Error] Please check is remote server is online and configured Properly");
	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	    	logErrorToFile("["+timeStamp+"] Could Not connect to "+remoteServer+" at: "+Server.toString()+":"+port);
    	}
    }

    
    //########################
    // Send Broadcast Methods#
    //########################
    public boolean sendBroadcast(CommandSender sender, String Server, String Command) {
		String Sender = sender.toString();
		int serverIndex = getIndex(Server); 
		String PassKey = t.getEncryptedKey(Passkey);
	    String ServerIPString = GetServerIp(serverIndex); 
	   	int serverPort = GetServerPort(serverIndex);
	   	InetAddress ServerIP = null; 
		try {
			ServerIP = InetAddress.getByName(ServerIPString); 
		} catch (UnknownHostException e) {
			e.printStackTrace();   //Catches Error
		}
	   	debug("[Remote Commands][Debug][Client] Variables Registered:||Player:"+Sender+" ||Command:" + Command +"  ||Server:"+ Server +"  ||IP:"+ ServerIP  +":"+ serverPort); 
   		startBroadcastClientSocket(ServerIP, serverPort, sender, Sender, Server,  PassKey, Command);
    	return true;
    }
    public boolean BSendAll(CommandSender sender, String Command) {
    	boolean check = false;
    	for (int u = 0; u< ServerList.length; u++) {
    		check = sendBroadcast(sender, ServerList[u], Command);
    	}
    	return check;
    }
    public void startBroadcastClientSocket(InetAddress Server, int port, CommandSender sender, String Sender, String remoteServer, String PassKey, String Command){ //This Method Starts the Greeting Server and allows Greeting Clients to Send Message  to this plugin to be Ran.
    	new Thread(() -> {
    	    BroadcastClient(Server, port, sender, Sender, remoteServer, PassKey, Command);
    	}).start();
    }
    public void BroadcastClient(InetAddress Server, int port, CommandSender sender, String Sender, String remoteServer, String PassKey, String Command) { //Connects to GreetingServer listener on other server. issues Command. Displays response from GreetingServer.					
    	//## Method Variable Declaration ##
		String Incoming;
		
		//## Attempting Connection ##
    	try {
    		//## Declaring Data In/Out Streams ##
    		Socket client = new Socket(Server, port);    		
    		debug( "[Remote Commands][Debug][Client] Established Connection to "+remoteServer+" at IP: "+ client.getRemoteSocketAddress());
    		OutputStream outToServer = client.getOutputStream(); //declares output Stream    		
    		DataOutputStream out = new DataOutputStream(outToServer); //declares output stream Variable
    		InputStream inFromServer = client.getInputStream(); //declares input stream
    		DataInputStream in = new DataInputStream(inFromServer); //declares inputstream variable 
    		
    		//## Communicating with Remote Server ##
    		
    		out.writeUTF(PassKey); //Converts Command to Data and Outputs Stream
    		Incoming = in.readUTF();
    		if (Incoming.equals("True")) {
    			out.writeUTF("Broadcast");
    			out.writeUTF(serverName);
    			out.writeUTF(Sender);
    			out.writeUTF(Command);
    			debug("[Remote Commands][Debug][Client] "+Sender+" Sent "+Command+" to "+remoteServer);
    	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	    	logToFile("["+timeStamp+"][Send] Player ["+Sender+"] Sent ["+Command+"] to ["+remoteServer+"]");
    		} else if (Incoming.equalsIgnoreCase("Invalid Passkey")){
    			sender.sendMessage(PM[18]+PM[14]);
    			System.out.println("[Remote Commands][Client][Error] A Broadcast Was Rejected by "+remoteServer+" For an Invalid Passkey");
    		} else if (Incoming.equalsIgnoreCase("OFF")){
    			sender.sendMessage(PM[18]+PM[19]);
    			System.out.println("[Remote Commands][Client][Error] A Broadcast Was Rejected by "+remoteServer+" - Remote Server Not Accepting Broadcasts");
    		} else {
    			sender.sendMessage(PM[18]+PM[13]);
    			System.out.println("[Remote Commands][Client][Error] A Broadcast Was Rejected by "+remoteServer);  
    			System.out.println("[Remote Commands][Client][Error] Please Contact Aikidored at https://discord.gg/RYTfade. Your Error Code is [01]");  			
    		}
    		//## Closes Connection ##
    		client.close(); //Closes Client Socket to allow for remote server to accept new connections
    	} catch (IOException e) {
			sender.sendMessage(PM[18]+PM[15]);    		
    		System.out.println("[Remote Commands][Client][Error] Could not connect to "+remoteServer);
    		System.out.println("[Remote Commands][Client][Error] Please check is remote server is online and configured Properly");
	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	    	logErrorToFile("["+timeStamp+"] Could Not connect to "+remoteServer+" at: "+Server.toString()+":"+port);
    	}
    }
    
   //#######################
   // Start Plugin Methods #
   //#######################
   public void startListenerSocket(int Port){ //This Method Starts the Greeting Server and allows Greeting Clients to Send Message  to this plugin to be Ran.            
  	if (pluginStarted == false) {
   		try {
   	         Thread t = new Server(Port); //Creates new Asynchronous Thread for Listener
   	         t.start();
   	    } catch (IOException e) {
   	         e.printStackTrace();
   	    }
       		pluginStarted = true;
   }
   		
}
   public void startRepeatingTask() { // Checks If Commands Are Awaiting to be Issued     
   	debug("[Remote Commands][Debug] Starting Repeating Clock");
   	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				if (commandWaiting == true) {
					for(int x = 0; x < WCLIndex; x++) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), WCL[x]);
						
					}
						commandWaiting = false;
						WCLIndex = 0;
						debug("[Remote Commands][Debug] Awaiting Commands Issued");
				}
				if (LSLWaiting == true) {
					for(int x = 0; x < LSLIndex; x++) {
						logToFile(LSL[x]);
					}
						LSLWaiting = false;
						LSLIndex = 0;
						debug("[Remote Commands][Debug] Awaiting Log Entries Issued");
				}				
			}    		
   	}, 100L, 100L);
   }
   
   //#######################
   // loadPlugin Methods #
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
        
    	configi = new File(getDataFolder(), "Config-Info.yml");    	
        if (!configi.exists()) {
            configi.getParentFile().mkdirs();
            saveResource("Config-Info.yml", false);
        }
        config2 = new YamlConfiguration();       
        try {
            config2.load(configi);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        } 
        
        
        //########### Retreiving Information ##############
        LSD = config.getBoolean("Log-Startup-Dump");
        logSID("Starting Log Dump");
        logSID("");
        logSID("");
        logSID("");
        
        
        System.out.println("");		
		Debug = config.getBoolean("Debug"); // Gets Debug Value 
		debug("Debug Value: "+ Debug);
        logSID("Debug Value: "+ Debug);
		
		if (Version == config2.getDouble("Config-Version"))   {debug("Config Version Correct");logSID("Config Version Correct");}   else   { M.ConfigOutdated();}  // Checks if Config Version is Correct
		if (config2.getBoolean("FirstLoad") == true) { M.FirstLoad();logSID("First Load"); config2.set("FirstLoad", false); saveConfigFile();}

		serverName = config.getString("Server-Name");
		debug("Server Name: "+ serverName);
        logSID("Server Name: "+ serverName);
		
		Passkey = config.getInt("PassKey");
		debug("Passkey: "+ Passkey);
        logSID("Passkey: "+ Passkey);
		
		Port = config.getInt("Port-Listener");
		debug("Port Retrieved from Config: "+Port);
        logSID("Port Retrieved from Config: "+Port);
		
		listCount = config.getInt("Server-Count");
		debug("Server List Count: "+ listCount);
        logSID("Server List Count: "+ listCount);

        System.out.println("");	
        FeatureToggles[0] = config.getBoolean("T0"); // Gets Commands Received Value
		debug("Allow Commands Received Value: "+ FeatureToggles[0]);
        logSID("Allow Commands Received Value: "+ FeatureToggles[0]);		
        FeatureToggles[1] = config.getBoolean("T1"); // Gets Commands Sent Value
		debug("Allow Commands Sent Value: "+ FeatureToggles[1]);	
        logSID("Allow Commands Sent Value: "+ FeatureToggles[1]);	
        FeatureToggles[2] = config.getBoolean("T2"); // Gets Broadcasts Received Value
		debug("Allow Broadcasts Received Value: "+ FeatureToggles[2]);
        logSID("Allow Broadcasts Received Value: "+ FeatureToggles[2]);		
        FeatureToggles[3] = config.getBoolean("T3"); // Gets Broadcasts Sent Value
		debug("Allow Broadcasts Sent Value: "+ FeatureToggles[3]);
        logSID("Allow Broadcasts Sent Value: "+ FeatureToggles[3]);		
        FeatureToggles[4] = config.getBoolean("T4"); // Gets Incoming/Outgoing Log Value
		debug("Log Incoming/OutGoing Value: "+ FeatureToggles[4]);
        logSID("Log Incoming/OutGoing Value: "+ FeatureToggles[4]);		
        FeatureToggles[5] = config.getBoolean("T5"); //Gets Error Log Value
		debug("Log Errors Value: "+ FeatureToggles[5]);
        logSID("Log Errors Value: "+ FeatureToggles[5]);
        
		
		debug("Loaded Config.yml");
		logSID("Loaded Config.yml");
        System.out.println("");	
        System.out.println("[Remote Commands][Status] Config has been Loaded");		
	}
	public void loadServersFile() { //Loads Servers    
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
        
        //########### Retreiving Information ##############
        
    	debug("Loading Servers.yml");
    	debug("Total Servers Found: "+listCount);
        logSID("Total Servers Found: "+listCount);
    	ServerList = new String[listCount]; 
    	ServerAddresses = new String[listCount]; 
    	ServerPorts = new int[listCount];
    	debug("Displaying Servers Found in Servers.yml");
        logSID("Displaying Servers Found in Servers.yml");
    	for (int x = 0; x < listCount; x++) { 
    		String serverName = Servers.getString("Server-List."+ x +".Name");
    		String serverAddress = Servers.getString("Server-List."+x+".Address");
    		int serverPort = Servers.getInt("Server-List."+x+".Port");
    		ServerList[x] = serverName;
    		ServerAddresses[x] =  serverAddress;
    		ServerPorts[x] = serverPort; 
    		int y = x+1;
    		debug(y+": |Name: "+serverName+"| |Host: "+ serverAddress+"| |Port: "+serverPort+"|");
            logSID(y+": |Name: "+serverName+"| |Host: "+ serverAddress+"| |Port: "+serverPort+"|");
    	}
    	debug("Servers.yml Loaded");
        logSID("Servers.yml Loaded");
    	

        System.out.println("");	
        System.out.println("[Remote Commands] Servers List has been Loaded");
	}
	public void loadMessageFile() { //Loads Custom Messages    
    	configm = new File(getDataFolder(), "Messages.yml");
    	
        if (!configm.exists()) {
            configm.getParentFile().mkdirs();
            saveResource("Messages.yml", false);
        }
        Messages = new YamlConfiguration();
        
        try {
            Messages.load(configm);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        } 
        
        //########### Retreiving Information ##############
    	debug("Loading Messages");
        logSID("Loading Messages");
    	PM[0] = ChatColor.translateAlternateColorCodes('&', Messages.getString("GeneralPrefix")); 					   debug("[Message]GeneralPrefix: "+PM[0]); 						logSID("[Message]GeneralPrefix: "+PM[0]); 	
    	PM[1] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Debug-On")); 						   debug("[Message]Debug-On: "+PM[1]);    							logSID("[Message]Debug-On: "+PM[1]);
    	PM[2] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Debug-Off")); 						   debug("[Message]Debug-Off: "+PM[2]);  							logSID("[Message]Debug-Off: "+PM[2]);
    	//PM[3] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Sender-Confirmation"));			 	   debug("[Message]Sender-Confirmation: "+PM[3]); 					logSID("[Message]Sender-Confirmation: "+PM[3]);
    	PM[4] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-Menu-Title")); 			 	   debug("[Message]Help-Menu-Title: "+PM[4]); 						logSID("[Message]Help-Menu-Title: "+PM[4]);
    	PM[5] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-1")); 							   debug("[Message]Help-1: "+PM[5]); 								logSID("[Message]Help-1: "+PM[5]);
    	PM[6] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-2")); 							   debug("[Message]Help-2: "+PM[6]); 								logSID("[Message]Help-2: "+PM[6]);
    	PM[7] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-3")); 						 	   debug("[Message]Help-3: "+PM[7]); 								logSID("[Message]Help-3: "+PM[7]);
    	PM[8] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-4")); 						       debug("[Message]Help-4: "+PM[8]);								logSID("[Message]Help-4: "+PM[8]);
    	PM[9] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-5")); 							   debug("[Message]Help-5: "+PM[9]); 								logSID("[Message]Help-5: "+PM[9]);
    	PM[10] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-6")); 						   debug("[Message]Help-6: "+PM[10]); 								logSID("[Message]Help-6: "+PM[10]);
    	PM[11] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-More-Arguments-Needed"));       debug("[Message]Error-More-Arguments-Needed: "+PM[11]); 			logSID("[Message]Error-More-Arguments-Needed: "+PM[11]);
    	PM[12] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Displaying-Help-Menu")); 	   debug("[Message]Error-Displaying-Help-Menu: "+PM[12]); 			logSID("[Message]Error-Displaying-Help-Menu: "+PM[12]);
    	PM[13] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Command-Send-Misc")); 		   debug("[Message]Error-Command-Send-Misc: "+PM[13]); 				logSID("[Message]Error-Command-Send-Misc: "+PM[13]);
    	PM[14] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Invalid-Passkey")); 			   debug("[Message]Error-Invalid-Passkey: "+PM[14]); 				logSID("[Message]Error-Invalid-Passkey: "+PM[14]);
    	PM[15] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Connection-Error")); 		   debug("[Message]Error-Connection-Error: "+PM[15]); 				logSID("[Message]Error-Connection-Error: "+PM[15]);
    	PM[16] = ChatColor.translateAlternateColorCodes('&', Messages.getString("RC-Server-List")); 				   debug("[Message]RC-Server-List: "+PM[16]); 						logSID("[Message]RC-Server-List: "+PM[16]);
    	PM[17] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Plugin-Reload")); 					   debug("[Message]Plugin-Reload: "+PM[17]); 						logSID("[Message]Plugin-Reload: "+PM[17]);
    	PM[18] = ChatColor.translateAlternateColorCodes('&', Messages.getString("BroadcastPrefix")); 				   debug("[Message]BroadcastPrefix: "+PM[18]);						logSID("[Message]BroadcastPrefix: "+PM[18]);
    	PM[19] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Not-Accepting-Broadcasts"));    debug("[Message]Error-Not-Accepting-Broadcasts: "+PM[19]); 		logSID("[Message]Error-Not-Accepting-Broadcasts: "+PM[19]);
    	PM[20] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-7")); 						   debug("[Message]Help-7: "+PM[20]);   							logSID("[Message]Help-7: "+PM[20]);
    	PM[21] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Not-Accepting-Commands"));	   debug("[Message]Error-Not-Accepting-Commands: "+PM[21]); 		logSID("[Message]Error-Not-Accepting-Commands: "+PM[21]);
    	PM[22] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Sending-Commands-Disabled"));   debug("[Message]Error-Sending-Commands-Disabled: "+PM[22]);  	logSID("[Message]Error-Sending-Commands-Disabled: "+PM[22]);
    	PM[23] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Sending-Commands-Disabled"));   debug("[Message]Error-Sending-Commands-Disabled: "+PM[23]);  	logSID("[Message]Error-Sending-Commands-Disabled: "+PM[23]);
    	PM[24] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Status-Online")); 					   debug("[Message]Status-Online: "+PM[24]); 						logSID("[Message]Status-Online: "+PM[24]);
    	PM[25] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Status-Offline")); 				   debug("[Message]Status-Offline: "+PM[25]); 						logSID("[Message]Status-Offline: "+PM[25]);
    	PM[26] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-8")); 						   debug("[Message]Help-8: "+PM[26]); 								logSID("[Message]Help-8: "+PM[26]);
    	debug("Finished Loading Messages");
        logSID("Finished Loading Messages");
        
        System.out.println("[Remote Commands] Messages.yml Loaded");
        
        
		
	}
	public void saveConfigFile() {
		try {
				config2.save(configi);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void loadBstats() {
		int pluginId = 6397; // <-- BSTATS Value
        @SuppressWarnings("unused")// <-- BSTATS Value
		MetricsLite metrics = new MetricsLite(this, pluginId);// <-- BSTATS Value
    	serverName = config.getString("Server-Name");
	}
	
	//###############
   // Log  Methods #
   //###############
   public static void addLog(String Message) {		//Accepts Log Entries from Other Classes and Adds them to Waiting List to be Added to Log
	   LSL[LSLIndex] = Message;
	   LSLWaiting = true;
	   LSLIndex++;
   }

	public void logSID(String message) {			// Logs Startup Info Dump
		 if (LSD == true) {
				String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
				
		        try
		        {
		            File RecievedCommandLog = getDataFolder();
		            if(!RecievedCommandLog.exists())
		            {
		            	RecievedCommandLog.mkdir();
		            } 
		            File saveTo = new File(getDataFolder(), "StartupLog.txt");
		            if (!saveTo.exists())
		            {
		                saveTo.createNewFile();
		            } 
		            FileWriter fw = new FileWriter(saveTo, true); 
		            PrintWriter pw = new PrintWriter(fw);
		            pw.println("|"+timeStamp+"|[SID] - "+message);
		            pw.flush(); 
		            pw.close(); 
		        } catch (IOException e)
		        {
		            e.printStackTrace();
		        } 
		 }
   }
	public void logToFile(String message) {			// Logs Recieved Message to File
		 if (FeatureToggles[4] == true) {
		        try
		        {
		            File RecievedCommandLog = getDataFolder();
		            if(!RecievedCommandLog.exists())
		            {
		            	RecievedCommandLog.mkdir();
		            } 
		            File saveTo = new File(getDataFolder(), "ActivityLog.txt");
		            if (!saveTo.exists())
		            {
		                saveTo.createNewFile();
		            } 
		            FileWriter fw = new FileWriter(saveTo, true); 
		            PrintWriter pw = new PrintWriter(fw);
		            pw.println(message);
		            pw.flush(); 
		            pw.close(); 
		        } catch (IOException e)
		        {
		            e.printStackTrace();
		        } 
		 }
    }
	public void logErrorToFile(String message) {
		 
        try
        {
            File ErrorLog = getDataFolder();
            if(!ErrorLog.exists())
            {
            	ErrorLog.mkdir();
            } 
            File saveTo = new File(getDataFolder(), "ErrorLog.txt");
            if (!saveTo.exists())
            {
                saveTo.createNewFile();
            } 
            FileWriter fw = new FileWriter(saveTo, true); 
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush(); 
            pw.close(); 
        } catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
	public static void debug(String D) {
	   if (Debug == true) {
		   System.out.println("[Remote Commands][Debug]-> "+D);
	   }
   }
   
}

