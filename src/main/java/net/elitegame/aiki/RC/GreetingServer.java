package net.elitegame.aiki.RC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;


public class GreetingServer extends Thread{
   private ServerSocket serverSocket;
   Encryption Decrypt = new Encryption(); //Calls Encryption Class
   String storedMessage = " "; // String established for Storing Message
   
   
   public GreetingServer(int port) throws IOException { //method for new server socket creation
      serverSocket = new ServerSocket(port); //defines new socket
      main.debug("[Remote Commands][Debug] Listener Listening on Port: "+port);
   }
   public void run() { //runs Server Listener
	   while(true) {
         try {
            Socket server = serverSocket.accept();//Establishes Socket
            main.debug("[Remote Commands][Debug][Server] Connection Established by [Remote Client: " + server.getRemoteSocketAddress()+ "]");//Status Display
            DataInputStream in = new DataInputStream(server.getInputStream()); //Declares Input Stream
            DataOutputStream out = new DataOutputStream(server.getOutputStream());//Declares output Stream
            
            String Passkey = in.readUTF();
            if (CheckKey(Passkey) == true) {
            	out.writeUTF("True");
            	String Server = in.readUTF();
            	String Sender = in.readUTF();
            	String Command = in.readUTF();
            	main.debug("[Remote Commands][Debug][Server] Server: "+Server);
            	main.debug("[Remote Commands][Debug][Server] Sender: "+Sender);
            	main.debug("[Remote Commands][Debug][Server] Command: "+Command);
        		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    			String LogString = "["+timeStamp+"][Received] Player: ["+Sender+"] Sent ["+Command+"] from Server: "+Server+" At: "+server.getRemoteSocketAddress();
            	main.addCommand(Command , LogString);
            } else {
            	main.debug("[Remote Commands][Debug][Server] Invalid Passkey Recieved from: "+server.getRemoteSocketAddress());
                out.writeUTF("Invalid PassKey"); //Throws invalid passkey error 
        		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()); 
                main.logString("["+timeStamp+"][Warning] "+server.getRemoteSocketAddress()+" Attempted to Connect with an Invalid Passkey");
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