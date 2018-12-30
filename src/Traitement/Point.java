package Traitement;

public class Point {
	
	String[] point;
	boolean visited;
	boolean bruit;
	int cluster;
	
	public Point(String[] point, boolean visited, boolean buit, int cluster) {
		this.point = point;
		this.visited = visited;
		this.bruit = bruit;
		this.cluster = cluster;
	}
	

	
	public boolean getVisited() {
		return visited;
	}
	
	public int getCluster() {
		return cluster;
	}
	
	public void setCluster(int clutser) {
		this.cluster = cluster;
	}
	
	public String[] getPoint() {
		return point;
	}
	
	public boolean getBruit() {
		return bruit;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public void setPoint(String[] point) {
		this.point = point;
	}
	
	public void setBruit(boolean bruit) {
		this.bruit = bruit;
	}

}
