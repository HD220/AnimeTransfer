package app.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModel {

	private StringProperty StatusProperty;
	private BooleanProperty CopiarProperty;
	private StringProperty ArquivoProperty;

	public TableModel() {

	}

	public TableModel(final Boolean copiarProperty, final String arquivoProperty,String statusProperty) {
		this.CopiarProperty = new SimpleBooleanProperty(copiarProperty);
		this.ArquivoProperty = new SimpleStringProperty(arquivoProperty);
		this.StatusProperty = new SimpleStringProperty(statusProperty);
	}

	public StringProperty ArquivoProperty() {
		return this.ArquivoProperty;
	}

	public StringProperty StatusProperty() {
		return this.StatusProperty;
	}

	
	public BooleanProperty CopiarProperty() {
		return this.CopiarProperty;
	}

	public void setArquivoProperty(final java.lang.String ArquivoProperty) {
		this.ArquivoProperty().set(ArquivoProperty);
	}
	
	public void setStatusProperty(final java.lang.String StatusProperty) {
		this.StatusProperty().set(StatusProperty);
	}

	public void setCopiarProperty(final boolean CopiarProperty) {
		this.CopiarProperty().set(CopiarProperty);
	}

}
