package app.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import app.Main;
import app.model.TableModel;

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
	Task<?> copyWorker = this.createWorker();
	public static Boolean activeThread = false;
	static ObservableList<TableModel> itens = FXCollections.observableArrayList();
	@FXML
	CheckBox check;
	@FXML
	ProgressBar progressBar;

	@FXML
	public void action_btdestino() {
		this.file_destino = this.opdir.showDialog(Main.mainStage);
		this.destino.setText(this.file_destino.getAbsolutePath());
		if (this.file_destino != null) {
			for (final TableModel dir_origem : SampleController.itens) {
				for (final String dir_destino : this.file_destino.list()) {
					if (dir_origem.ArquivoProperty().getValue().equalsIgnoreCase(dir_destino)) {
						dir_origem.setCopiarProperty(true);
					}
				}
			}
		}
	}

	@FXML
	public void action_btorigem() {

		this.file_origem = this.opdir.showDialog(Main.mainStage);
		this.origem.setText(this.file_origem.getAbsolutePath());
		if (this.file_origem != null) {
			SampleController.itens.clear();
			for (final String dir : this.file_origem.list()) {
				SampleController.itens.add(new TableModel(false, dir));
			}
			this.table.setItems(SampleController.itens);
		}
	}

	@FXML
	public void action_bttransferir() throws IOException {
		try {
			this.copyWorker = null;
			this.copyWorker = this.createWorker();
			//progressBar.setProgress(0);
			//progressBar.progressProperty().unbind();
			progressBar.progressProperty().bind(copyWorker.progressProperty());
			lbdir.textProperty().bind(copyWorker.messageProperty());
			lbstatus.textProperty().bind(copyWorker.titleProperty());
			this.copy = new Thread(this.copyWorker);
			this.copy.start();
		
		} catch (final Exception e) {
			System.out.println("Thread erro: "+e.getMessage());
		}
		

	}

	@FXML
	public void action_checkbox() {
		for (final TableModel item : SampleController.itens) {
			item.setCopiarProperty((this.check.isSelected()) ? true : false);
		}
	}

	@FXML
	public void change_destino() {
		try {
			this.file_destino = new File(this.destino.getText());
			if (this.file_destino != null) {
				for (final TableModel dir_origem : SampleController.itens) {
					for (final String dir_destino : this.file_destino.list()) {
						if (dir_origem.ArquivoProperty().getValue().equalsIgnoreCase(dir_destino)) {
							dir_origem.setCopiarProperty(true);
						}
					}
				}
				this.lbdir.setText("");
			} else {
				this.lbdir.setText("Caminho não é válido");
			}
		} catch (final Exception e) {
			this.lbdir.setText("Caminho não é válido");
		}
	}

	@FXML
	public void change_origem() {
		try {
			this.file_origem = new File(this.origem.getText());
			if (this.file_origem != null) {
				SampleController.itens.clear();
				for (final String dir : this.file_origem.list()) {
					SampleController.itens.add(new TableModel(false, dir));
				}
				this.table.setItems(SampleController.itens);
				this.lbdir.setText("");
			} else {
				this.lbdir.setText("Caminho não é válido");
			}
		} catch (final Exception e) {
			this.lbdir.setText("Caminho não é válido");
		}
	}

	public String geraNomeArquivo(String pasta,String origem, String destino, String nome){
		
		String reso = "undefined";
		
		if(nome.contains("1080") || nome.contains("FullHD")){
			reso = "1080";
		}else if(nome.contains("720") || nome.contains("HD")){
			reso = "720";
		}
		
		int n = 0;
		
		ArrayList<String> aux = new ArrayList<String>();
		String seq = "";
		
		for (int i = 0; i < nome.length();i++) {
			
			if(Character.isDigit(nome.charAt(i))){
				seq += nome.charAt(i);
			}else if(seq != ""){
				try{
					if(!seq.contains(reso) && !(seq.length() > 3) && !(nome.charAt(i-2) == 'S' ) ){
						aux.add(n, String.valueOf(seq));
						n++;
					}
				}catch(Exception e ){
					System.out.println(e.getMessage());
				}
				seq = "";
				
			}	
			
		}
		 
		String ext[] = nome.split("\\.");
		try{
			String retorno = pasta+" - "+aux.get(0)+" - "+"["+reso+"]."+ext[ext.length -1];
			return retorno;
		}catch(Exception e1){
			return "";
		}
		
		
	}
	
	public Task<Object> createWorker() {

		return new Task<Object>() {

			@Override
			protected Object call() throws Exception {
				
				SampleController.activeThread = true;
				// bttransferir.setVisible(false);
				for (final TableModel tableModel : SampleController.itens) {
					if (tableModel.CopiarProperty().getValue() == true) {
						final File dir = new File(file_origem.getAbsolutePath()+ "\\"+ tableModel.ArquivoProperty().getValue());
						for (final String f : dir.list()) {
							this.updateProgress(0,1);
							
							System.out.println("Nome Original: "+f);
							String nome = geraNomeArquivo(tableModel.ArquivoProperty().getValue(), file_origem.getAbsolutePath(),file_destino.getAbsolutePath(),f);
							System.out.println("Nome Destino: "+nome);
							System.out.println(" ");
							
							
							final File file = new File(dir, f);
							final InputStream in = new FileInputStream(file);
							final File destino = new File(file_destino.getAbsolutePath()+ "\\"+ tableModel.ArquivoProperty().getValue() + "\\" + nome);
							
							if (!destino.exists()) {
								if (new File(file_destino.getAbsolutePath()+ "\\"+ tableModel.ArquivoProperty().getValue() + "\\").mkdirs()) {
									System.out.println("Diretorios criados com sucesso.");
								} else {
									System.out.println("Não foi possivel criar a cadeia de diretorios");
								}
								
								try {
									final OutputStream out = new FileOutputStream(destino);
									this.updateMessage("Copiando "+ destino.getName());
									final byte[] buf = new byte[1024];
									int len;

									this.updateTitle("Transferido: "+ ((destino.length() / 1024) / 1000)+ " MB de "	+ ((file.length() / 1024) / 1000)+ " MB");

									System.out.println("Transferindo: "+ destino.getName());
									while ((len = in.read(buf)) > 0) {
										out.write(buf, 0, len);
										out.flush();
										this.updateProgress(destino.length(),file.length());
										// System.out.println("Transferido: "+((destino.length()/1024)/1000)+" MB de "+((file.length()/1024)/1000)+" MB");
										this.updateTitle("Transferido: "+ ((destino.length() / 1024) / 1000)+ " MB de "	+ ((file.length() / 1024) / 1000)+ " MB");

									}
									in.close();
									out.close();
									System.out.println("Transferido: "+ destino.getName());
								} catch (final Exception e) {
									this.updateMessage("Erro ao copiar "+ destino.getName());
								}
							}
						}
					}
				}
				this.updateProgress(0,1);
				activeThread = false;
				return true;
			}
		};
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		tbarquivo.setCellValueFactory(new PropertyValueFactory<TableModel, String>("Arquivo"));
		tbcopiar.setCellValueFactory(new PropertyValueFactory<TableModel, Boolean>("Copiar"));
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
