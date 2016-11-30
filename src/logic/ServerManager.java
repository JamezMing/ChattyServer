package logic;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import global.HasRegisteredException;
import global.ServerMessageDataBaseManager;
import global.ServerUserDataBaseManager;
import global.User;
import requestsParser.Request;
import requestsParser.RequestExecutor;
import ui.ServerMainUIController;
import logic.ServerListenThread;

public class ServerManager {
	private volatile CopyOnWriteArrayList<User> userList = new CopyOnWriteArrayList<User>();
	private global.GlobalVariables GV;
	private InetAddress nextServerAddr;
	private int nextServerPort;
	private ServerMessageDataBaseManager messageDataBase;
	private ServerUserDataBaseManager userDataBase;
	private boolean hasNextServer = false;
	private ServerMainUIController myController;
	private DatagramSocket serverListenSoc;
	private DatagramSocket serverSendingSoc;

	

	public ServerManager(int sendingPort, int recevingPort, InetAddress addr, int port, ServerMainUIController controller){
		try {
			serverListenSoc = new DatagramSocket(recevingPort);
			serverSendingSoc = new DatagramSocket(sendingPort);
			nextServerAddr = addr;
			nextServerPort = port;
			hasNextServer = true;
			myController = controller;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ServerManager(int sendingPort, int recevingPort, ServerMainUIController controller) throws UnknownHostException{
		try {
			serverListenSoc = new DatagramSocket(recevingPort);
			serverSendingSoc = new DatagramSocket(sendingPort);
			nextServerAddr = InetAddress.getLocalHost();
			nextServerPort = recevingPort;;
			myController = controller;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public int getUserIndexByKey(byte[] key){
		for (User u:userList){
			if(Arrays.equals(u.getSecret(), key)){
				System.out.println("User Key Found");
				return userList.indexOf(u); 
			}
		}
		return -1;
	}

	public void init(){
		ServerListenThread lisTh = new ServerListenThread(serverListenSoc, this);
		lisTh.start();
		ServerSystemListenThread sysTh = new ServerSystemListenThread(this);
		sysTh.start();
		System.out.println("Server Manager initialized");
	}
	
	
	public void processRequest(Request req){
		new RequestExecutor(req, this).start();
	}
	
	public void displayIncomingMessage(String message){
		myController.displayMessage(message);
	}
	
	public void broadCast(String message) throws IOException{
		for(User u:userList){
			DatagramPacket sendPac = new DatagramPacket(message.getBytes(), message.length(), u.getAddr(), u.getRecevingPort());
			serverSendingSoc.send(sendPac);
		}
	}
	
	public void broadCast(String message, InetAddress except_addr) throws IOException{
		for(User u:userList){
			if(!u.getAddr().equals(except_addr)){
				DatagramPacket sendPac = new DatagramPacket(message.getBytes(), message.length(), u.getAddr(), u.getRecevingPort());
				serverSendingSoc.send(sendPac);
			}
		}
	}
	
	
	public synchronized void authenticateUser(User self, User[] users){
		for (User u:users){
			u.makeFriend(self);
		}
	}
	
	public synchronized int getNumOfUser(){
		return userList.size();
	}
	
	public synchronized InetAddress getNextServerAddr(){
		return nextServerAddr;
	}
	
	public synchronized int getNextServerPort(){
		return nextServerPort;
	}
	
	
	public boolean getHasNextServer() throws UnknownHostException{
		if(nextServerAddr.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())){
			return false;
		}
		else{
			return true;	
		}
	}
	
	public void registerNextServer(InetAddress addr, Integer recPort) throws UnknownHostException{
		nextServerAddr = addr;
		nextServerPort = recPort;
	}
	
	

	public synchronized int registerClient(User user) throws HasRegisteredException{
		
		if(userList.size() >= 5){
			return 1;
		}
		user.register();
		userList.add(user);
		return 0;
	}
	
	public synchronized User[] findListOfUsers(String[] names){
		User[] list = new User[5];
		int resCount = 0;
		ArrayList<String> namelist = new ArrayList<String>(Arrays.asList(names));
		for (String name:namelist){
			for (User user: userList){
				if(user.getName().equals(name)){
					list[resCount] = user;
					resCount++;
					namelist.remove(name);
					continue;
				}
			}
		}
		return list;
	}
	
	//The function has 2 return states, 0 for register successful, 1 for full server, 2 for user has already registered
	public synchronized int registerClient(String name, String address, int portNum, byte[] key) throws UnknownHostException, HasRegisteredException{ 
		if(hasUser(InetAddress.getByName(address), name)){
			return 2;
		}
		if(userList.size() >= 5){
			return 1;
		}
		User newUser = new User(name, InetAddress.getByName(address), portNum, key);
		newUser.register(); 
		userList.add(newUser);
		return 0;
	}
	
	public synchronized int getUserInList(String name, InetAddress addr){
		for(User u:userList){
			if(u.getName().equals(name) && u.getAddr().equals(addr)){
				System.out.println("User Found");
				return userList.indexOf(u);
			}
		}
		return -1;
	}
	
	public DatagramSocket getServerSendingPort(){
		return this.serverSendingSoc;
	}
	
	public DatagramSocket getServerListeningPort(){
		return this.serverListenSoc;
	}
	
	public synchronized CopyOnWriteArrayList<User> getUserList(){
		return userList;
	}
	
	public User[] findUserByName(String name){
		User[] results = new User[5];
		int resCount = 0;
		for(User u:userList){
			if(u.getName().equals(name)){
				results[resCount] = u;
				resCount++;
			}
		}
		return results;
	}
	

	
	public User findUserByName_Single(String name){
		for(User u:userList){
			if(u.getName().equals(name)){
				return u;
			}
		}
		return null;
	}
	public synchronized boolean hasUser(InetAddress userAdd){
		boolean hasUserExist = false;
		for (User regUser: userList){
			if((regUser.getAddr().equals(userAdd))){
				hasUserExist = true;
				break;
			}
		}
		return hasUserExist;
	}

	public synchronized boolean hasUser(InetAddress userAdd, String userName){
		boolean hasUserExist = false;
		for (User regUser: userList){
			if((regUser.getAddr().equals(userAdd))&&(regUser.getName().equalsIgnoreCase(userName))){
				hasUserExist = true;
				break;
			}
		}
		return hasUserExist;
	}
	
	
	protected void closeDown(){
		serverListenSoc.close();
		serverSendingSoc.close();
		System.out.println("All Sockets are closed");
	}
	
	

}
