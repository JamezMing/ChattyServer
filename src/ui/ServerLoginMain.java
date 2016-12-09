package ui;

import java.io.IOException;

import global.ServerLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ServerLoginMain extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout; 
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Log In");
        initRootLayout();
    }
	
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerLoginMain.class.getResource("ServerLoginUI.fxml"));
            ServerLoginUIController con = new ServerLoginUIController();
            loader.setController(con);
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            ServerLogger.init();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

	public static void main(String[] args) {
		launch(args);
	}
}
