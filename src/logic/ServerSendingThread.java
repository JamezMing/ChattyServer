package logic;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import global.User;
import java.io.IOException;


public class ServerSendingThread extends Thread{
	private DatagramSocket dataSendPort;
	private User targetUser;
	private String message;
	
	public ServerSendingThread(DatagramSocket port, User targetUser, String message){
		dataSendPort = port;
		this.targetUser = targetUser;
		this.message = message; 
	}
	
	
	public void run(){
        DatagramPacket sendPac = new DatagramPacket(message.getBytes(), message.length(), targetUser.getAddr(), targetUser.getRecevingPort());
        try {
			dataSendPort.send(sendPac);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
