package app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import app.control.SampleController;

public class Main extends Application {

	public static void main(final String[] args) {
		Application.launch(args);
	}

	public static Stage mainStage;
	public static Stage secundStage;

	public static Boolean isOpen = false;

	@Override
	public void start(final Stage primaryStage) {
		try {
			Main.isOpen = true;
			final AnchorPane root = (AnchorPane) FXMLLoader.load(this
					.getClass().getResource("view/Principal.fxml"));
			final Scene scene = new Scene(root);
			scene.getStylesheets().add(
					this.getClass().getResource("view/application.css")
							.toExternalForm());
			Main.mainStage = primaryStage;
			Main.mainStage.setScene(scene);
			Main.mainStage.setResizable(false);
			Main.mainStage.show();

			Main.mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					if (!SampleController.activeThread) {
						Main.isOpen = false;
						Main.mainStage.close();
					} else {
						event.consume();

					}
					
				}
			}
			);

			scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().toString() == "ESCAPE") {
						if (!SampleController.activeThread) {
							Main.isOpen = false;
							Main.mainStage.close();
						} else {
							System.out.println("Há uma tarefa em andamento");
						}
					}
					
				}
			});

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
