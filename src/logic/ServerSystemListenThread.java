package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import global.ServerLogger;

public class ServerSystemListenThread extends Thread{
	ServerManager myManager; 
	public ServerSystemListenThread(ServerManager father){
		myManager = father;
	}
	
	public void run(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
		while(true){
			String msg;
			while ((msg = reader.readLine()) == null) {
				ServerLogger.log("Preparing Reader...");
				Thread.sleep(100);
			}
	        if(msg.equals("EXIT")){
	        	myManager.closeDown();
	        	break;
	        }
	        myManager.broadCast(msg);
	        
		}}catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ServerLogger.log("The client has closed its connection");
	}
		
}
