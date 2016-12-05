package ui;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import global.HasRegisteredException;
import global.ServerUserDataBaseManager;
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
		File dbFile = new File(ServerUserDataBaseManager.DB_NAME);
		if(dbFile.exists()){
			try {
				myManager.recoverFromDatabase();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		myManager.init();
	}
	
	public void refreshBoxDisplay(){
		userListBox.getChildren().clear();
		for(UserIconController u: userList){
			userListBox.getChildren().add(u);
		}
	}
	
	public void setUserIconState(User u, boolean state){
		for (UserIconController user:userList){
			if(user.isUserIcon(u)){
				user.setAvaliablity(state);
			}
		}
		refreshBoxDisplay();
	}
	
	public void terminateManager(){
		if(myManager != null){
			myManager.closeDown();
		}
	}
	
	public void addUsericonToList(User user){
		UserIconController icon = new UserIconController(myManager);
		icon.initIcon(user.getName(), user.getAddr(), user.getRecevingPort());
		if(userList.size() >= 5){
			return;
		}
		userList.add(icon);
		userListBox.getChildren().add(icon);
		System.out.println("New user icon added to the list");

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
