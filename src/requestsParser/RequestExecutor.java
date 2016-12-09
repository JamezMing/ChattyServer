package requestsParser;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;
import global.GlobalVariables;
import global.HasRegisteredException;
import global.ServerLogger;
import global.ServerUserDataBaseManager;
import global.User;
import javafx.application.Platform;
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
				String responce = new String(GlobalVariables.REGISTER_FULL + GlobalVariables.delimiter + index.toString() + GlobalVariables.delimiter + 
						myManager.getNextServerAddr().getHostAddress() + GlobalVariables.delimiter + new Integer(myManager.getNextServerPort()).toString());
				String msgDisp = new String(newUser.getName() + "( " + newUser.getAddr().getHostAddress() + " )" + " wants to register on this server but the server is full");
				String msgDisp2 = new String("The client is refered to " + myManager.getNextServerAddr().getHostAddress());
				myManager.displayIncomingMessage(msgDisp);
				myManager.displayIncomingMessage(msgDisp2);
				new ServerSendingThread(myManager.getServerSendingPort(), newUser, responce).start();

			}
			else{
				String responce = new String(GlobalVariables.REGISTER_DENIED + GlobalVariables.delimiter + index.toString() + "error happened in processing");
				myManager.displayIncomingMessage("Error happened while a client wish to register with the server\n");
				new ServerSendingThread(myManager.getServerSendingPort(), newUser, responce).start();
			}
			break;
		case GlobalVariables.PUBLISH_ACTION:
			index = new Integer(args[1]);
			int tar = myManager.getUserInList(args[2], req.getSenderAddr());
			int key_tar = myManager.getUserIndexByKey(new BigInteger(args[6], 16).toByteArray());
			ServerLogger.log("Index Found: " + new Integer(tar).toString());
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

			int status = 0;
			if(args[4].equalsIgnoreCase("on")||args[4].equalsIgnoreCase("true")){
				status = 1;
			}
			else if(args[4].equalsIgnoreCase("off")||args[4].equalsIgnoreCase("false")){
				status = -1;
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
				case -1:
					isOn = false;
					break;
				default:
					throw new Exception("The server is in an invalid state");
				}
				myManager.getUserList().get(key_tar).setReceivePort(new Integer(args[3]));
				myManager.getUserList().get(key_tar).setAvaliability(status);
				ServerUserDataBaseManager.modifyUserStatus(myManager.getUserList().get(key_tar), status);
				ServerUserDataBaseManager.modifyUserAllowList(myManager.getUserList().get(key_tar), args[5]);
				if(status != 0)
					myManager.changeIconState(myManager.getUserList().get(key_tar), isOn);
				String usernames = args[5];
				usernames = usernames.trim();
				usernames = usernames.substring(1, usernames.length()-1);
				String[] userNameList = usernames.split(",");
				myManager.getUserList().get(key_tar).resetFriendList();
				for (String n:userNameList){
						n = n.trim();
						myManager.getUserList().get(key_tar).makeFriend(n);
				}
				String msgDisp = new String(myManager.getUserList().get(key_tar).getName() + " has published a message and changed its user status to "  + 
						String.valueOf(isOn) + "\n List of Users able to access the user is: " + usernames);
				myManager.displayIncomingMessage(msgDisp);
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
			ServerLogger.log("User Index Retrieved: " + tar_key2);
			if (myManager.getUserList().isEmpty() || tar_key2 == -1){
				throw new Exception("The message is not sent from a validated user");
			}
			if(!myManager.getUserList().get(tar_key2).logHistoryRequest(req, index) == false){
				String responce = new String(GlobalVariables.INFORMATION_REQUEST_DENIED + GlobalVariables.delimiter + index.toString());
				new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key2), responce).start();
				break;
			}
			Request retrieved = myManager.getUserList().get(tar_key2).retrieveHistoryItem(index);
			String msgNew = new String(GlobalVariables.IMFORMATION_REQUEST_SUCCESS + GlobalVariables.delimiter + retrieved.getMessage());
			new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key2), msgNew).start();
			break;
			
		case GlobalVariables.REQUEST_ACTION:
			index = new Integer(args[1]);
			String name = args[2];
			int tar_key_in = myManager.getUserIndexByKey(new BigInteger(args[3], 16).toByteArray());
			ServerLogger.log("User Index Retrieved: " + tar_key_in);
			if (myManager.getUserList().isEmpty() || tar_key_in == -1){
				throw new Exception("The message is not sent from a validated user");
			}
			if(myManager.getUserList().get(tar_key_in).logHistoryRequest(req, index) == false){
				ServerLogger.log("A duplicate message number has found");
				break;
			}
			String listOfUser = myManager.getUserList().get(tar_key_in).returnStringListOfUser();
			int stat = myManager.getUserList().get(tar_key_in).returnAvaliability();
			String recPort = String.valueOf(myManager.getUserList().get(tar_key_in).getRecevingPort());
			String resp = new String(GlobalVariables.REQUEST_SUCCESSFUL + GlobalVariables.delimiter + name + GlobalVariables.delimiter +
					recPort + GlobalVariables.delimiter + stat + GlobalVariables.delimiter + listOfUser);
			new ServerSendingThread(myManager.getServerSendingPort(), myManager.getUserList().get(tar_key_in), resp).start();
			break;

			
			

			
		case GlobalVariables.USER_INFO_REQUEST_ACTION:
			String tarName = args[1]; //name of the sender
			String myName = args[2]; //name requesting for
			Integer myPort = new Integer(args[3]);
			InetAddress addr = req.getSenderAddr();
			User tarUser = myManager.findUserByName_Single(tarName);
			if(tarUser == null){
				String responce = new String(GlobalVariables.REFER_ACTION  + GlobalVariables.delimiter + myManager.getNextServerAddr().toString()
						+ GlobalVariables.delimiter + myManager.getNextServerPort());
				new ServerSendingThread(myManager.getServerSendingPort(), req.getSenderAddr(), myPort, responce).start();
				break;
			}
			else if(tarUser.returnAvaliability() == 0){
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_DENIED + GlobalVariables.delimiter + tarName);
				new ServerSendingThread(myManager.getServerSendingPort(), req.getSenderAddr(), myPort, responce).start();
				break;
			}
			else if(!tarUser.isFriend(myName)){
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_DENIED + GlobalVariables.delimiter + tarName);
				new ServerSendingThread(myManager.getServerSendingPort(), req.getSenderAddr(), myPort, responce).start();
				break;
			}
			else if(tarUser.returnAvaliability() == -1){
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_SUCCESS + GlobalVariables.delimiter + tarName + GlobalVariables.delimiter + "off");
				new ServerSendingThread(myManager.getServerSendingPort(), req.getSenderAddr(), myPort, responce).start();
				break;
			}
			else{
				String responce = new String(GlobalVariables.USER_INFO_REQUEST_SUCCESS + GlobalVariables.delimiter  
						+ tarName + GlobalVariables.delimiter + tarUser.getRecevingPort()
						+ GlobalVariables.delimiter + tarUser.getAddr().getHostAddress());
				new ServerSendingThread(myManager.getServerSendingPort(), req.getSenderAddr(), myPort, responce).start();
			}
			
			//Format: info_a%_James%_Molly%_2234 (Molly asking information about James)
			break;

				//TODO
		}
	}
	

	
	public void run(){
		ServerLogger.log("The Request has been received");
		ServerLogger.log(rawMsg);
		fields = rawMsg.split(GlobalVariables.token);
		if(fields.length == 1){
			try {
				throw new Exception("Bad Input, no delimiter identified");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Platform.runLater(new Runnable(){
			public void run(){
				try {
					processRequest();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		
	}
	
}
