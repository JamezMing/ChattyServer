package ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ServerLoginUIController extends AnchorPane {
	@FXML TextArea recPortField;
	@FXML TextArea sendPortField;
	@FXML TextArea nextServerAddressField;
	@FXML TextArea nextServerPortField;
	@FXML Button startButton;
	@FXML ImageView logoContainer;
	
	public ServerLoginUIController(){
	}
	
	@FXML public void onClickStartButton() throws IOException{
		try{
			Integer recPort = new Integer(recPortField.getText());
			Integer sendPort = new Integer(sendPortField.getText());
			InetAddress nextServerAddress = InetAddress.getLocalHost();
			Integer nextServerRecPort = recPort;
			if(nextServerAddressField.getText()!=null && nextServerPortField.getText()!=null){
				nextServerAddress = InetAddress.getByName(nextServerAddressField.getText());
				nextServerRecPort = new Integer(nextServerPortField.getText());
			}else{
				System.out.println("The next server fields are empty");

			}
			if(recPort >= 65536 || recPort < 0 ||sendPort >= 65536 || sendPort < 0 ){
				throw new NumberFormatException();
			}
			String myAddr = InetAddress.getLocalHost().getHostAddress();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerMainUI.fxml"));
			ServerMainUIController con = new ServerMainUIController(recPort, sendPort, nextServerAddress, nextServerRecPort);
			fxmlLoader.setController(con);
			Stage preStage = (Stage) startButton.getScene().getWindow();
			preStage.close();
			System.out.println("Start Button Clicked");
			AnchorPane rootLayout = (AnchorPane) fxmlLoader.load();
			Stage newSta = new Stage();
			newSta.setTitle("Chatty Server");
			newSta.setScene(new Scene(rootLayout));
			newSta.show();
		}catch(NumberFormatException e){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Input Error");
			alert.setHeaderText(null);
			alert.setContentText("Please input a valid port number!");
			alert.showAndWait();
		}catch(UnknownHostException f){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Input Error");
			alert.setHeaderText(null);
			alert.setContentText("Please input a valid IP address!");
			alert.showAndWait();
		}
		
	}

}
