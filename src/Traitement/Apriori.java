package Traitement;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import application.FreqItemSet;
import application.controller1;
import application.rules;

public class Apriori {
	static char[] _charset;
	
	static ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> _transactions = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> _transactionsGlobale = new ArrayList<ArrayList<String>>();

	static ArrayList<ArrayList<String>> prevItemSetsWithMinSupportCount = new ArrayList<ArrayList<String>>();
	static int confiance = 1;
	
	public static void representation(controller1 control,String path, int minSupportCount, double minConf) throws IOException {
		System.out.println("min sup == "+minSupportCount);
		


		String ligne;
		String[] item;
		InputStream ips=new FileInputStream(path); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		
		while((ligne=br.readLine())!= null){
			int numAttr = 1;
			ArrayList<String> transaction = new ArrayList<String>();
			item = ligne.split(",");
			//int numAttrMax = item.length;
			for (int j = 0; j < item.length; j++) {
				String s = Integer.toString(numAttr)+":"+item[j];
				
				transaction.add(s);
				numAttr++;
			}
			transactions.add(transaction);
			_transactions.add(transaction);
			_transactionsGlobale.add(transaction);
		}
		
				
		ArrayList<String> items = getUniqueItems(transactions);

		int x = 0; 
		while (true) {

			//Ajouter un itemSet de plus que l'ancienne itération
			x++;

			
			ArrayList<Integer> supportCountList = new ArrayList<Integer>();

			// Récuperer toutes les pérmutation, il y en aura x elements dans chaque itemsets
			ArrayList<ArrayList<String>> itemSets = getItemSets(items, x);

			// Calculer les supports des itemSet générés précédemment
			for (ArrayList<String> itemSet : itemSets) {

				int count = 0;
				for (ArrayList<String> transaction : transactions) {
					if (existsInTransaction(itemSet, transaction)) count++;
				}
				supportCountList.add(count);
			}

			ArrayList<ArrayList<String>> itemSetsWithMinSupportCount = getItemSetsWithMinSupportCount(itemSets, supportCountList, minSupportCount);

			// Sortie, avec aucun itemSet dont le support est >= minSup
			int k=0;
			if (itemSetsWithMinSupportCount.size() == 0) {
				System.out.print("Les itemSet les plus fréquents sont:  ");
				System.out.println(prevItemSetsWithMinSupportCount);
				
				for(int i=0; i<prevItemSetsWithMinSupportCount.size(); i++) {
					String itemu = "";

					//System.out.println(prevItemSetsWithMinSupportCount.get(i).toString());
					for(int j=0; j<prevItemSetsWithMinSupportCount.get(i).size(); j++) {
						//System.out.print(prevItemSetsWithMinSupportCount.get(i).get(j));
						
						
						itemu = itemu + prevItemSetsWithMinSupportCount.get(i).get(j)+". ";
					}
					
					control.dataItemFreq.add(new FreqItemSet(Integer.toString(i+1), itemu));
					//if(k<supportCountList.size()) {
						
				
				}
				//rulesAssociations(control, prevItemSetsWithMinSupportCount);
				associationRules(control, prevItemSetsWithMinSupportCount, minConf);

				break;
			}

			items = getUniqueItems(itemSetsWithMinSupportCount);

			prevItemSetsWithMinSupportCount = itemSetsWithMinSupportCount;
		}
	}
	
	public static double calculateConf(ArrayList<ArrayList<String>> transactions, ArrayList<String> gauche, ArrayList<String> droit) {
		double conf = 0.0;
		int count1 = 0, count2 = 0;
		String pG="", pD="";
		
		for(ArrayList<String> transaction : transactions) {
			if(existsInTransaction(gauche, transaction)) count1++;
		}
		ArrayList<String> newAll = new ArrayList<String>();
		for(int i=0; i<gauche.size(); i++) {
			newAll.add(gauche.get(i));
		}
		for(int i=0; i<droit.size(); i++) {
			newAll.add(droit.get(i));
		}
		
		for(ArrayList<String> transaction : transactions) {
			if(existsInTransaction(newAll, transaction)) count2++;
		}
		
		conf =(double)(count2)/(double)(count1);		
		for(String s: gauche) {
			pG += s;
		}
		for(String s: newAll) {
			pD += s;
		}
		//System.out.println("partie gauche == "+pG+" == "+count1);
		//System.out.println("partie droite == "+pD+" == "+count2);
		//System.out.println("confiance == "+conf);
		
		return conf;
	}


	// Returns the list of unqiue items from a list of transactions
	public static ArrayList<String> getUniqueItems (ArrayList<ArrayList<String>> data) {
		ArrayList<String> toReturn = new ArrayList<String>();

		for (ArrayList<String> transaction : data) {
			for (String item : transaction) {
				if (!toReturn.contains(item)) toReturn.add(item);
			}
		}


		Collections.sort(toReturn);
		return toReturn;
	}

	// Returns a list of itemsets, where each itemset has x number of items
	public static ArrayList<ArrayList<String>> getItemSets (ArrayList<String> items, int number) {
		if (number == 1) {

			// Return ArrayList of (ArrayList with one item)
			ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();
			for (String item : items) {
				ArrayList<String> aList = new ArrayList<String>();
				aList.add(item);
				toReturn.add(aList);
			}
			return toReturn;

		} else {

			int size = items.size();

			ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();

			for (int i = 0; i < size; i++) {

				// Copy items to _items
				ArrayList<String> _items = new ArrayList<String>();
				for (String item : items) {
					_items.add(item);
				}

				// Get item at i-th position
				String thisItem = items.get(i);

				// Remove items upto i, inclusive
				for (int j = 0; j <= i; j++) {
					_items.remove(0);
				}

				
				ArrayList<ArrayList<String>> permutationsBelow = getItemSets(_items, number - 1);

				// Ajouter cet itemSet à toutes les permutations possibles puis les ajouter à toReturn 
				for (ArrayList<String> aList : permutationsBelow) {
					aList.add(thisItem);
					Collections.sort(aList);
					toReturn.add(aList);
				}

			}

			return toReturn;

		}
	}

	// Vérifier si un itemSet existe dans une transaction
	public static boolean existsInTransaction (ArrayList<String> items, ArrayList<String> transaction) {
		for (String item : items) {
			if (!transaction.contains(item)) return false;
		}
		return true;
	}

	// Retourner les itemSets qui ont un support >= minSup
	public static ArrayList<ArrayList<String>> getItemSetsWithMinSupportCount (
		ArrayList<ArrayList<String>> itemSets, ArrayList<Integer> count, int minSupportCount) {

		ArrayList<ArrayList<String>> toReturn = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < count.size(); i++) {
			int c = count.get(i);
			if (c >= minSupportCount) {
				toReturn.add(itemSets.get(i));
				System.out.println(itemSets.get(i)+" : Support = "+c);
			}
		}
		return toReturn;
	}
	
	// Fonction qui calcule le nombre de combinaisons max pour un charset et une longueur données
	public static Integer maxCombi (Integer charsetsize, Integer longueur) {
	    // Variable qui cumule les combinaisons possibles (par défaut = 0)
	    Double max = 0.0;
	    // Cumul pour toutes les longueurs possibles
	    for (int j=1;j<=longueur;j++) {
	        max += Math.pow((double)charsetsize, (double)j);
	    }
	    return max.intValue();
	}
	
	public static String computeMot (Integer charsetsize, Integer indice) 
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
	
	
	public static void associationRules(controller1 control, ArrayList<ArrayList<String>> itemSets, double confianceMin) {
		ArrayList<ArrayList<String>> subSet = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<ArrayList<String>>> allSubSet = new ArrayList<ArrayList<ArrayList<String>>>();
		for(int i=0; i<itemSets.size(); i++) {
			
			int p = 0;
			int n = itemSets.get(i).size();
			for (int j = 0; j < (1<<n); j++) 
		     { 
				
				ArrayList<String> subSubSet = new ArrayList<String>();
		           // System.out.print("{ "); 
		            // Print current subset 
		            if(p!=0) {
		            for (int k = 0; k < n; k++) {
		            	if ((j & (1 << k)) > 0 ) {
		            		 subSubSet.add(itemSets.get(i).get(k));
		            		 //System.out.println(itemSets.get(i).get(k)+" ");	 
		            	}
		            	
		            	
		            }
		            
			            subSet.add(subSubSet);
			           // System.out.print(" }");
		            }
		            p++;

		     }
			allSubSet.add(subSet);
			
		}
		//System.out.println();
		//for(int i=0; i<subSet.size(); i++) {
			//for(int j=0; j<subSet.get(i).size(); j++) {
				//System.out.print(subSet.get(i).get(j)+ " ");
			//}
			
			//System.out.println();
		//}
		System.out.println("haya bina");
		ArrayList<String> rulesWithConf = new ArrayList<String>();
		ArrayList<String> rules = new ArrayList<String>();
		ArrayList<String> gauche = new ArrayList<String>();
		ArrayList<String> droit = new ArrayList<String>();;
		
		
		
		for(int i=0; i<allSubSet.size(); i++) {
			//System.out.println("Taille allSubSet == "+allSubSet.size());
			//int k=0; 
			for(int j=0; j<allSubSet.get(i).size(); j++) {
				//System.out.println("Workiing .. ");
				gauche = allSubSet.get(i).get(j);
				for(int k = 0; k<allSubSet.get(i).size(); k++) {
					droit = allSubSet.get(i).get(k);
					if(k!=j) {
						if(!existeInSubSet(gauche, droit)) {
							double conf = calculateConf(_transactions, gauche, droit);
							if(conf>=confianceMin) {
								String rule = "";
								String rulesWithC = "";
								for(int k1=0; k1<gauche.size(); k1++) {
									rule = rule + " " + gauche.get(k1);
									rulesWithC = rulesWithC + " " + gauche.get(k1);
								}
								rule = rule + " ==> ";
								rulesWithC = rulesWithC + " ==> ";
								for(int k2=0; k2<droit.size(); k2++) {
									rule = rule + " " + droit.get(k2);
									rulesWithC = rulesWithC + " " + droit.get(k2);

								}
								rulesWithC = rulesWithC + " conf = "+Double.toString(conf);
								rules.add(rule);
								rulesWithConf.add(rulesWithC);
							}
						}
					}
				}
				
			}
		}
		List<String> setC = new ArrayList<String>();
		for(int i=0; i<rulesWithConf.size(); i++) {
			setC.add(rulesWithConf.get(i));
			//System.out.println(rulesWithConf.get(i));
		}
		int i =0;
		Set<String> setList = setC.stream().collect(Collectors.toSet());
		for(Iterator<String> iter = setList.iterator(); iter.hasNext();) {
			String a = iter.next();
			control.dataRules.add(new rules(Integer.toString(i+1), a));
			i++;
		}

	}
	
	public static boolean existeInSubSet(ArrayList<String> gauche, ArrayList<String> droit) {
		for (int i=0; i<gauche.size(); i++) {
			int j=0;
			while(j<droit.size()) {
				if(gauche.get(i).equals(droit.get(j)))
					return true;
				j++;
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
}