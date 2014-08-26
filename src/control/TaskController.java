package app.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import app.model.TableModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class TaskController extends Task<TableModel> {

	static ObservableList<TableModel> listDir = FXCollections.observableArrayList();
	static FileChannel fcOrigem;    
    static FileChannel fcDestino;
	
	public TaskController(ObservableList<TableModel> itens,
			String dOrigem, String dDestino) {
		listDir =  itens;
		
	}

	public static String geraNomeArquivo(String pasta, String nome){
		
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
	
	
	
	public void copy(File origem,File destino,boolean overwrite) throws IOException{
		if (destino.exists() && !overwrite){  
            System.err.println(destino.getName()+" já existe, ignorando...");  
            return;  
        }  
		
		FileInputStream in = new FileInputStream(origem);
    	FileOutputStream out = new FileOutputStream(destino);
        
        try {	
        	
			final byte[] buf = new byte[1024];
			int len;
			
			Long mb = (destino.length() / 1024) / 1000;
			Double mbmax = (double) ((origem.length() / 1024) / 1000);
			Long mbs = new Long(0);
			
			Long timeProgress = System.currentTimeMillis();
			Long timeMbs = System.currentTimeMillis();
			System.out.println("Transferindo: "+ destino.getName());
			updateMessage("Transferindo: "+ destino.getName());
			
			this.updateTitle(" Transferido: "+ mb + " MB de "+ mbmax+ " MB "+mbs+" MB/s");
			this.updateProgress(mb,mbmax);
			
			while ((len = in.read(buf)) > 0) {
				
				out.write(buf, 0, len);
				
				if( (System.currentTimeMillis() - timeMbs) >= 1000 ){
					mb = (destino.length() / 1024) / 1000;
					timeMbs = System.currentTimeMillis();
				}
				if( (System.currentTimeMillis() - timeProgress) >= 500 ){
					mbs = (((destino.length() / 1024) / 1000) - mb);
					updateTitle("Transferido: "+ mb + " MB de "+ mbmax+ " MB "+mbs+" MB/s");
					timeProgress = System.currentTimeMillis();
				}
				this.updateProgress(destino.length(),origem.length());
			}
			
			this.updateProgress(0,1);
			this.updateTitle("");
		
		} catch (final Exception e) {
			this.updateMessage("Erro ao copiar "+ destino.getName());
		}finally{
			in.close();    
	        out.close(); 
		}
         
    }  
	
	public void copyAll(File origem,File destino,boolean overwrite) throws IOException{  
        if (!destino.exists())  
            destino.mkdir();  
        if (!origem.isDirectory())  
            throw new UnsupportedOperationException("Origem deve ser um diretório");  
        if (!destino.isDirectory())  
            throw new UnsupportedOperationException("Destino deve ser um diretório");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                updateMessage(destino.getName());
            }
        });
        File[] files = origem.listFiles();
        for (File file : files) {  
            if (file.isDirectory())  
                copyAll(file,new File(destino+"\\"+file.getName()),overwrite);  
            else{  
                System.out.println("Copiando arquivo: "+file.getName());  
                copy(file, new File(destino+"\\"+geraNomeArquivo(file.getParentFile().getName(),file.getName())),overwrite);  
                
            }  
        }
	}

	@Override
	protected TableModel call() throws Exception {
		PrincipalController.activeThread = true;
		for (TableModel dir : listDir) {
	
			if(dir.CopiarProperty().getValue() == true){
				dir.setStatusProperty("Transferindo");
				System.out.println(dir.ArquivoProperty().getValue());
				try {
					copyAll(new File(PrincipalController.dOrigem+"\\"+dir.ArquivoProperty().getValue()+"\\"),new File(PrincipalController.dDestino+"\\"+dir.ArquivoProperty().getValue()+""),false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dir.setStatusProperty("Completo");
			}
		}
		PrincipalController.activeThread = false;
		return null;
	}
	
	
}
