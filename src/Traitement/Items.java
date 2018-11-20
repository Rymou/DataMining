package Traitement;

public class Items {
	String item;
	int occ;
	//boolean itemSuppr;
	
	public Items(String item, int occ) {
		this.item = item;
		this.occ = occ;
		//this.itemSuppr = itemSuppr;
	}
	
	
	
	public String getItem() {
		return item;
	}
	
	public int getOcc() {
		return occ;
	}
	
	
	public void setItem(String item) {
		this.item = item;
	}
	
	public void setOcc(int occ) {
		this.occ = occ;
	}
	

}
