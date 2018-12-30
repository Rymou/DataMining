package Traitement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import application.FreqItemSet;
import application.controller1;
import weka.core.pmml.jaxbbindings.Cluster;

public class DBScan {
	

	static ArrayList<Point> points = new  ArrayList<Point>();
	static ArrayList<Point> visited = new ArrayList<Point>();
	static ArrayList<ArrayList<Point>> allClusters = new ArrayList<ArrayList<Point>>();
	
	
	//Maximum radius of the neighborhood to be considered
    static double eps;

    //Minimum number of points needed for a cluster
    static int minPts;


	
	public static void getData() throws IOException {
		String ligne;
		String[] item;
		ArrayList<String[]>  mylist = new ArrayList<String[]>(); 
		InputStream ips=new FileInputStream("C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt"); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		while((ligne=br.readLine())!= null){
			item = ligne.split(",");
			for(int i=0; i<item.length; i++) {
				if(item[i].equals("?")) {
					item[i] = "0";
				}
			}
			Point point = new Point(item,false,false,-1);
			points.add(point);
			mylist.add(item);
		}
	}
	
	public static void dbScan(int minPts, double eps, controller1 control) throws IOException{
        //final Map<Point, ArrayList<Point>> visited = new HashMap<Point, ArrayList<Point>>();
		getData();
		int nb = 0;
		System.out.println("minPoint == "+minPts);
		System.out.println(("eps == "+eps));
				//ArrayList<Point> visited = new ArrayList<Point>();
		int c = 0;
		for(int i=0; i<points.size(); i++) {
			//System.out.println("yes");
			if(points.get(i).visited==false) {
				//Le cas ou un point n'est pas encore visité
				ArrayList<Point> neighbors = getNeighbors(points.get(i), points, minPts, eps);
				System.out.println();
				if(neighbors.size()>=minPts) {
					System.out.println("kaka");
					ArrayList<Point> cluster = new ArrayList<Point>();
					cluster = etendreCluster(cluster, c, points.get(i), neighbors, points, minPts, eps);
					allClusters.add(cluster);
					nb++;
					
				}
				else
				{
					c++;
					System.out.println("There is noise !!");
					points.get(i).visited = true;
					//visited.put(collectionPoints.get(i), ArrayList<Point>.NOISE)
				}
			}
			
		
			
		}
		System.out.println("All clusters == "+nb);
		ArrayList<Point> bruit = new ArrayList<Point>();
		for(int i=0; i<allClusters.size(); i++) {
			System.out.println("Cluster numero : "+i+" Size = "+allClusters.size());
			String variable = "";
			for(int j=0; j<allClusters.get(i).size(); j++) {
				//System.out.println();
				//System.out.println("here");
				//if(allClusters.get(i).get(j).cluster != -1) {
					for(int k=0; k<allClusters.get(i).get(j).point.length; k++) {
						//System.out.println("Taille du point = "+allClusters.get(i).get(j).point.length);
						//System.out.println("nb point = "+allClusters.get(i).get(j).point.length);
						variable = variable + " " + allClusters.get(i).get(j).getPoint()[k];
						System.out.print(allClusters.get(i).get(j).getPoint()[k]+"   ");
					}
					//System.out.print(allClusters.get(i).get(j).cluster);
				//}
					variable = variable + "\n";
				if(allClusters.get(i).get(j).cluster != -1)
					bruit.add(allClusters.get(i).get(j));
				
				System.out.println();
			}
			String afficheVariable = "Cluster "+Integer.toString(i+1)+" ==> \n"+variable;
			
			control.dataDBScan.add(new FreqItemSet(Integer.toString(i+1), afficheVariable));
		}
		System.out.println("Les Points bruits = "+bruit.size());
		String var = "";
		for(int i=0; i<bruit.size(); i++) {
			for(int j=0; j<bruit.get(i).point.length; j++) {
				System.out.print(bruit.get(i).point[j]+"  ");
				var = var + " " + bruit.get(i).point[j];
			}
			var = var + "\n";
			System.out.println();
		}
		String v = "Bruit "+"\n"+var;
		control.dataDBScan.add(new FreqItemSet(Integer.toString(nb+1), v));

		control.nbClust.setText(""+allClusters.size());
		control.nbBruit.setText(""+bruit.size());
		
		ArrayList<Point> centreGravGlobal = new ArrayList<Point>();
		//Calcul des performances des clusters
		double inertieIntraGlob = 0.0;
		for(int i=0; i<allClusters.size(); i++) {
			ArrayList<String> centreGrav = centreDeGravite(allClusters.get(i));
			String tmp[] = new String[centreGrav.size()];
			//System.out.println("tttttttttttmmmmmmmmppppppp");
			for(int j1=0; j1<centreGrav.size(); j1++) {
				tmp[j1] = centreGrav.get(j1);
				//System.out.print(tmp[j]);
			}
			Point pGlobal = new Point(tmp, false, false, -1);
			centreGravGlobal.add(pGlobal);
			System.out.println("Centre de Gravité du cluster "+(i+1));
			for(int j=0; j<centreGrav.size(); j++) {
				System.out.print(centreGrav.get(j)+"  ");
			}
			inertieIntraGlob += inertieIntraClasse(centreGrav, allClusters.get(i));
			System.out.println("Inertie intraclasse de ce cluster == "+inertieIntraClasse(centreGrav, allClusters.get(i)));
			
		}
		double ii = (Double)(inertieIntraGlob/centreGravGlobal.size());

		System.out.println("Inertie intra globale == "+ii);
		control.inertieInter.setText(""+ii);
		
		//Calcul de l'intra
		ArrayList<String> centreGraviteGlobal = centreDeGravite(centreGravGlobal);
		double in = inertieIntraClasse(centreGraviteGlobal, centreGravGlobal);
		in = (Double)(in/centreGravGlobal.size());
		System.out.println("Inertie inter globale == "+in);
		control.inertieIntra.setText(""+in);
	}
	
	
	public static double inertieIntraClasse(ArrayList<String> centreGrav, ArrayList<Point> cluster) {
		double inertieIntra = 0.0;
		
		for(int i=0; i<cluster.size(); i++) {
			String tmp[] = new String[cluster.get(i).point.length];
			//System.out.println("tttttttttttmmmmmmmmppppppp");
			for(int j=0; j<centreGrav.size(); j++) {
				tmp[j] = centreGrav.get(j);
				//System.out.print(tmp[j]);
			}
			Point centreP = new Point(tmp,false,false,-1);
			double distance = distance(cluster.get(i),centreP);
			inertieIntra += Math.pow(distance, 2);
			
		}
		
		return inertieIntra;
	}
	
	public static double calculMoy(ArrayList<String> cluster) {
		
		double moyenne = 0.0;
		
		if(isNumeric(cluster.get(0))){
			for(int i=0; i<cluster.size(); i++) {
				double tmp = Double.parseDouble(cluster.get(i));
				moyenne += tmp;
			
			}
			moyenne = (double) moyenne / cluster.size();
		}
		
		return moyenne;
	}
	
	public static String calculMode(ArrayList<String> cluster) {
	
		String varTmp = cluster.get(0);
		int maxCount = 0;
		for(int i=0; i<cluster.size(); i++) {
			int count = 0;
			for(int j=0; j<cluster.size(); j++) {
				if(cluster.get(i).equals(cluster.get(j)))
					count ++;
			}
			
			if(count>maxCount) {
				maxCount = count;
				varTmp = cluster.get(i);
			}	
			
		}
		
		return varTmp;
		
	}
	
	public static ArrayList<String> centreDeGravite(ArrayList<Point> cluster) {
		
		double moyenne;
		String mode;
		int k = 0;
		ArrayList<String> centreGrav = new ArrayList<String>();
		if(cluster.size()!=0) {
		for(int i=0; i<cluster.get(0).point.length; i++) {
			ArrayList<String> tempCluster = new ArrayList<String>();
			for(int j=0; j<cluster.size(); j++) {
				tempCluster.add(cluster.get(j).point[i]);
			}
			//ici je calcule la moyenne de ce résultat et je la stock 
			if(isNumeric(tempCluster.get(0))) {
				moyenne = calculMoy(tempCluster);
				moyenne = moyenne / cluster.size();
				centreGrav.add(""+Double.valueOf(moyenne));
				System.out.println("numériique l'attribut hada");
			}
			else {
				System.out.println("nominal l'attribut hada");
				mode = calculMode(tempCluster);
				centreGrav.add(mode);
			}
			k++;
		}
		}
		return centreGrav;
		
		
	}
	
	
	public static ArrayList<Point> etendreCluster(ArrayList<Point> cluster, int numCluster, Point point, ArrayList<Point> neighbors, ArrayList<Point> collectionPoints, int minPts, double eps){
		//cluster.add(point);
		//visited.add(point);
		cluster.add(point);
		point.visited = true;
		point.cluster = numCluster;
		int index = 0;
		while(index<neighbors.size()) {
			//Point p = neighbors.get(index);
			//Point pStatus = visited.get(index)
			if(neighbors.get(index).visited == false) {
				neighbors.get(index).visited = true;
			
				ArrayList<Point> pointNeighbors = getNeighbors(neighbors.get(index), points, minPts, eps);
				
				if(pointNeighbors.size()>=minPts) {
					neighbors = concat(neighbors,pointNeighbors);					
					
				}
				
			}
			if(neighbors.get(index).cluster == -1) {
				//neighbors.get(index).cluster = 
				cluster.add(neighbors.get(index));
			}
			index ++;				
		}

		return cluster;
	}
	
	public static boolean contains(ArrayList<Point> arrayL, Point p) {
		
		int i=0; 
		boolean equals = true, contain = false;
		
		while(i<arrayL.size() && contain == false) {
			if(arrayL.get(i).point.length == p.point.length) {
				int j=0;
				while(j<arrayL.get(i).point.length && equals == true) {
					if(isNumeric(arrayL.get(i).point[j]) && isNumeric(p.point[j])){
						
						if(arrayL.get(i).point[j]!= p.point[j]) {
							equals = false;
						}
					}
					else {
						if(!arrayL.get(i).point[j].equals(p.point[j])) {
							equals = false;
						}
					}
					j++;

				}
				if(equals == true)
					contain = true;
			}
			i++;
		}
		return contain;
	}
	
	
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("^([+-]?(\\d+\\.)?\\d+)$");  
	}  
	
	public static ArrayList<Point> concat(ArrayList<Point> one, ArrayList<Point> two) {
		ArrayList<Point> oneSet = one;
		for(int i=0; i<two.size(); i++) {
			if(contains(oneSet, two.get(i))==false) {
				one.add(two.get(i));
			}
			
		}
		return one;
	}
	
	
	
	public static double distance(Point p1, Point p2) {
		double distance = 0.0;
		//System.out.println("calcul de la distance ...");
		for(int i=0; i<p1.point.length; i++) {
			
			if(isNumeric(p1.point[i]) && isNumeric(p2.point[i])) {
				distance +=euclideanDistance(p1.point[i],p2.point[i]);
			}
			else {
				if(!p1.point[i].toLowerCase().equals(p2.point[i].toLowerCase())){
					distance+=1;
				}
			}	
		}
		
		System.out.println("Distance == "+(Double)Math.sqrt(distance));
		return (Double)Math.sqrt(distance);
	}
	
	public static ArrayList<Point> getNeighbors(Point point, ArrayList<Point> allPoints, int minPts, double eps){
		ArrayList<Point> neighbors = new ArrayList<Point>();
		
		for(int i=0; i<allPoints.size(); i++) {
			if(!equals(allPoints.get(i),point)&&(distance(point,allPoints.get(i)) <= eps))
			{
				System.out.println("un neighbor retrouvé");
				neighbors.add(allPoints.get(i));
			}
		}
		
		return neighbors;
	}
	
	public static boolean equals(Point p1, Point p2) {
		//System.out.println("essss");
		int i=0,j=0;
		boolean equal = true;
		if(p1.point.length != p2.point.length)
			return false;
		while((i<p1.point.length)&&(j<p2.point.length)&&(equal == true)) {
			if(!p1.point[i].equals(p2.point[j]))
				equal = false;
			i++;
			j++;
		}
		if(equal == false) {
			//System.out.println("faux");
			return false;
		}
		
		return true;
	}
	
	
	
	

	public static Double euclideanDistance(String instance1, String instance2) {
		double distance = 0.0;
		
			distance += Math.pow((Float.valueOf(instance1) - Float.valueOf(instance2)), 2);
		return distance;
	}
	
	/*public static void main(String[] args) throws IOException {
		//getData();
		dbScan(5, 0.2);
		/*String s1[] = new String[3];
		s1[0] = "1";
		s1[1] = "2";
		s1[2] = "3";
		Point p1 = new Point(s1,false,false,-1);
		
		String s2[] = new String[4];
		s2[0] = "1";
		s2[1] = "2";
		s2[2] = "3";
		s2[3] = "4";
		Point p2 = new Point(s2,false,false,-1);
		
		if(equals(p1,p2))
			System.out.println("ils sont egaux");
		else
			System.out.println("ils ne sont pas egaux");
		
	
	
	
	}
*/
}
