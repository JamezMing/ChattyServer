package ui;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import global.HasRegisteredException;
import global.ServerLogger;
import global.ServerUserDataBaseManager;
import global.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
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
	@FXML ScrollBar scrollBar;
	private ServerManager myManager;
	private ArrayList<UserIconController> userList = new ArrayList<UserIconController>();
	private ArrayList<String> messageHistory = new ArrayList<String>();
	
	
	public ServerMainUIController(Integer recPort, Integer sendPort, InetAddress nextServerAddr, Integer nextServerRecPort){
		myManager = new ServerManager(sendPort,recPort, nextServerAddr, nextServerRecPort, this);
		ServerLogger.log("Server Manager Contructed");
		myManager.init();
	}
	
	
	public void recoverManagerData() throws UnknownHostException{
		CopyOnWriteArrayList<User> uList = myManager.recoverFromDatabase();
		for(User u: uList){
			addUsericonToList(u);
		}
		
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
				ServerLogger.log("User Icon Found");
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
		if(user.returnAvaliability() == 1){
			icon.initIcon(user.getName(), user.getAddr(), user.getRecevingPort(), true);
		}
		else if(user.returnAvaliability() == -1){
			icon.initIcon(user.getName(), user.getAddr(), user.getRecevingPort(), false);
		}
		else{
			icon.initIcon(user.getName(), user.getAddr(), user.getRecevingPort());
		}
		if(userList.size() >= 5){
			return;
		}
		userList.add(icon);
		userListBox.getChildren().add(icon);
		ServerLogger.log("New user icon added to the list");
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
