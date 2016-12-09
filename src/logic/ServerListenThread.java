package logic;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import global.ServerLogger;
import requestsParser.*;

public class ServerListenThread extends Thread{
	private DatagramSocket recSoc;
	private ServerManager myManager;
	boolean isRunning = true; 
	
	public ServerListenThread(DatagramSocket port, ServerManager father){
		recSoc = port;
		myManager = father;
	}
	
	public void run(){
		try {
			byte[] buffer = new byte[1024];
			DatagramPacket recPac = new DatagramPacket(buffer, 1024);
			while(isRunning){
				recSoc.receive(recPac);
				String str_receive = new String(recPac.getData(),0,recPac.getLength());
				Request req = new Request(str_receive, recPac.getAddress(), recPac.getPort());
				myManager.processRequest(req);
				
				//TODO
				myManager.broadCast(str_receive,recPac.getAddress());
				ServerLogger.log(str_receive);
				recPac.setLength(1024);
			}
			recSoc.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}