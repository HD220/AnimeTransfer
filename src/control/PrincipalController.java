package app.control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import app.model.TableModel;
import app.control.SceneController;

public class PrincipalController implements Initializable {

	@FXML
	AnchorPane fmprincipal;
	@FXML
	TextField origem;
	@FXML
	Button btorigem;
	@FXML
	TableView<TableModel> table;
	@FXML
	TableColumn<TableModel, Boolean> tbcopiar;
	@FXML
	TableColumn<TableModel, String> tbarquivo;
	@FXML 
	TableColumn<TableModel, String> tbstatus;
	@FXML
	Button bttransferir;
	@FXML
	TextField destino;
	@FXML
	Button btdestino;
	@FXML
	Label lbstatus;
	@FXML
	Label lbdir;
	static String dOrigem = "";
	static String dDestino = "";

	DirectoryChooser opdir = new DirectoryChooser();
	File file_origem;
	File file_destino;
	Thread copy = new Thread();
	TaskController copyWorker;
	public static Boolean activeThread = false;
	static ObservableList<TableModel> itens = FXCollections.observableArrayList();
	@FXML
	CheckBox check;
	@FXML
	ProgressBar progressBar;
	
	@FXML
	public void action_btdestino() {
		this.file_destino = this.opdir.showDialog(SceneController.getAppScene().getWindow());
		destino.setText(this.file_destino.getAbsolutePath());
		dDestino = destino.getText();
		if (this.file_destino != null) {
			for (final TableModel dir_origem : PrincipalController.itens) {
				for (final String dir_destino : this.file_destino.list()) {
					if (dir_origem.ArquivoProperty().getValue().equalsIgnoreCase(dir_destino)) {
						dir_origem.setCopiarProperty(true);
						dir_origem.setStatusProperty("Parado");
					}
				}
			}
		}
	}

	@FXML
	public void action_btorigem() {

		this.file_origem = this.opdir.showDialog(SceneController.getAppScene().getWindow());
		origem.setText(this.file_origem.getAbsolutePath());
		dOrigem = origem.getText();
		
		if (this.file_origem != null) {
			PrincipalController.itens.clear();
			for (final String dir : this.file_origem.list()) {
				PrincipalController.itens.add(new TableModel(false, dir,""));
			}
			this.table.setItems(PrincipalController.itens);
		}
	}

	@FXML
	public void action_bttransferir() throws IOException {
		try {
			this.copyWorker = new TaskController(PrincipalController.itens, dOrigem, dDestino);
			progressBar.progressProperty().bind(copyWorker.progressProperty());
			lbdir.textProperty().bind(copyWorker.messageProperty());
			lbstatus.textProperty().bind(copyWorker.titleProperty());
			this.copy = new Thread(this.copyWorker);
			this.copy.start();
		
		} catch (final Exception e) {
			System.out.println("Thread erro: "+e.getCause());
		}
		

	}

	@FXML
	public void action_checkbox() {
		for (final TableModel item : PrincipalController.itens) {
			item.setCopiarProperty((this.check.isSelected()) ? true : false);
		}
	}
	
	

	@FXML
	public void change_destino() {
		try {
			this.file_destino = new File(destino.getText());
			if (this.file_destino != null) {
				for (final TableModel dir_origem : PrincipalController.itens) {
					for (final String dir_destino : this.file_destino.list()) {
						if (dir_origem.ArquivoProperty().getValue().equalsIgnoreCase(dir_destino)) {
							dir_origem.setCopiarProperty(true);
						}
					}
				}
				lbdir.setText("");
			} else {
				lbdir.setText("Caminho não é válido");
			}
		} catch (final Exception e) {
			lbdir.setText("Caminho não é válido");
		}
	}

	@FXML
	public void change_origem() {
		try {
			this.file_origem = new File(origem.getText());
			if (this.file_origem != null) {
				PrincipalController.itens.clear();
				for (final String dir : this.file_origem.list()) {
					PrincipalController.itens.add(new TableModel(false, dir,""));
				}
				this.table.setItems(PrincipalController.itens);
				lbdir.setText("");
			} else {
				lbdir.setText("Caminho não é válido");
			}
		} catch (final Exception e) {
			lbdir.setText("Caminho não é válido");
		}
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		tbarquivo.setCellValueFactory(new PropertyValueFactory<TableModel, String>("Arquivo"));
		tbcopiar.setCellValueFactory(new PropertyValueFactory<TableModel, Boolean>("Copiar"));
		tbstatus.setCellValueFactory(new PropertyValueFactory<TableModel, String>("Status"));
		tbcopiar.setCellFactory(new Callback<TableColumn<TableModel,Boolean>, TableCell<TableModel,Boolean>>() {
			@Override
			public TableCell<TableModel, Boolean> call(TableColumn<TableModel, Boolean> param) {
				return new CheckBoxTableCell<TableModel, Boolean>();
			}
		});

		table.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 2) {
					if (table.getSelectionModel().getSelectedItem().CopiarProperty().getValue() == true) {
						table.getSelectionModel().getSelectedItem().setCopiarProperty(false);
					} else {
						table.getSelectionModel().getSelectedItem().setCopiarProperty(true);
					}
				}
				
			}
		});

		this.table.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if ((event.getCode().toString() == "ENTER")|| (event.getCode().toString() == "SPACE")) {
					if (table.getSelectionModel().getSelectedItem().CopiarProperty().getValue() == true) {
						table.getSelectionModel().getSelectedItem().setCopiarProperty(false);
					} else {
						table.getSelectionModel().getSelectedItem().setCopiarProperty(true);
					}
				}
				
			}
		});

	}
}
