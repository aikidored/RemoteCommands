package net.elitegame.aiki.RC;

import java.util.Random;

public class Encryption {
	//##########################################
	//####    This Class
	//####     Encrypts 6 Key ints into a Passkey
	//####     Decrypts Passkey Received in Command
	//##########################################
	private String EncryptedPassKey;
	
	public String getEncryptedKey(int PassKey) { //Controls Encryption
		int EncryptionLevel = getEncryptionLevel();
		int EncryptionDepth = getEncryptionDepth();
		int finalKey = EncryptKey(PassKey, EncryptionDepth, EncryptionLevel);
		EncryptedPassKey = EncryptionDepth + "/" + EncryptionLevel + "/" + finalKey;
		main.debug(EncryptedPassKey);
		return EncryptedPassKey;
		
	}
	private int getEncryptionLevel() { //Generates Random Number For Encryption level
		Random rand = new Random();
		int n = rand.nextInt(100);
		n += 1;
		return n;		
	}
	private int getEncryptionDepth() { //Generates Encryption Depth
		Random rand = new Random();
		int n = rand.nextInt(10);
		n += 1;
		return n;		
	}
	private int EncryptKey(int PassKey, int eDepth, int eLevel) { //Generates Encrypted PassKey
		PassKey *= eLevel;
		PassKey *= eDepth;
		return PassKey;				
	}
	public boolean isKey(String EncryptedKey) { //Compares EncryptedKey to Passkey
		boolean isKey = Decryption(EncryptedKey);
		return isKey;
	}
	
	
	private boolean Decryption(String EncryptedKey) { //Decrypts Key and returns if it is a valid Key
		int loc = EncryptedKey.lastIndexOf('/');
		int loc2 = EncryptedKey.indexOf('/');
	    int PassKey = Integer.parseInt(EncryptedKey.substring(loc+1, EncryptedKey.length()));
	    int eDepth = Integer.parseInt(EncryptedKey.substring(0, loc2));
	    int eLevel = Integer.parseInt(EncryptedKey.substring(loc2+1, loc));
 	    int decryptedKey = PassKey / eLevel;
	    decryptedKey /= eDepth;
	    if (decryptedKey == main.Passkey) {
		   return true;
	    } else {
		   return false; 
	    }	   	   
    }
	

}
