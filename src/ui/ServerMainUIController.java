package ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
		myManager = new ServerManager(recPort, sendPort, nextServerAddr, nextServerRecPort, this);
		myManager.init();
	}
	
	@FXML public void refreshListBox(){
		userListBox.getChildren().clear();
		userListBox.getChildren().addAll(userList);
	}
	
	public void addUserToList(UserIconController user){
		userList.add(user);
		refreshListBox();
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
	
	@FXML public void inSendBtnClicked(){
		String command = messageInputField.getText();
		messageInputField.clear();
		displayMessage(command);
	}

}
