package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FreqItemSet {

	private StringProperty numFreqItem = new SimpleStringProperty();
	private StringProperty itemSet = new SimpleStringProperty();
	
	public FreqItemSet(String num, String itemSet) {
		this.numFreqItem.set(num);
		this.itemSet.set(itemSet);
	}
	
	public String getNumFreqItem() {
		return numFreqItem.get();
	}
	
	public String getItemSet() {
		return itemSet.get();
	}
}
