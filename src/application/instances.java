package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class instances {
	
	private StringProperty numAttr = new SimpleStringProperty();
	private StringProperty instance = new SimpleStringProperty();
	
	public instances(String num, String instance) {
		this.numAttr.set(num);
		this.instance.set(instance);
	}
	
	public String getNumAttr() {
		return numAttr.get();
	}
	
	public String getInstance() {
		return instance.get();
	}
	

}
