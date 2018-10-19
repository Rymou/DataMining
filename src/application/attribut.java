package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class attribut {

	private StringProperty numAttr = new SimpleStringProperty();
	private StringProperty nomAttr = new SimpleStringProperty();
	private StringProperty typeAttr = new SimpleStringProperty();

	
	
	public attribut(String num, String nomAttr, String type) {
		this.numAttr.set(num);
		this.nomAttr.set(nomAttr);
		this.typeAttr.set(type);
	}
	
	public StringProperty numA() { return numAttr;}
	
	public StringProperty nomAttr() { return nomAttr;}

	public String getNumAttr() {
		return numAttr.get();
	}
	
	public String getNomAttr() {
		return nomAttr.get();
	}
	
	public String getTypeAttr() {
		return typeAttr.get();
	}
}
