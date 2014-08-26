package app.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class FileModel {

	private LongProperty SizeProperty;
	private LongProperty FinalSizeProperty;

	public FileModel() {

	}

	public FileModel(Long sizeProperty, Long finalSizeProperty) {
		this.SizeProperty = new SimpleLongProperty(sizeProperty);
		this.FinalSizeProperty = new SimpleLongProperty(finalSizeProperty);
	}

	public LongProperty SizeProperty() {
		return this.SizeProperty;
	}

	public LongProperty FinalSizeProperty() {
		return this.FinalSizeProperty;
	}

	public void setSizeProperty(final Long SizeProperty) {
		this.SizeProperty().set(SizeProperty);
	}
	
	public void setFinalSizeProperty(final Long FinalSizeProperty) {
		this.FinalSizeProperty().set(FinalSizeProperty);
	}
	
}
