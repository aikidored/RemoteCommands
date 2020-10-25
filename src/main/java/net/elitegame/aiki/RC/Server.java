package net.elitegame.aiki.RC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class Server extends Thread{
	   private ServerSocket serverSocket;
	   Encryption Decrypt = new Encryption(); //Calls Encryption Class
	   String storedMessage = " "; // String established for Storing Message
	   
	   
	   public Server(int port) throws IOException { //method for new server socket creation
	      serverSocket = new ServerSocket(port); //defines new socket
	      main.debug("Client Listening on Port: "+port);
	   }
	   public void run() { //runs Server Listener
		   while(true) {
	         try {
	             Socket server = serverSocket.accept();//Establishes Socket
	             main.debug("[Server] Connection Established by [Remote Client: " + server.getRemoteSocketAddress()+ "]");//Status Display
	             DataInputStream in = new DataInputStream(server.getInputStream()); //Declares Input Stream
	             DataOutputStream out = new DataOutputStream(server.getOutputStream());//Declares output Stream
	             
	             String Passkey = in.readUTF();
	             if (CheckKey(Passkey) == true) {
	             	String IncomingType = in.readUTF(); // Should Receive "Command", "Broadcast", or "Test"
	             	if (IncomingType.equalsIgnoreCase("Command") ) {
	             		if(main.FeatureToggles[0] == true) {
	                     	out.writeUTF("True");
	 		            	String Server = in.readUTF();
	 		            	String Sender = in.readUTF();
	 		            	String Command = in.readUTF();
	 		            	main.debug("Incoming Command: ");
	 		            	main.debug("Server: "+Server);
	 		            	main.debug("Sender: "+Sender);
	 		            	main.debug("Command: "+Command);
	 		        		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	 		    			String LogString = "["+timeStamp+"][Received] Player: ["+Sender+"] Sent Command ["+Command+"] from Server: "+Server+" At: "+server.getRemoteSocketAddress();
	 		            	main.addCommand(Command);
	 		            	main.addLog(LogString);
	             		} else {
	             			out.writeUTF("OFF");
	             		}
	             	} else if (IncomingType.equalsIgnoreCase("Broadcast")) {
	             		if(main.FeatureToggles[2] == true) {
	                     	out.writeUTF("True");
	     	            	String Server = in.readUTF();
	     	            	String Sender = in.readUTF();
	     	            	String Broadcast = in.readUTF();
	 		            	main.debug("Incoming Broadcast: ");
	 		            	main.debug("Server: "+Server);
	 		            	main.debug("Sender: "+Sender);
	 		            	main.debug("Command: "+Broadcast);
	     	        		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	     	    			String LogString = "["+timeStamp+"][Received] Player: ["+Sender+"] Sent Broadcast ["+Broadcast+"] from Server: "+Server+" At: "+server.getRemoteSocketAddress();
	     	    			Bukkit.broadcastMessage(main.PM[18] + ChatColor.translateAlternateColorCodes('&', Broadcast));
	     	    			main.addLog(LogString);
	             		} else {
	             			out.writeUTF("OFF");
	             		}
	             	} else if (IncomingType.equalsIgnoreCase("Test")) {
	             		main.debug("[Server] Established Connection was Status Check. Responding Online and Resetting Socket");
	             		out.writeUTF("Online");
	             	}
	             } else {
	             	main.debug("Invalid Passkey Recieved from: "+server.getRemoteSocketAddress());
	                 out.writeUTF("Invalid PassKey"); //Throws invalid passkey error 
	             }
	             server.close();//Closes Current Connection for new Connections
	          } catch (SocketTimeoutException s) {
	             System.out.println( "[Remote Commands][Server][Error] Socket timed out!");//Error Status Display
	             break;
	          } catch (IOException e) {
	             e.printStackTrace();//Error message Display
	             break;
	          }
	 	   }//close while
	    }//Close Run()
	   public boolean CheckKey(String PassKey) { 
		   boolean validKey = Decrypt.isKey(PassKey); //Calls Decryption Method, Decrypts Key, returns if correct Passkey
		   if (validKey == true) { //Checks boolean
			   return true; //returns message to be sent
		   } else {
			   return  false; //returns blank message
		   }
		   
	   }
}//Close Class