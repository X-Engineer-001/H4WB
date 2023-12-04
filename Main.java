package h4wb;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main extends Application {
	
	public static Stage stage;
	public static void main(String[] args){
		launch(args);
	}
	public void start(Stage mainStage) throws IOException {
		stage = mainStage;
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
		Scene scene = new Scene(loader.load());
		scene.setOnKeyPressed(((MainController)loader.getController()).onKeyPressed);
	    Main.stage.setScene(scene);
	    mainStage.setX(0);
	    mainStage.setY(0);
	    mainStage.initStyle(StageStyle.UNDECORATED);
		mainStage.setResizable(false);
	    mainStage.show();
	}

}
