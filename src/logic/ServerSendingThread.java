package logic;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import global.ServerLogger;
import global.User;
import java.io.IOException;


public class ServerSendingThread extends Thread{
	private DatagramSocket dataSendPort;
	private String message;
	private Integer recPort;
	private InetAddress recAddr;
	
	public ServerSendingThread(DatagramSocket port, User targetUser, String message){
		dataSendPort = port;
		recPort = targetUser.getRecevingPort();
		recAddr = targetUser.getAddr();
		this.message = message;
	}
	
	public ServerSendingThread(DatagramSocket port, InetAddress recAddr, Integer recPort, String message){
		dataSendPort = port;
		this.recPort = recPort;
		this.recAddr = recAddr;
		this.message = message;
	}
	
	
	
	public void run(){
		ServerLogger.log(message);
        DatagramPacket sendPac = new DatagramPacket(message.getBytes(), message.length(), recAddr, recPort);
        try {
			dataSendPort.send(sendPac);
			ServerLogger.log("Message sent: " + message);
			ServerLogger.log("Message Destination: " + recAddr.getHostAddress() + " Destination Port: " + recPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
