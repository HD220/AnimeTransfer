package app.control;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SceneController {
	
	static Stage appStage = new Stage();
	static AnchorPane appRoot = new AnchorPane();
	static Scene appScene;
	
	public SceneController(Stage stage) {
		
		try {
			appStage = stage;
			
			appRoot = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/app/view/Principal.fxml"));
			
			appScene = new Scene(appRoot,465,370);
			appScene.getStylesheets().add(this.getClass().getResource("/app/view/application.css").toExternalForm());
			
			appStage.setScene(appScene);
			appStage.resizableProperty().set(false);
			appStage.setTitle("AnimaTrans - Seu trasferidor de animes automatizado");
			appStage.show();
			appStage.setOnCloseRequest(event -> {
				
				if (!PrincipalController.activeThread) {
					
				} else {
					event.consume();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static Stage getAppStage() {
		return appStage;
	}

	public void setAppStage(Stage appStage) {
		SceneController.appStage = appStage;
	}

	public static AnchorPane getAppRoot() {
		return appRoot;
	}

	public void setAppRoot(AnchorPane appRoot) {
		SceneController.appRoot = appRoot;
	}

	public static Scene getAppScene() {
		return appScene;
	}

	public void setAppScene(Scene appScene) {
		SceneController.appScene = appScene;
	}
	
	
	
}
