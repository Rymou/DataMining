package Traitement;

import java.io.*;
import java.util.*;

import application.FreqItemSet;
import application.rules;
import application.controller2;

public class Apriori {
	static char[] _charset;
	static 	ArrayList<String> rules = new ArrayList<String>();
	static ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> _transactions = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> prevItemSetsWithMinSupportCount = new ArrayList<ArrayList<String>>();
	
	public static void representation(controller2 control, int minSupportCount) throws IOException {
		System.out.println("min sup == "+minSupportCount);
		

		String ligne;
		String[] item;
		InputStream ips=new FileInputStream("C:\\Users\\USER\\Documents\\Master2\\DataMining\\try.txt"); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		
		while(!(ligne=br.readLine()).equals("%")){
		
			ArrayList<String> transaction = new ArrayList<String>();
			item = ligne.split("\\s+");
			for (int j = 0; j < item.length; j++) 
				transaction.add(item[j]);
			transactions.add(transaction);
			_transactions.add(transaction);
		}
		
		
		
			

		
		
		// Get input
		/*Scanner terminal = new Scanner(System.in);
		System.out.print("Number of transactions: ");
		int numberOfTransactions = Integer.parseInt(terminal.nextLine());
		System.out.println("Enter transactions separated by new line and items separated by spaces:");

		ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> _transactions = new ArrayList<ArrayList<String>>();

		ArrayList<ArrayList<String>> prevItemSetsWithMinSupportCount = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < numberOfTransactions; i++) {
			ArrayList<String> transaction = new ArrayList<String>();
			String str = terminal.nextLine();
			String arr[] = str.split(" ");
			for (int j = 0; j < arr.length; j++) transaction.add(arr[j]);
			transactions.add(transaction);
			_transactions.add(transaction);
		}
*/
				
		ArrayList<String> items = getUniqueItems(transactions);

		int x = 0; // x is the number of elements in the item-sets to consider
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
			if (itemSetsWithMinSupportCount.size() == 0) {
				System.out.print("Les itemSet les plus fréquents sont:  ");
				//System.out.println(prevItemSetsWithMinSupportCount);
				
				for(int i=0; i<prevItemSetsWithMinSupportCount.size(); i++) {
					String itemu = "";
					//System.out.println(prevItemSetsWithMinSupportCount.get(i).toString());
					for(int j=0; j<prevItemSetsWithMinSupportCount.get(i).size(); j++) {
						//System.out.print(prevItemSetsWithMinSupportCount.get(i).get(j));
						itemu = itemu + prevItemSetsWithMinSupportCount.get(i).get(j)+". ";
					}
					control.dataItemFreq.add(new FreqItemSet(Integer.toString(i+1), itemu));
					System.out.println(itemu);
				}
				
			/*for(int i=0; i<apriori.rules.size(); i++) {
				dataItemFreq.add(new FreqItemSet(Integer.toString(i+1), apriori.rules.get(i)));
				System.out.println(apriori.rules.get(i));
			}*/
			
				
				rulesAssociations(control, prevItemSetsWithMinSupportCount);

				break;
			}

			items = getUniqueItems(itemSetsWithMinSupportCount);

			prevItemSetsWithMinSupportCount = itemSetsWithMinSupportCount;
		}
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
		
		/*System.out.println("Itemset");
		for(int i=0; i<toReturn.size(); i++) {
			System.out.println(toReturn.get(i));
		}*/

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
	
	public static void rulesAssociations(controller2 control, ArrayList<ArrayList<String>> itemSets) {
		System.out.println("Les règles d'associations sont :");
		for(int i=0; i<itemSets.size(); i++) {
			//ArrayList<String> r = new ArrayList<String>();
			//System.out.print("Règle : ");
			String itemGauche = "";
			for(int j=0; j<itemSets.get(i).size(); j++) {
				String item1 = itemSets.get(i).get(j);
				for(int k=0; k<itemSets.get(i).size(); k++) {
					if(k!=j) {
						//System.out.println(item1+" ==> "+itemSets.get(i).get(k));
						rules.add(item1+" ==> "+itemSets.get(i).get(k));
					}
				
				}
				itemGauche = itemGauche + itemSets.get(i).get(j) + " ";
				String itemDroit = "";
				//System.out.println(itemGauche);
				for(int k1=j+1; k1<itemSets.get(i).size(); k1++) {
					itemDroit = itemDroit + itemSets.get(i).get(k1) + " ";
					//System.out.println("itemDroit = "+itemDroit);
				}
				if(j<itemSets.get(i).size()-1) {
					//System.out.println(itemGauche+" ==> "+itemDroit);
					rules.add(itemGauche+" ==> "+itemDroit);
				}
				
				}
			}
		
		for(int i=0; i<rules.size(); i++) {
			System.out.println(rules.get(i));
			control.dataRules.add(new rules(Integer.toString(i+1), rules.get(i)));
		}
		

}