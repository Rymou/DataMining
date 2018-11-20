package Traitement;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.script.ScriptException;


public class Apri {
	static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	static int minSupport = 3;
	final static int N = 310, M = 8;
	static int[][] matriceData = new int[N][M];
	static ArrayList<Items> items = new ArrayList<Items>();
	static ArrayList<Items> itemsTuples = new ArrayList<Items>();
	
	//this one is la plus importante

	//HashSet<ArrayList<Integer>> itemsTuple= new HashSet<ArrayList<Integer>>();
	// Charset
	static char[] _charset;
	// Longueur max
	//static Integer _longueur = 2;

	
	// Fonction qui calcule le nombre de combinaisons max pour un charset et une longueur données
	private static Integer maxCombi (Integer charsetsize, Integer longueur) {
	    // Variable qui cumule les combinaisons possibles (par défaut = 0)
	    Double max = 0.0;
	    // Cumul pour toutes les longueurs possibles
	    for (int j=1;j<=longueur;j++) {
	        max += Math.pow((double)charsetsize, (double)j);
	    }
	    return max.intValue();
	}


	public static void representation() throws NumberFormatException, IOException {
		String ligne;
		String[] item;
		InputStream ips=new FileInputStream("C:\\Users\\USER\\Documents\\Master2\\DataMining\\data.txt"); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		int i=0;
		while(!(ligne=br.readLine()).equals("%")){
			
			item = ligne.split("\\s+");
			for(int j=0; j<item.length; j++) {
				matriceData[i][j] = Integer.parseInt(item[j]);
			}
			i++;
		}
		for(int i1=0; i1<N; i1++) {
			for(int i2=0; i2<M; i2++) {
				//System.out.print(matriceData[i1][i2]);
			}
			//System.out.println();
		}
			/*for(int j=0; j<item.length; j++) {
				if(map.containsKey(item[j])){
					//System.out.println("no");
					map.put(item[j],map.get(item[j])+1);
				}
				else {
					//System.out.println("yeaah");
					map.put(item[j],1);
				}
			}
		
		for (String key : map.keySet()) {
			System.out.println(key + ":\t" + map.get(key));
		}
		System.out.println("iiiiiiiiiiiiii = "+i);
		*/

	}
	
	
	public void create1ItemSet() {
		for(int i1=0; i1<M; i1++) {
			int cpt = 0;
			for(int i2=0; i2<N; i2++) {
				if(matriceData[i2][i1]==1) {
					cpt++;
				}
			}
			Items it = new Items(String.valueOf(i1+1),cpt);
			items.add(it);
			
			itemsTuples.add(it);
		}
		System.out.println("Support dataset de taille 1 === 1 itemset ");
		
		Iterator<Items> myListIterator = items.iterator();
		for (Items value : items) {
	    	System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
	    } 
	}
	
	
	   public String getUniqueString(String text) 
	    {
		   //System.out.println("kifech kan "+text);
	    	String data="";
	    	for(int i=0; i<text.length(); i++)
	    	{
	    		data+=(data.contains(text.charAt(i)+"")?"":text.charAt(i)+"");
	    	}
			//System.out.println("kifech wella "+data);
	    	return data;
	    } 
	
	
	
	public ArrayList<Items> createItemSetFromPreviousOne(int numKemeItem, ArrayList<Items> itemsT) {
		ArrayList<Items> itemsTuplesTemp = new ArrayList<Items>();

		
		
		//while(itemsTuples.size()!=0 && itemsTuples.size()!=1) {
			List<String> aList = new ArrayList<String>();
			String resultat = "";
			for(int i=0; i<itemsT.size(); i++) {
				resultat += itemsT.get(i).getItem();
			}
			_charset = resultat.toCharArray();
			// Nombre de combinaisons possibles pour le charset _charset et la longueur _longueur définie plus haut
			int nbCombinaison = maxCombi(_charset.length, numKemeItem);
			String mot= "";
			for(int i=0; i<nbCombinaison; i++) {

				mot = computeMot(_charset.length, i);
				//System.out.println("yeeeeeees it works 8)");
				//Items it = new Items(mot,0);
				//itemsTuplesTemp.add(it);
				
				aList.add(mot);
				//System.out.println(mot);
			}
			//Réordonner les items
			//Collections.sort(aList);
			List<String> aListTemp = new ArrayList<String>();
			for (Iterator<String> iter = aList.iterator(); iter.hasNext(); ) {
				    String s = iter.next();
				    char[] a = s.toCharArray();
				    Arrays.sort(a);
				    String sortedA = String.valueOf(a);
				    //System.out.println(sortedA);
				    //iter.remove();
				    aListTemp.add(sortedA);
			}
			Collections.sort(aListTemp);
			//System.out.println("après sort");
			for (Iterator<String> iter = aListTemp.iterator(); iter.hasNext(); ) {
			    String a = iter.next();
			    //System.out.println(a);
			}
			
			//Suppression des redondances dans les items
			List<String> setC = new ArrayList<String>();
			for (Iterator<String> iter = aListTemp.iterator(); iter.hasNext(); ) {
				    String a = iter.next();
				    //Fonction qui enlève les doublants dans un string
				    setC.add(getUniqueString(a));
				    
			}
			 //System.out.println("après suppression des redondants dans les items");
			 for (Iterator<String> iter = setC.iterator(); iter.hasNext(); ) {
				    String a = iter.next();
				    //System.out.println(a);
			}
			
			//Supprimer les items dont la longueur est inférieur a numKItems
			 Set<String> setList = setC.stream().collect(Collectors.toSet());
			
			 for (Iterator<String> iter = setList.iterator(); iter.hasNext(); ) {
				    String a = iter.next();
				    if (a.length()<numKemeItem) {
				        iter.remove();
				    }
				}
			// System.out.println("après suppression des redondants et des items dont la longueur < numKemeItem");
			 for (Iterator<String> iter = setList.iterator(); iter.hasNext(); ) {
				    String a = iter.next();
				    //System.out.println(a);
			}

			 
			 
			 //Ajouter les nouveaux k-itemset dans la liste des items avec valeur == 0
			 for (Iterator<String> iter = setList.iterator(); iter.hasNext(); ) {
				    String a = iter.next();
				    //System.out.println("keke : "+a);
				    //Vérifier qu'un items parmi ceux choisi dans "a" n'a pas été modifié précédemment
				    
				    itemsTuplesTemp.add(new Items(a,0));
			}
			 //Ajouter les nouveaux k-itemset dans la liste des items avec leurs valeurs(freqs)
			 for (Iterator<String> iter = setList.iterator(); iter.hasNext(); ) {
				    String a = iter.next();
				    int cpt = countNombreItemSat(a);
				    
			    	//System.out.println("cpt == "+cpt);

				    itemsTuplesTemp.add(new Items(a,cpt));
			}
			 
			 if(itemsTuplesTemp.size()==0)
				 //System.out.println("mahabch j'en ai marre");
			 //System.out.println("2 itemset avant suppression des valeurs < minSup");
			/* System.out.println("2 itemsets");
			for (Items value : itemsTuplesTemp) {
			    System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
			} */
			this.supprItemsInfMinSupport(itemsTuplesTemp);
			/*System.out.println("Après suppression des 2 itemset dont la valeur < minSup");
			for (Items value : itemsTuplesTemp) {
			    System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
			} */
			 
			 
			

		return itemsTuplesTemp;	 
			
			
			
		//}
		
	}
	
	
	public int countNombreItemSat(String a) {
		
		//for (Iterator<String> iter = itm.iterator(); iter.hasNext(); ) {
		    //String a = iter.next();
		    //System.out.println(a);
		    char[] c = a.toCharArray();
	    	boolean trouve = true;
	    	int cpt = 0;
		   // System.out.println("length de c == "+c.length+" c[0] = "+c[0]+" c[1] = "+c[1]);
		    for(int i=0; i<N; i++) {
		    	trouve=true;
		    	int colonne = 0;
		    	int j=0;
		    	while((j<c.length) && (trouve==true)) {
		    		//System.out.println("c = "+c[j]);
		    		if(matriceData[i][Character.getNumericValue(c[j])-1]==1) {
		    				j++;
		    		}
		    		else trouve = false; 
		    		
		    	}
		    	if((trouve==true))
		    		cpt++;
		    }
	    	//System.out.println("cpt == "+cpt);

	
		
		return cpt;
	}
	
	public ArrayList<Items> supprItemsInfMinSupport(ArrayList<Items> itm) {
		for (Iterator<Items> iterator = itm.iterator(); iterator.hasNext(); ) {
		    Items value = iterator.next();
		    if (value.getOcc()<minSupport) {
		    	//value.setItemSuppr(true);
		        iterator.remove();
		    }
		}
		//System.out.println("1 itemset après suppression des itemset<minSup");
		for (Items value : itm) {
	    	//System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
	    }
		
		return itm;
		
		
		
	}
	
	private static String computeMot (Integer charsetsize, Integer indice) 
	{
	    // Mot à retourner (par défaut vide)
	    String result = "";

	    // Calcul du mot
	    while (indice>=0) {
	         result = _charset[(indice%charsetsize)] + result;
	         indice = (indice/charsetsize) - 1;
	    }

	    return result;
	}
	
	
	public static void main(String[] args) throws IOException, ScriptException {
		representation();
		Apri a = new Apri();
		ArrayList<Items> itemsTuplesIteration;

		
		a.create1ItemSet();
		items = a.supprItemsInfMinSupport(items);
		System.out.println("1 itemset");
		for (Items value : items) {
		    System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
		} 
		//a.createItemSetFromPreviousOne(2,items);
		itemsTuplesIteration = items;
		int niveau = 1;
		//System.out.println("sizeeee == "+itemsTuplesIteration.size());
		while(itemsTuplesIteration.size()!=0 || itemsTuplesIteration.size()!=1) {
			//System.out.println("dakhaaal");
			System.out.println("size of itemData = "+itemsTuplesIteration.size());
			niveau ++;
			itemsTuplesIteration = a.createItemSetFromPreviousOne(niveau,itemsTuplesIteration);
			System.out.println(niveau+" itemset de tous les supports de ce niveaux");
			for (Items value : itemsTuplesIteration) {
			    System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
			} 
			itemsTuplesIteration = a.supprItemsInfMinSupport(itemsTuplesIteration);
			System.out.println(niveau+" itemset des support > minSup");
			for (Items value : itemsTuplesIteration) {
			    System.out.println(" Item : "+value.getItem()+" value : "+value.getOcc());
			} 
	
		}
	}

    
}
