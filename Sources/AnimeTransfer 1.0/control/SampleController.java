package app.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import app.Main;
import app.model.TableModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.CheckBox;
import javafx.util.Callback;
import javafx.scene.control.ProgressBar;

public class SampleController implements Initializable {

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
	Button bttransferir;
	@FXML
	TextField destino;
	@FXML
	Button btdestino;
	@FXML
	Label lbstatus;
	@FXML
	Label lbdir;

	DirectoryChooser opdir = new DirectoryChooser();
	File file_origem;
	File file_destino;
	Thread copy = new Thread();
	

	ObservableList<TableModel> itens = FXCollections.observableArrayList();
	@FXML
	CheckBox check;
	@FXML
	ProgressBar progressBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		tbarquivo
				.setCellValueFactory(new PropertyValueFactory<TableModel, String>(
						"Arquivo"));
		tbcopiar.setCellValueFactory(new PropertyValueFactory<TableModel, Boolean>(
				"Copiar"));
		tbcopiar.setCellFactory(new Callback<TableColumn<TableModel, Boolean>, TableCell<TableModel, Boolean>>() {
			public TableCell<TableModel, Boolean> call(
					TableColumn<TableModel, Boolean> p) {
				return new CheckBoxTableCell<TableModel, Boolean>();
			}
		});

		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 0) {
					if (table.getSelectionModel().getSelectedItem()
							.CopiarProperty().getValue() == true) {
						table.getSelectionModel().getSelectedItem()
								.setCopiarProperty(false);
					} else {
						table.getSelectionModel().getSelectedItem()
								.setCopiarProperty(true);
					}
				}
			}
		});

		table.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().toString() == "ENTER"
						|| event.getCode().toString() == "SPACE") {
					if (table.getSelectionModel().getSelectedItem()
							.CopiarProperty().getValue() == true) {
						table.getSelectionModel().getSelectedItem()
								.setCopiarProperty(false);
					} else {
						table.getSelectionModel().getSelectedItem()
								.setCopiarProperty(true);
					}
				}
			}
		});

	}

	@FXML
	public void action_btorigem() {

		file_origem = opdir.showDialog(Main.mainStage);
		origem.setText(file_origem.getAbsolutePath());
		if (file_origem != null) {
			itens.clear();
			for (String dir : file_origem.list()) {
				itens.add(new TableModel(false, dir));
			}
			table.setItems(itens);
		}
	}

	@FXML
	public void action_btdestino() {
		file_destino = opdir.showDialog(Main.mainStage);
		destino.setText(file_destino.getAbsolutePath());
		if (file_destino != null) {
			for (TableModel dir_origem : itens) {
				for (String dir_destino : file_destino.list()) {
					if (dir_origem.ArquivoProperty().getValue()
							.equalsIgnoreCase(dir_destino)) {
						dir_origem.setCopiarProperty(true);
					}
				}
			}
		}
	}

	public Task<Object> createWorker() {
		
		return new Task<Object>() {
			
			@Override
			protected Object call() throws Exception {
				// bttransferir.setVisible(false);
				for (TableModel tableModel : itens) {
					System.out.println("Verificando: "+file_origem.getAbsoluteFile()
							+ "\\"
							+ tableModel.ArquivoProperty().getValue());
					if (tableModel.CopiarProperty().getValue() == true) {
						File dir = new File(file_origem.getAbsoluteFile()
								+ "\\"
								+ tableModel.ArquivoProperty().getValue());
						System.out.println("Copiando: "+file_origem.getAbsoluteFile()
								+ "\\"
								+ tableModel.ArquivoProperty().getValue());
						for (String f : dir.list()) {
							File file = new File(dir,f);
							InputStream in = new FileInputStream(file);
							File destino = new File(file_destino.getAbsoluteFile()+ "\\"+ tableModel.ArquivoProperty().getValue() + "\\"+ f);
							
							System.out.println("Transferindo: "+file_destino.getAbsoluteFile()+ "\\"+ tableModel.ArquivoProperty().getValue() + "\\"+ file.getName());
							
							if (!destino.exists()) {
								if (new File(file_destino.getAbsoluteFile()+ "\\"+ tableModel.ArquivoProperty().getValue() + "\\").mkdirs()) {
									System.out.println("Diretorios criados com sucesso.");
								} else {
									System.out.println("Não foi possivel criar a cadeia de diretorios");
								}
							}
							try {
								OutputStream out = new FileOutputStream(destino);
								updateMessage("Copiando " + destino.getName());
								byte[] buf = new byte[2048];
								int len;
								while ((len = in.read(buf)) > 0) {
									out.write(buf, 0, len);
									out.flush();
									updateProgress(destino.length(), file.length());
									System.out.println("Transferido: "+((destino.length()/1024)/1000)+" MB de "+((file.length()/1024)/1000)+" MB");
									updateTitle("Transferido: "+((destino.length()/1024)/1000)+" MB de "+((file.length()/1024)/1000)+" MB");
									
								}
								in.close();
								out.close();
							} catch (Exception e) {
								updateMessage("Erro ao copiar " + destino.getName());
							}
							

						}
					}
				}
				progressBar.setProgress(0);
				progressBar.progressProperty().unbind();
				// bttransferir.setVisible(true);
				return true;
			}
		};
	}

	@FXML
	public void action_bttransferir() throws IOException {
		try {	
			if(copy.isAlive()){
				System.out.println(copy.isAlive());
			}else{
				Task<?> copyWorker = createWorker();
				progressBar.setProgress(0);
				progressBar.progressProperty().unbind();
				progressBar.progressProperty().bind(copyWorker.progressProperty());
				lbdir.textProperty().bind(copyWorker.messageProperty());
				lbstatus.textProperty().bind(copyWorker.titleProperty());
				Thread copy = new Thread(copyWorker);
				copy.start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	@FXML
	public void action_checkbox() {
		for (TableModel item : itens) {
			item.setCopiarProperty((check.isSelected()) ? true : false);
		}
	}

	@FXML
	public void change_origem() {
		try {
			file_origem = new File(origem.getText());
			if (file_origem != null) {
				itens.clear();
				for (String dir : file_origem.list()) {
					itens.add(new TableModel(false, dir));
				}
				table.setItems(itens);
				lbdir.setText("");
			} else {
				lbdir.setText("Caminho não é válido");
			}
		} catch (Exception e) {
			lbdir.setText("Caminho não é válido");
		}
	}

	@FXML
	public void change_destino() {
		try {
			file_destino = new File(destino.getText());
			if (file_destino != null) {
				for (TableModel dir_origem : itens) {
					for (String dir_destino : file_destino.list()) {
						if (dir_origem.ArquivoProperty().getValue()
								.equalsIgnoreCase(dir_destino)) {
							dir_origem.setCopiarProperty(true);
						}
					}
				}
				lbdir.setText("");
			} else {
				lbdir.setText("Caminho não é válido");
			}
		} catch (Exception e) {
			lbdir.setText("Caminho não é válido");
		}
	}

}
