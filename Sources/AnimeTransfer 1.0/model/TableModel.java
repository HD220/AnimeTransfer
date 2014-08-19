package app.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModel {

	private BooleanProperty CopiarProperty;
	private StringProperty ArquivoProperty;
	
	public TableModel() {
		
	}
	
	public TableModel(Boolean copiarProperty, String arquivoProperty) {
		this.CopiarProperty = new SimpleBooleanProperty(copiarProperty);
		this.ArquivoProperty = new SimpleStringProperty(arquivoProperty);
	}

	public BooleanProperty CopiarProperty() {
		return this.CopiarProperty;
	}

	public void setCopiarProperty( boolean CopiarProperty) {
		this.CopiarProperty().set(CopiarProperty);
	}

	public StringProperty ArquivoProperty() {
		return this.ArquivoProperty;
	}

	public void setArquivoProperty(final java.lang.String ArquivoProperty) {
		this.ArquivoProperty().set(ArquivoProperty);
	}

}
