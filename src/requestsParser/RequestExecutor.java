package requestsParser;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;
import global.GlobalVariables;
import global.HasRegisteredException;
import global.User;
import logic.ServerManager;
import logic.ServerSendingThread;

public class RequestExecutor extends Thread{
	private String rawMsg;
	private Request req; 
	private String[] fields; 
	private ServerManager myManager;
	
		
	
	public RequestExecutor(Request request, ServerManager manager){
		req = request;
		myManager = manager;
		rawMsg = req.getMessage();
	}
	
	public String[] returnArgs(){
		return fields;
	}
	

	
	public void processRequest() throws Exception{
		String type = new String();		
		String[] args = fields;
		type = args[0];
		Integer index;
		switch(type){
		case GlobalVariables.REGISTER_ACTION:
			index = new Integer(args[1]);
			byte[] cliPubKey = new BigInteger(args[5],16).toByteArray();
			User newUser = new User(args[2], req.getSenderAddr() , (int)new Integer(args[4]), cliPubKey);
			if(newUser.logHistoryRequest(req, index) == false){
				String responce = new String(GlobalVariables.REGISTER_DENIED + GlobalVariables.delimiter + index.toString());
				String msgDisp = new String(newUser.getName() + "( " + newUser.getAddr().getHostAddress() + " )" + " requested for a registration but encountered a message index error.");
				myManager.displayIncomingMessage(msgDisp);
				new ServerSendingThread(myManager.getServerSendingPort(), newUser, responce).start();
				break;
			}
			if(!myManager.hasUser(req.getSenderAddr(),args[2]) && (myManager.getNumOfUser() < 5)){	
				myManager.registerClient(newUser);
				String msgDisp = new String(newUser.getName() + "( " + newUser.getAddr().getHostAddress() + " )" + " has successfully registerrd to this server");
				String msgDisp2 = new String("There are " + myManager.getNumOfUser() + " users registered to this server currently");
				myManager.displayIncomingMessage(msgDisp);
				myManager.displayIncomingMessage(msgDisp2);
				String responce = new String(GlobalVariables.REGISTER_SUCCESS + GlobalVariables.delimiter + index.toString() + GlobalVariables.delimiter + DatatypeConverter.printHexBinary(newUser.getPublicKey())); 
				new ServerSendingThread(myManager.getServerSendingPort(), newUser, responce).start();
				
			}
			else if((myManager.getNumOfUser() >= 5)){
				String responce = new String(GlobalVariables.REGISTER_DENIED + GlobalVariables.delimiter + index.toString() + GlobalVariables.delimiter + 
						myManager.getNextServerAddr().toString() + GlobalVariables.delimiter + new Integer(myManager.getNextServerPort()).toString());
				String msgDisp = new String(newUser.getName() + "( " + newUser.getAddr().getHostAddress() + " )" + " wants to register on this server but the server is full");
				String msgDisp2 = new String("The client is refered to " + myManager.getNextServerAddr().toString());
				myManager.displayIncomingMessage(msgDisp);
				myManager.displayIncomingMessage(msgDisp2);
				new ServerSendingThread(myManager.getServerSendingPort(), newUser, responce).start();
			}
			else{
				String responce = new String(GlobalVariables.REGISTER_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), newUser, responce).start();
			}
			break;
		case GlobalVariables.PUBLISH_ACTION:
			index = new Integer(args[1]);
			int tar = myManager.getUserInList(args[2], req.getSenderAddr());
			int key_tar = myManager.getUserIndexByKey(new BigInteger(args[6], 16).toByteArray());
			System.out.println(new Integer(tar));
			System.out.println(new Integer(key_tar));
			if(key_tar != tar){
				throw new Exception("Authentification error");
			}
			if (myManager.getUserList().isEmpty() || tar == -1){
				//TODO: need to consturct a user not registered error in order to make it work
				throw new Exception("The message is not sent from a validated user");
			}
			if(myManager.getUserList().get(key_tar).logHistoryRequest(req, index) == false){
				String responce = new String(GlobalVariables.PUBLISH_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar), responce).start();
				break;
			}

			int status = -1;
			if(args[4].equalsIgnoreCase("on")){
				status = 1;
			}
			else if(args[4].equalsIgnoreCase("off")){
				status = 0;
			}
			else{
				String responce = new String(GlobalVariables.PUBLISH_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar), responce).start();
			}
			
			if(tar > -1){
				boolean isOn;
				switch(status){
				case 1:
					isOn = true;
					break;
				case 0:
					isOn = false;
					break;
				default:
					throw new Exception("The server is in an invalid state");
				}
				myManager.getUserList().get(key_tar).setReceivePort(new Integer(args[3]));
				myManager.getUserList().get(key_tar).setAvaliability(isOn);
				String usernames = args[5];
				String[] userNameList = usernames.split(" ");
				for (String n:userNameList){
					User u = myManager.findUserByName_Single(n);
					if(u!=null){
						myManager.getUserList().get(key_tar).makeFriend(u);
					}
				}
				String msgNew  = rawMsg.replace(GlobalVariables.PUBLISH_ACTION, GlobalVariables.PUBLISH_SUCCESS);
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(key_tar), msgNew).start();
			}
			else{
				String responce = new String(GlobalVariables.PUBLISH_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(key_tar), responce).start();
			}
			break;
		case GlobalVariables.IMFORMATION_REQUEST_ACTION:
			index = new Integer(args[1]);
			int tar_2 = myManager.getUserInList(args[2], req.getSenderAddr());
			int tar_key2 = myManager.getUserIndexByKey(new BigInteger(args[3], 16).toByteArray());
			System.out.println(tar_2);
			System.out.println(tar_key2);
			if (myManager.getUserList().isEmpty() || tar_key2 == -1){
				throw new Exception("The message is not sent from a validated user");
			}
			if(myManager.getUserList().get(tar_key2).hasIndexLogged(index) == false){
				String responce = new String(GlobalVariables.INFORMATION_REQUEST_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key2), responce).start();
				break;
			}
			Request retrieved = myManager.getUserList().get(tar_key2).retrieveHistoryItem(index);
			String msgNew = retrieved.getMessage();
			new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key2), msgNew).start();
			break;
		
		case GlobalVariables.USER_INFO_REQUEST_ACTION:
			index = new Integer(args[1]);
			String tarName = args[2];
			int tar_key3 = myManager.getUserIndexByKey(new BigInteger(args[3], 16).toByteArray());
			if (myManager.getUserList().isEmpty() || tar_key3 == -1){
				throw new Exception("The message is not sent from a validated user");
			}
			if(myManager.getUserList().get(tar_key3).logHistoryRequest(req, index) == false){
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key3), responce).start();
				break;
			}
			User tarUser = myManager.findUserByName_Single(tarName);
			if(tarUser == null){
				String responce = new String(GlobalVariables.REFER_ACTION + GlobalVariables.delimiter + index.toString() + GlobalVariables.delimiter + myManager.getNextServerAddr().toString()
						+ GlobalVariables.delimiter + myManager.getNextServerPort());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key3), responce).start();
				break;
			}
			else if(!tarUser.isFriend(myManager.getUserList().get(tar_key3))){
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_DENIED + GlobalVariables.delimiter + tarName);
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key3), responce).start();
				break;
			}
			else if(tarUser.returnAvaliability() == false){
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_SUCCESS + GlobalVariables.delimiter  
						+ index.toString() + GlobalVariables.delimiter + tarName + GlobalVariables.delimiter + "off");
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key3), responce).start();
				break;
			}
			else{
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_SUCCESS + GlobalVariables.delimiter  
						+ index.toString() + GlobalVariables.delimiter + tarName + GlobalVariables.delimiter + tarUser.getRecevingPort()
						+ GlobalVariables.delimiter + tarUser.getAddr().toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key3), responce).start();
			}
			break;

				//TODO
		}
	}
	

	
	public void run(){
		System.out.println("The Request has been received");
		System.out.println(rawMsg);
		fields = rawMsg.split(GlobalVariables.token);
		if(fields.length == 1){
			try {
				throw new Exception("Bad Input, no delimiter identified");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			processRequest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
