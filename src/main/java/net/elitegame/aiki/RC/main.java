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
	//#####################################################################################
	//                   Todo Ideas / Plans
	//#####################################################################################
	
		// Integrate Remote Broadcast With Custom Colors
		// Code Cleanup / Code Re-Organization
		// Look into Improving Encryption / Decryption of Passkey
		// Plugin User Data Bug

	//#####################################################################################
	//                   Premium Todo Ideas / Plans
	//#####################################################################################
		
		// DiscordSRV Integration
		// Basic Web Integration(If Possible?)
		// 
	
	//#####################################################################################
	//                   Declarations
	//#####################################################################################
	//####   Plugin Variables   ####
	String[] argArray; // Array to Hold Commands Sent to this Plugin
    static boolean commandWaiting = false; // Declares 
    static String receivedCommand = " "; // Stores Received Command
    static String LoggedString = " "; // Stores Received String 
    static String[] WCL = new String[20]; //WCL = Waiting Command List. Stores Commands in queue 
    static String[] LSL = new String[20]; //LSL = Logged String List. Stores Log Messages in queue
    static int Index = 0;
	
	//####   Config Variables   ####
	int Port = 4000; // This Temporarily Sets This Plugins Listener Port.
    int listCount = 100; // Defines Array Sizes for Servers
    static boolean Debug = false; // Debug Value Default as False
    double Version = 3.0;	// Current Version Number
    double ConfigV = Version; // Temporarily Set as Current Version. Changed to Value in Config After Config Loaded 
    static String serverName = " "; // Temporarily Set as Blank. Changed To Value in Config
    boolean pluginStarted = false; 
    
    //####   Messages Variables   ####
    static String[] PM = new String[26]; // Array Contains All Messages Defined in Messages.yml
    
    //####   Servers Variables   ####
	String ServerList[]; //Array containing List of the names of servers listed in the Servers.yml
	String ServerAddresses[]; //Array Containing list of all Server IP's listed in Servers.yml
	int ServerPorts[];	//Array Containing list of all ports listed in Servers.yml
	
	//####   Files   ####
	//#### Declares All File Systems
    public File configf;
    public static FileConfiguration config;
    public File configs;
    public static FileConfiguration Servers;
    public File configl;
    public static FileConfiguration Log;
    public File configm;
    public static FileConfiguration Messages;
	
	//####   Declarations   #####
	Encryption t = new Encryption();
	ConsoleCommandSender commandSender = Bukkit.getServer().getConsoleSender();
	
	
    //#####################################################################################
  	//                   Enable/Disable Methods
  	//#####################################################################################
    
	@Override
    public void onEnable() {    // Displays Enable Message 
		loadPlugin();
    	System.out.println("########################"); 
    	System.out.println("# Remote Commands v3.0 #");
    	System.out.println("########################");
    	System.out.println("[Remote Commands] Made by Aikidored");
    }
    @Override
    public void onDisable() {
        // Displays Disable Message
    	System.out.println( "########################"); 
    	System.out.println( "# Remote Commands v3.0 #");
    	System.out.println( "########################");
    	System.out.println("[Remote Commands] Made by Aikidored");
    }	
    
    //#####################################################################################
  	//                  Load Plugin  Method
  	//#####################################################################################    
    public void loadPlugin() {
		loadConfigFile();
		Debug = config.getBoolean("Debug");
		debug("[Remote Commands][Debug] Loaded Config.yml");
		loadServersFile();
		debug("[Remote Commands][Debug] Checking Config.yml Version");
		checkVersion();
		loadMessageFile();
		loadMessages();
		LoadArrays();
		startRepeatingTask();
		Port = getConfigListenerPort();
		StartListener(Port);
		int pluginId = 6397; // <-- BSTATS Value
        @SuppressWarnings("unused")// <-- BSTATS Value
		MetricsLite metrics = new MetricsLite(this, pluginId);// <-- BSTATS Value
        getServerName();
	}
    public void StartListener(int Port) {
    	if (pluginStarted == false) {
    		startListenerSocket(Port);
    		pluginStarted = true;
    	}
    }
    
    //#####################################################################################
  	//                  Config Data Loading Method 
  	//#####################################################################################
	public void checkVersion() {
		ConfigV = config.getDouble("Config-Version");
		if(ConfigV == Version){
			debug("[Remote Commands][Debug] Config Up to Date");
		} else {
			System.out.println("[Remote Commands][Warning] Config OutDated");
			debug("[Remote Commands][Debug][Warning] Current Version is: "+Version);
			debug("[Remote Commands][Debug][Warning] Config Version is: "+ ConfigV);
		}			
	}
    public static int getConfigPassKey() { //Returns PassKey from Config
    	int Key = config.getInt("PassKey");
    	return Key;
    }
    public int getConfigListenerPort() { 
    	int Port = config.getInt("Port-Listener");
    	debug("[Remote Commands][Debug] Port Retrieved from Config: "+Port);
    	return Port; 
    }
    public void LoadArrays() { 
    	debug("[Remote Commands][Debug] Loading Servers.yml");
    	listCount = config.getInt("Server-Count"); 
    	debug("[Remote Commands][Debug] Total Servers Found: "+listCount);
    	ServerList = new String[listCount]; 
    	ServerAddresses = new String[listCount]; 
    	ServerPorts = new int[listCount]; 
    	debug("[Remote Commands][Debug] Servers Found in Servers.yml");
    	for (int x = 0; x < listCount; x++) { 
    		String serverName = Servers.getString("Server-List."+ x +".Name");
    		String serverAddress = Servers.getString("Server-List."+x+".Address");
    		int serverPort = Servers.getInt("Server-List."+x+".Port");
    		int y = x+1;
    		debug("[Remote Commands][Debug] "+y+". Name: "+serverName+" Host: "+ serverAddress+" Port: "+serverPort);
    		ServerList[x] = serverName;
    		ServerAddresses[x] =  serverAddress;
    		ServerPorts[x] = serverPort; 
    	}
    	debug("[Remote Commands][Debug] Servers.yml Loaded");
    }
    public void getServerName() {
    	serverName = config.getString("Server-Name");
    }
    
    //#####################################################################################
  	//                  On Command Method
  	//#####################################################################################
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws IndexOutOfBoundsException{
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
        		debug("[Remote Commands][Debug] CommandArgument: "+ Argument0);
        		debug("[Remote Commands][Debug] Label: "+ label);
        		debug("[Remote Commands][Debug] Server List:" + DebugServerlist);
        		debug("[Remote Commands][Debug] Server Address:" + DebugServerAddress);
        		debug("[Remote Commands][Debug] Server Ports:" + DebugServerPorts);
        		debug("[Remote Commands][Debug] Message: "+DebugMessage);
        		
        		boolean checkServerWithinArray = checkArray(Argument0);
        		if(Argument0.equalsIgnoreCase("Reload") == true) {
        			checkBool = Reload(sender);
        		}
        		else if (Argument0.equalsIgnoreCase("Help")) {
        			checkBool = HelpCommand(sender);
        		}
        		else if (Argument0.equalsIgnoreCase("List")) {
        			checkBool = list(sender);
        		}
        		else if (argArray.length < 2) {
        			sender.sendMessage(PM[0]+PM[11]);
        			checkBool = HelpCommand(sender);
        		}
        		else if (Argument0.equalsIgnoreCase(serverName) == true) {
        			checkBool = true;
    				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Command);
        	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        	    	logSendToFile("["+timeStamp+"][Send] Player ["+sender+"] Sent Command:["+Command+"] to ["+Argument0+"]");
        		}
        		else if (Argument0.equalsIgnoreCase("SendAll") == true) {
        			checkBool = CSendAll(sender, Command);
        		}
        		else if(checkServerWithinArray == true) {
        			checkBool = sendCommand(sender, Argument0, Command);
        		} else {
        			sender.sendMessage(PM[0]+PM[12]);
        			checkBool = HelpCommand(sender);
        		} 
        		return checkBool;
    		} else if (label.equalsIgnoreCase("RB") == true) {
        	   	String Argument0 = args[0];
        	   	String Broadcast;
        		boolean checkBool = false;
        		argArray = new String[args.length];	
        		for(int h = 0; h < args.length-1; h++) {
        			argArray[h] = args[h+1];
        		}
        		argArray[argArray.length-1] = " ";
            	Broadcast = String.join(" ", argArray);

        		String DebugMessage = String.join(" ", args); 
        		String DebugServerlist = String.join(" ", ServerList); 
        		String DebugServerAddress = String.join(" ", ServerAddresses);
        		String DebugServerPorts = StringUtils.join(ArrayUtils.toObject(ServerPorts), " - ");
        		debug("[Remote Commands][Debug] CommandArgument: "+ Argument0);
        		debug("[Remote Commands][Debug] Label: "+ label);
        		debug("[Remote Commands][Debug] Server List:" + DebugServerlist);
        		debug("[Remote Commands][Debug] Server Address:" + DebugServerAddress);
        		debug("[Remote Commands][Debug] Server Ports:" + DebugServerPorts);
        		debug("[Remote Commands][Debug] Message: "+DebugMessage);
        		
        		boolean checkServerWithinArray = checkArray(Argument0);
        		if(Argument0.equalsIgnoreCase("Reload") == true) {
        			checkBool = Reload(sender);
        		}
        		else if (Argument0.equalsIgnoreCase("Help")) {
        			checkBool = HelpBroadcast(sender);
        		}
        		else if (Argument0.equalsIgnoreCase("List")) {
        			checkBool = list(sender);
        		}
        		else if (argArray.length < 2) {
        			sender.sendMessage(PM[18]+PM[11]);
        			checkBool = HelpBroadcast(sender);
        		}
        		else if (Argument0.equalsIgnoreCase(serverName) == true) {
        			checkBool = true;
    				Bukkit.broadcastMessage(PM[18] + Broadcast);
        	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        	    	logSendToFile("["+timeStamp+"][Send] Player ["+sender+"] Sent Broadcast:["+Broadcast+"] to ["+Argument0+"]");
        		}
        		else if (Argument0.equalsIgnoreCase("SendAll") == true) {
        			checkBool = BSendAll(sender, Broadcast);
        		}
        		else if(checkServerWithinArray == true) {
        			checkBool = sendBroadcast(sender, Argument0, Broadcast);
        		} else {
        			sender.sendMessage(PM[18]+PM[12]);
        			checkBool = HelpBroadcast(sender);
        		} 
        		return checkBool;
    		} else {
    			return false;
    		}
    	}
    	else {
    	    return HelpCommand(sender);
    	}
	   	

    }
    //#####################################################################################
  	//                   Broadcast Methods
  	//##################################################################################### 
    public boolean sendBroadcast(CommandSender sender, String Server, String Command) {
		String Sender = sender.toString();
		int serverIndex = getIndex(Server); 
		String Passkey = t.getEncryptedKey(getConfigPassKey());
	    String ServerIPString = GetServerIp(serverIndex); 
	   	int serverPort = GetServerPort(serverIndex);
	   	InetAddress ServerIP = null; 
		try {
			ServerIP = InetAddress.getByName(ServerIPString); 
		} catch (UnknownHostException e) {
			e.printStackTrace();   //Catches Error
		}
	   	debug("[Remote Commands][Debug][Client] Variables Registered:||Player:"+Sender+" ||Command:" + Command +"  ||Server:"+ Server +"  ||IP:"+ ServerIP  +":"+ serverPort); 
   		startBroadcastClientSocket(ServerIP, serverPort, sender, Sender, Server,  Passkey, Command);
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
    			sender.sendMessage(PM[18]+PM[3]);
    			debug("[Remote Commands][Debug][Client] "+Sender+" Sent "+Command+" to "+remoteServer);
    	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	    	logSendToFile("["+timeStamp+"][Send] Player ["+Sender+"] Sent ["+Command+"] to ["+remoteServer+"]");
    		} else if (Incoming.equalsIgnoreCase("Invalid Passkey")){
    			sender.sendMessage(PM[18]+PM[14]);
    			System.out.println("[Remote Commands][Client][Error] A Broadcast Was Rejected by "+remoteServer+" For an Invalid Passkey");
    	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	    	logSendToFile("["+timeStamp+"][Error] "+remoteServer+" Denied Connection for an Invalid Passkey");
    			
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
    
    //#####################################################################################
  	//                   Command Methods
  	//#####################################################################################   
    
    
    
    public boolean sendCommand(CommandSender sender, String Server, String Command) {
		String Sender = sender.toString();
		int serverIndex = getIndex(Server); 
		String Passkey = t.getEncryptedKey(getConfigPassKey());
	    String ServerIPString = GetServerIp(serverIndex); 
	   	int serverPort = GetServerPort(serverIndex);
	   	InetAddress ServerIP = null; 
		try {
			ServerIP = InetAddress.getByName(ServerIPString); 
		} catch (UnknownHostException e) {
			e.printStackTrace();   //Catches Error
		}
	   	debug("[Remote Commands][Debug][Client] Variables Registered:||Player:"+Sender+" ||Command:" + Command +"  ||Server:"+ Server +"  ||IP:"+ ServerIP  +":"+ serverPort); 
   		startCommandClientSocket(ServerIP, serverPort, sender, Sender, Server,  Passkey, Command);
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
    			sender.sendMessage(PM[0]+PM[3]);
    			debug("[Remote Commands][Debug][Client] "+Sender+" Sent "+Command+" to "+remoteServer);
    	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	    	logSendToFile("["+timeStamp+"][Send] Player ["+Sender+"] Sent ["+Command+"] to ["+remoteServer+"]");
    		} else if (Incoming.equalsIgnoreCase("Invalid Passkey")){
    			sender.sendMessage(PM[0]+PM[14]);
    			System.out.println("[Remote Commands][Client][Error] A Command Was Rejected by "+remoteServer+" For an Invalid Passkey");
    	    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	    	logSendToFile("["+timeStamp+"][Error] "+remoteServer+" Denied Connection for an Invalid Passkey");
    			
    		} else {
    			sender.sendMessage(PM[0]+PM[13]);
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
    public void startRepeatingTask() {
    	debug("[Remote Commands][Debug] Starting Repeating Clock");
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				if (commandWaiting == true) {
					for(int x = 0; x < Index; x++) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), WCL[x]);
						logRecievedToFile(LSL[x]);
						
					}
						commandWaiting = false;
						Index = 0;
						debug("[Remote Commands][Debug] Command Issued");
						LoggedString = " ";
				}				
			}    		
    	}, 100L, 100L);
    }
    public static void addCommand(String Command, String LogString) {
    	commandWaiting = true;
    	WCL[Index] = Command;
    	LSL[Index] = LogString;
    	Index++;
    	debug("[Remote Commands][Debug] Command Loaded to memory Awaiting Repeating Task");
    	debug("[Remote Commands][Debug] Command: "+Command);
    	
    }
    public void startListenerSocket(int Port){ //This Method Starts the Greeting Server and allows Greeting Clients to Send Message  to this plugin to be Ran.
		try {
            Thread t = new Server(Port); //Creates new Asynchronous Thread for Listener
            t.start();
         } catch (IOException e) {
            e.printStackTrace();
         }
    }

    public boolean HelpCommand(CommandSender sender) {
    	sender.sendMessage(PM[4]);
    	sender.sendMessage(PM[5]); 
    	sender.sendMessage(PM[6]);
    	sender.sendMessage(PM[7]);
    	sender.sendMessage(PM[8]);
    	sender.sendMessage(PM[9]);
    	//sender.sendMessage(PM[10]);
    	return true;
    }

    public boolean HelpBroadcast(CommandSender sender) {
    	sender.sendMessage(PM[19]);
    	sender.sendMessage(PM[20]); 
    	sender.sendMessage(PM[21]);
    	sender.sendMessage(PM[22]);
    	sender.sendMessage(PM[23]);
    	sender.sendMessage(PM[24]);
    	//sender.sendMessage(PM[25]);
    	return true;
    }
    public boolean Reload(CommandSender sender) {
    	loadPlugin();
    	debug("[Remote Commands][Client] Reload Completed.");   
    	sender.sendMessage(PM[0]+PM[17]);
    	return true;
    }
    public boolean list(CommandSender sender) {
    	sender.sendMessage(PM[0]+PM[16]);
    	for (int h = 0; h < ServerList.length; h++) {
    		int i = h+1;
    		sender.sendMessage(i + ". " + ServerList[h]);
    	}
    	return true;
    }
    //#####################################################################################
  	//                   Get Data from Array Methods
  	//#####################################################################################
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
    
    public void loadMessages() {
    	debug("[Remote Commands][Debug] Loading Messages");
    	PM[0] = ChatColor.translateAlternateColorCodes('&', Messages.getString("CommandPrefix"));
    	//PM[1] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Debug-On"));    //Removed Setting. Left Commented out as Placeholder for future expansion
    	//PM[2] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Debug-Off"));   //Removed Setting. Left Commented out as Placeholder for future expansion 
    	PM[3] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Sender-Confirmation"));
    	PM[4] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-Menu-Title"));
    	PM[5] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-1"));
    	PM[6] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-2"));
    	PM[7] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-3"));
    	PM[8] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-4"));
    	PM[9] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-5"));
    	//PM[10] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-6"));  //Removed Setting. Left Commented out as Placeholder for future expansion
    	PM[11] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-More-Arguments-Needed"));
    	PM[12] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Displaying-Help-Menu"));
    	PM[13] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Command-Send-Misc"));
    	PM[14] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Invalid-Passkey"));
    	PM[15] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Error-Connection-Error"));
    	PM[16] = ChatColor.translateAlternateColorCodes('&', Messages.getString("RC-Server-List"));
    	PM[17] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Plugin-Reload"));
    	PM[18] = ChatColor.translateAlternateColorCodes('&', Messages.getString("BroadcastPrefix"));
    	PM[19] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-Menu-Title-2"));
    	PM[20] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-7"));
    	PM[21] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-8"));
    	PM[22] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-9"));
    	PM[23] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-10"));
    	PM[24] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-11"));
    	//PM[25] = ChatColor.translateAlternateColorCodes('&', Messages.getString("Help-12"));  //Removed Setting. Left Commented out as Placeholder for future expansion
    	debug("[Remote Commands][Debug][Message]Prefix: "+PM[0]);
    	//debug("[Remote Commands][Debug][Message]Debug-On: "+PM[1]);   //Removed Setting. Left Commented out as Placeholder for future expansion 
    	//debug("[Remote Commands][Debug][Message]Debug-Off: "+PM[2]);  //Removed Setting. Left Commented out as Placeholder for future expansion 
    	debug("[Remote Commands][Debug][Message]Sender-Confirmation: "+PM[3]);
    	debug("[Remote Commands][Debug][Message]Help-Menu-Title: "+PM[4]);
    	debug("[Remote Commands][Debug][Message]Help-1: "+PM[5]);
    	debug("[Remote Commands][Debug][Message]Help-2: "+PM[6]);
    	debug("[Remote Commands][Debug][Message]Help-3: "+PM[7]);
    	debug("[Remote Commands][Debug][Message]Help-4: "+PM[8]);
    	debug("[Remote Commands][Debug][Message]Help-5: "+PM[9]);
    	//debug("[Remote Commands][Debug][Message]Help-6: "+PM[10]);  //Removed Setting. Left Commented out as Placeholder for future expansion
    	debug("[Remote Commands][Debug][Message]Error-More-Arguments-Needed: "+PM[11]);
    	debug("[Remote Commands][Debug][Message]Error-Displaying-Help-Menu: "+PM[12]);
    	debug("[Remote Commands][Debug][Message]Error-Command-Send-Misc: "+PM[13]);
    	debug("[Remote Commands][Debug][Message]Error-Invalid-Passkey: "+PM[14]);
    	debug("[Remote Commands][Debug][Message]Error-Connection-Error: "+PM[15]);
    	debug("[Remote Commands][Debug][Message]RC-Server-List: "+PM[16]);
    	debug("[Remote Commands][Debug][Message]Plugin-Reload: "+PM[17]);
    	debug("[Remote Commands][Debug][Message]Help-Menu-Title-2: "+PM[19]);
    	debug("[Remote Commands][Debug][Message]Help-7: "+PM[20]);
    	debug("[Remote Commands][Debug][Message]Help-8: "+PM[21]);
    	debug("[Remote Commands][Debug][Message]Help-9: "+PM[22]);
    	debug("[Remote Commands][Debug][Message]Help-10: "+PM[23]);
    	debug("[Remote Commands][Debug][Message]Help-11: "+PM[24]);
    	//debug("[Remote Commands][Debug][Message]Help-12: "+PM[25]);  //Removed Setting. Left Commented out as Placeholder for future expansion
    	debug("[Remote Commands][Debug] Finished Loading Messages");
    }
    
    //#####################################################################################
  	//                   Debug Output Method
  	//#####################################################################################
    

    public static void debug(String Message) {
    	if (Debug == true) {
    		System.out.println(Message);
    	}
    }
    public static void logString(String Message) {
    	LoggedString = Message;
    }
    public static void logErrorString(String Message) {
    	LoggedString = Message;
    }
    
    //#####################################################################################
  	//                   File Method
  	//#####################################################################################

	public void loadConfigFile() { //Loads Config
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
        System.out.println("[Remote Commands] Config has been Loaded");		
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
        System.out.println("[Remote Commands] Servers List has been Loaded");		
	}	
	
	public void logSendToFile(String message) {
 
        try
        {
            File SentCommandLog = getDataFolder();
            if(!SentCommandLog.exists())
            {
            	SentCommandLog.mkdir();
            } 
            File saveTo = new File(getDataFolder(), "SentCommandLog.txt");
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
	public void logRecievedToFile(String message) {
 
        try
        {
            File RecievedCommandLog = getDataFolder();
            if(!RecievedCommandLog.exists())
            {
            	RecievedCommandLog.mkdir();
            } 
            File saveTo = new File(getDataFolder(), "ReceivedCommandLog.txt");
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
    //#####################################################################################
  	//                  Message.YML Method
  	//#####################################################################################

	public void loadMessageFile() { //Loads Config
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
        System.out.println("[Remote Commands] Messages.yml Loaded");
        
        
		
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
}

