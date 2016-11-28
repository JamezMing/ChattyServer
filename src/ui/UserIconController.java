package ui;

import java.io.IOException;
import java.net.InetAddress;

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
