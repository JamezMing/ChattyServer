package ui;

import java.io.IOException;
import java.net.InetAddress;

import global.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.ServerManager;

public class UserIconController extends VBox {
	@FXML Label addressField;
	@FXML Label recevingPortField;
	@FXML Label nameField;
	@FXML CheckBox isAvaliableField;
	ServerManager myManager;
	

	
    public UserIconController(ServerManager manager) {
    	myManager = manager;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userIcon.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }       
    }
    
    public boolean isUserIcon(User u){
    	if(u.getName().equals(nameField.getText()) && u.getAddr().getHostAddress().equals(addressField.getText())){
    		return true;
    	}
    	return false;
    }
    
    public boolean isUserIcon(InetAddress addr, Integer port){
    	if(addr.getHostAddress().equals(addressField.getText()) && port.equals(new Integer(recevingPortField.getText()))){
    		return true;
    	}
    	return false;
    }
    
    public boolean isUserIcon(InetAddress addr, String name){
    	if(addr.getHostAddress().equals(addressField.getText()) && name.equals(nameField.getText())){
    		return true;
    	}
    	return false;
    }
    
    
    public void initIcon(String name, InetAddress addr, Integer port){
    	addressField.setText(addr.getHostAddress());
    	recevingPortField.setText(port.toString());
    	nameField.setText(name);
    	isAvaliableField.setIndeterminate(true);
    }
    
    public void initIcon(String name, InetAddress addr, Integer port, boolean isAvaliable){
    	addressField.setText(addr.getHostAddress());
    	recevingPortField.setText(port.toString());
    	nameField.setText(name);
    	isAvaliableField.setSelected(isAvaliable);
    }
    
    public void setAvaliablity(boolean isAvaliable){
    	isAvaliableField.setSelected(isAvaliable);
    }
    
    public String getAddress(){
    	return addressField.getText();
    }
    
    public Integer getPort(){
    	return new Integer(recevingPortField.getText());
    }
    
    public String getName(){
    	return nameField.getText();
    }
    
    public boolean getAvaliablity(){
    	return isAvaliableField.isSelected();
    }
    
    protected void whenIconClicked(){
    	//TODO
    }

}
