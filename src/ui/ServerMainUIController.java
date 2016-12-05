package ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import global.HasRegisteredException;
import global.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import logic.ServerManager;

public class ServerMainUIController extends AnchorPane{
	@FXML TextField messageInputField;
	@FXML Button sendButton;
	@FXML VBox userListBox;
	@FXML TextArea messageDisplayArea;
	private ServerManager myManager;
	private ArrayList<UserIconController> userList = new ArrayList<UserIconController>();
	private ArrayList<String> messageHistory = new ArrayList<String>();
	
	public ServerMainUIController(Integer recPort, Integer sendPort, InetAddress nextServerAddr, Integer nextServerRecPort){
		myManager = new ServerManager(sendPort,recPort, nextServerAddr, nextServerRecPort, this);
		System.out.println("Server Manager Contructed");
		myManager.init();
	}
	
	public void setUserIconState(User u, boolean state){
		for (UserIconController user:userList){
			if(user.isUserIcon(u)){
				user.setAvaliablity(state);
			}
		}
	}
	
	public void addUsericonToList(User user){
		UserIconController icon = new UserIconController(myManager);
		if(userList.size() >= 5){
			return;
		}
		userList.add(icon);
		icon.initIcon(user.getName(), user.getAddr(), user.getRecevingPort());
		userListBox.getChildren().add(icon);
	}
	
	public void displayMessage(String msg){
		messageHistory.add(msg);
		refreshDisplay();
	}
	
	public void refreshDisplay(){
		messageDisplayArea.clear();
		for(String s:messageHistory){
			messageDisplayArea.appendText(s);
			messageDisplayArea.appendText("\n");
		}
	}
	
	@FXML public void inSendBtnClicked() throws UnknownHostException, HasRegisteredException{
		String command = messageInputField.getText();
		messageInputField.clear();
		displayMessage(command);
	}

}
