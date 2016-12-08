package global;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.crypto.ShortBufferException;
import javax.xml.bind.DatatypeConverter;

import requestsParser.Request;
import logic.ServerSignatureGen;

public class User {
	private static int usercount = 0;
	private String name;
	private InetAddress addr;
	private int recevingPort;
	private HashMap<Integer, Request> history = new HashMap<Integer, Request>();
	private boolean isRegistered = false;
	private int isAvaliable = 0;
	private boolean toBeRecovered = false;
	private ArrayList<String> allowedListofUser = new ArrayList<String>();
	private byte[] userFingerPrint; 
	private byte[] userPubKey;
	private byte[] userPriKey;

	
	public User(InetAddress address, int recPortNum, byte[] key){
		usercount++;
		name = new String("User" + usercount);
		addr = address;
		recevingPort = recPortNum;
		ServerSignatureGen sigGen = new ServerSignatureGen();
		try {
			sigGen.init(key);
			userPubKey = sigGen.getPublicKey();
			userPriKey = sigGen.getPrivateKey();
			userFingerPrint = sigGen.getSecret();
			
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidParameterSpecException
				| InvalidAlgorithmParameterException | InvalidKeySpecException | IllegalStateException
				| ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public User(String username, InetAddress address, int recPortNum, byte[] key){
		usercount++;
		name = username;
		addr = address;
		recevingPort = recPortNum;
		ServerSignatureGen sigGen = new ServerSignatureGen();
		try {
			sigGen.init(key);
			userPubKey = sigGen.getPublicKey();
			userPriKey = sigGen.getPrivateKey();
			userFingerPrint = sigGen.getSecret();
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidParameterSpecException
				| InvalidAlgorithmParameterException | InvalidKeySpecException | IllegalStateException
				| ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public User(String username, InetAddress address, int recPortNumber, Integer avaliability, byte[] secretKey, ArrayList<String> friendsList){
		usercount++;
		name = username;
		addr = address;
		recevingPort = recPortNumber;
		if(avaliability == 1){
			isAvaliable = 1;
		}
		else if(avaliability == -1){
			isAvaliable = -1;
		}
		else{
			isAvaliable = 0;
			isRegistered = false;
		}
		userFingerPrint = secretKey;
		allowedListofUser = friendsList;
		toBeRecovered = true;
	}
	
	public void recoverHistoryRequest(HashMap<Integer, Request> his){
		if(this.toBeRecovered == true){
			history = his;
		}
		toBeRecovered = false;
	}
	
	public String returnStringListOfUser(){
		return Arrays.toString(allowedListofUser.toArray());
	}
	
	public ArrayList<String> getAllowedList(){
		return this.allowedListofUser;
	}
	
	public boolean logHistoryRequest(Request req, Integer index){
		if(history.containsKey(index)){
			return false;
		}
		else{
			history.put(index, req);
			return true;
		}
	}
	
	public Request retrieveHistoryItem(Integer index){
		return history.get(index);
	}
	
	public boolean hasIndexLogged(Integer index){
		if(history.containsKey(index)){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public byte[] getPublicKey(){
		return userPubKey;
	}
	
	protected byte[] getPrivateKey(){
		return userPriKey;
	}
	
	public byte[] getSecret(){
		return userFingerPrint;
	}
	
	public void setListOfAllowedUsers(ArrayList<String> list){
		this.allowedListofUser = list;
	}
	
	public int getHistorySize(){
		return history.size();
	}
	
	public InetAddress getAddr(){
		return addr;
	}
	
	public int getRecevingPort(){
		return recevingPort;
	}	
	
	public String getName(){
		return name;
	}

	
	public void setAvaliability(int ava){
		isAvaliable = ava;
	}
	
	public int returnAvaliability(){
		return isAvaliable;
	}
	
	public boolean isRegistered(){
		return isRegistered;
	}
	
	
	
	public void register() throws HasRegisteredException{
		if(isRegistered == true){
			throw new HasRegisteredException();
		}else{
			isRegistered = true;
		}
	}
	
	public void makeFriend(String userSig){
		allowedListofUser.add(userSig);
	}
	
	public void makeFriend(User user){
		allowedListofUser.add(DatatypeConverter.printHexBinary(user.getSecret()));
	}
	
	public void setReceivePort(int port){
		recevingPort = port;
	}
	
	public boolean isFriend(String userSignature){
		for (String u: allowedListofUser){
			if(u.equals(userSignature)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isFriend(User tar){
		for (String u: allowedListofUser){
			if(u.equals(DatatypeConverter.printHexBinary(tar.getSecret()))){
				return true;
			}
		}
		return false;
	}
	
	

	

	
	

}
