package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
				System.out.println("Preparing Reader...");
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
		
		System.out.println("The client has closed its connection");
	}
		
}
