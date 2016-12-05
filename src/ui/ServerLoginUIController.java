package ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import global.ServerMessageDataBaseManager;
import global.ServerUserDataBaseManager;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

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
			if(!(nextServerAddressField.getText().equals(new String())) && !(nextServerPortField.getText().equals(new String()))){
				nextServerRecPort =  new Integer(nextServerPortField.getText());
				nextServerAddress = InetAddress.getByName(nextServerAddressField.getText());
			}else{
				System.out.println("The next server fields are empty");
				System.out.println("Default Address is " + nextServerAddress.getHostAddress());
				System.out.println("Default port is " + nextServerRecPort);

			}
			if(recPort >= 65536 || recPort < 0 ||sendPort >= 65536 || sendPort < 0 ){
				System.out.println(recPort+ " " + sendPort);
				throw new NumberFormatException();
			}

			String myAddr = InetAddress.getLocalHost().getHostAddress();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerMainUI.fxml"));
			ServerMessageDataBaseManager.init();
			ServerUserDataBaseManager.init();
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
			newSta.setOnCloseRequest(new EventHandler<WindowEvent>(){
				public void handle(WindowEvent we){
					con.terminateManager();
				}
			});
		}catch(NumberFormatException e){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Input Error");
			alert.setHeaderText(null);
			alert.setContentText("Please input a valid port number!");
			alert.showAndWait();
			System.out.println(e);
		}catch(UnknownHostException f){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Input Error");
			alert.setHeaderText(null);
			alert.setContentText("Please input a valid IP address!");
			alert.showAndWait();
			System.out.println(f);

		}
		
	}

}
