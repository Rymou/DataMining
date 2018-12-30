package Traitement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import application.FreqItemSet;
import application.controller1;

public class Classify {
	//testing and training data here
	static ArrayList<String[]> trainingSet=new  ArrayList<String[]>();
	static ArrayList<String[]> testingSet=new  ArrayList<String[]>();
// hadi fonction nedi biha gaa data mel fichier li generitih
	public static ArrayList<String[]> getData(String path) throws IOException {
		String ligne;
		String[] item;
		ArrayList<String[]>  mylist = new ArrayList<String[]>(); 
		InputStream ips=new FileInputStream(path); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		
		while((ligne=br.readLine())!= null){
			item = ligne.split(",");
			mylist.add(item);
		}
		Collections.shuffle(mylist);
		
		return mylist;
	}
	//hna neksem ala hsab pourcentage
	public static void splitData(float pourcentage,ArrayList<String[]> data) {
		int testingRate = Math.round(data.size()*pourcentage);
		for(int i=0;i<data.size();i++) {
			if(i<testingRate) {
				trainingSet.add(data.get(i));
			}
			else {
				testingSet.add(data.get(i));
				
			}
		}
	}
	//distance euclidienne entre deux strings d'une seule instance
	public static Double euclideanDistance(String instance1, String instance2) {
		float distance = 0;
		
			distance += Math.pow((Float.valueOf(instance1) - Float.valueOf(instance2)), 2);
		return (Double)Math.sqrt(distance);
	}
	// hna ala hsab chhal men voisin nedi les plus proches zaama 5 nedi 5 li krab
	public static ArrayList<String[]> neighbors(String[] testInstance,int k){
		TreeMap<Double,ArrayList<Integer>> map=new TreeMap<Double,ArrayList<Integer>>();
		
			for(int j=0;j<trainingSet.size();j++) {
				//add condition here for nominal and numeric
				
				Double distance= 0.0;
				String instance1="",instance2="";
				//euclideanDistance(testInstance,trainingSet.get(j),testInstance.length-1);
				for(int i=0;i<testInstance.length-1;i++) {
					instance1 = testInstance[i];
					instance2 = trainingSet.get(j)[i];
					if(isNumeric(instance1) && isNumeric(instance2)) {
						distance +=euclideanDistance(instance1,instance2);
					}
					else {
						if(!instance1.toLowerCase().equals(instance2.toLowerCase())){
							distance+=1;
						}
					}
				}
				if(map.containsKey(distance)) {
					//ArrayList<String[]> li = new ArrayList<String[]>();
					ArrayList<Integer> li = new ArrayList<Integer>();
					li=map.get(distance);
				
					//li.add(trainingSet.get(j));
					li.add(j);
					map.put(distance, li);
				}
				else {

					//ArrayList<String[]> li = new ArrayList<String[]>();
					//li.add(trainingSet.get(j));
					ArrayList<Integer> li = new ArrayList<Integer>();
					li.add(j);
					map.put(distance, li);
				}
			}
			
			
			ArrayList<String[]> result= new ArrayList<String[]>();
			while(result.size()!=k) {
			for(Entry<Double, ArrayList<Integer>> entry : map.entrySet()) {
				  Double key = (Double)entry.getKey();
				  ArrayList<Integer> value = (ArrayList<Integer>)entry.getValue();
				  
				  for(int j=0;j<value.size();j++) {
					  if(result.size()==k) return result;
					  result.add(trainingSet.get(value.get(j)));
				  }
				}
			}
		
		return result;
	}
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("-?\\d+\\.?\\d*$");  
	}  
	public static void affiche(ArrayList<String[]>  mylist) {
		for(int i=0;i<mylist.size();i++) {
			System.out.println();
			for(int j = 0;j<mylist.get(i).length;j++) {
				System.out.print(mylist.get(i)[j]+"|");
			}
		}
	}
	public static String getPrediction(ArrayList<String[]> result) {
		String answer = "";
		HashMap<String, Integer> map = new HashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(map);
		TreeMap<String,Integer> map1=new TreeMap<String,Integer>(bvc);
		
		for(int j=0;j<result.size();j++) {
			
			String key = result.get(j)[result.get(j).length-1];
			if(map.containsKey(key)) {
				int li;
				li=map.get(key);
				li++;
				map.put(key, li);
			}
			else {
				map.put(key, 1);
			}
		}
		map1.putAll(map);
		Map.Entry<String, Integer> entry = map1.pollFirstEntry();
		if(entry != null) {
		    answer = entry.getKey();
		}
		return answer;
	} 
	
	public static Double accuracy(int k, controller1 control) {
		int count=0;
	
		for(int j=0;j<testingSet.size();j++) {
			// hna pour chaque instance taffichi wech lazem tkon w wech dar prediction
			String hiba = "";
			hiba += Arrays.toString(testingSet.get(j));
			hiba = "\n" + "prediction : "+getPrediction(neighbors(testingSet.get(j),k)).toString()+"| true class : "+testingSet.get(j)[testingSet.get(j).length-1];
			control.dataKnn.add(new FreqItemSet(Integer.toString(j+1), hiba));
			if(getPrediction(neighbors(testingSet.get(j),k)).equals(testingSet.get(j)[testingSet.get(j).length-1])) {
				count++;
			}
		}
		
		double kaa = (double) (100*count/testingSet.size());
		System.out.println("accuracyyyyy "+kaa);
		String accu = Double.toString(kaa);
		//control.accuracy.setText(""+kaa);
		//control.accuracy.setText(""+kaa);
		return(double) (100*count/testingSet.size());
	}
	public static void afficheTreeMap(TreeMap<Double,ArrayList<String[]>> treeMap) {
		for(Entry<Double, ArrayList<String[]>> entry : treeMap.entrySet()) {
			  Double key = (Double)entry.getKey();
			  ArrayList<String[]> value = (ArrayList<String[]>)entry.getValue();
			  System.out.println("la distance + "+key);
			  affiche(value);
			}
	}
	public static void afficheTreeMapV2(TreeMap<String,Integer> treeMap) {
		for(Entry<String,Integer> entry : treeMap.entrySet()) {
			  String key = (String)entry.getKey();
			  Integer value = (Integer)entry.getValue();
			  System.out.println("la key + "+key+ "valeur "+value);
			  
			}
	}
	
	
	
	/*public static void main(String[] args) throws IOException {
		//affiche(getData());
		String path = "C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt";
		splitData((float)0.66,getData(path));
		//System.out.println("kakaa");
		System.out.println(accuracy(testingSet,5));
		float testingData = (float) 0.66;
		int nbVoisins = 5;
		//lancerKNN(path,  testingData, nbVoisins);
		//hna ya djma3a naffichiw testingset
		//affiche(testingSet);
		//whnaya trainingSet
		//affiche(trainingSet);

	}*/
	

}
class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}
