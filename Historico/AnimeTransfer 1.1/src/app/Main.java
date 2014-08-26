package app;

import app.control.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	SceneController sceneControl;
	
	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		try {
			sceneControl = new SceneController(primaryStage);
			
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
