package code;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class HITS {
	
	private HashMap<String, LinkedList<String>> baseSet;
	private List<String> baseSetDocs;
	
	public void runHits(HashMap<String, LinkedList<String>> network, List<String> rootSet, int noOfItearions, String outputPath, int noOfResults) throws Exception {
		//Initialize the adjacency list
		baseSet = new HashMap<>();
		LinkedList<String> baseSetDocs = new LinkedList<>();
		baseSetDocs.addAll(rootSet);
		//Add the nodes which are pointed to by root set in the base set
		for(String str: rootSet) {
			String node = str;
			baseSet.put(node, new LinkedList<>());
			baseSetDocs.add(node);
			LinkedList<String> nodes = network.get(str);			
			for(String doc: nodes) {
				baseSet.get(node).add(doc);
				LinkedList<String> docList = new LinkedList<>();
				baseSet.put(doc, docList);
			}
		}
		//Add the nodes which are pointing to the root set in the baseDocs list
		for(String key: network.keySet()) {
			LinkedList<String> nodes = network.get(key);
			for(String str: rootSet) {
				if(nodes.contains(str)) {
					if(!baseSetDocs.contains(key)) {
						baseSetDocs.add(key);
						String node = key;
						LinkedList<String> list = new LinkedList<>();
						list.add(str);
						baseSet.put(node, list);
					}else {
						baseSet.get(key).add(str);
					}
				}
			}
		}		
		//Adding the interlinks between hubs
		for(String node: baseSetDocs) {
			if(!rootSet.contains(node)) {
				LinkedList<String> links = network.get(node);
				for(String str: links) {
					if(baseSetDocs.contains(str)) {
						if(!baseSet.get(node).contains(str))
							baseSet.get(node).add(str);
					}
				}
			}
		}
		
		//Create and initialize the authority and hub score hash map.
		//Put the initial scores to be 1
		HashMap<String, Double> authScores = new HashMap<>(); 
		HashMap<String, Double> hubScores = new HashMap<>(); 
		for(String node: baseSet.keySet()) {
			authScores.put(node, 1.0);
			hubScores.put(node, 1.0);
		}
		
		//Now the base set is built
		//Calculate hub and authority score for every node in each iteration
		while(noOfItearions>0) {
			
			//Calculate the authority scores
			for(String node: baseSet.keySet()) {				
				double authScore = authScores.get(node);
				for(String in: baseSet.keySet()) {
					if(!in.equals(node)) {
						if(baseSet.get(in).contains(node)) {
							authScore += hubScores.get(in);
						}
					}
				}
				authScores.put(node, authScore);
			}
			
			//Normalize the authority scores
			normalizeMap(authScores);
			
			//Calculate the hub scores
			for(String node: baseSet.keySet()) {
				double hubScore = hubScores.get(node);				
				for(String out: baseSet.get(node)) {
					hubScore += authScores.get(out);
				}
				hubScores.put(node, hubScore);
			}
			
			//Normalize the hub scores
			normalizeMap(hubScores);
			
			--noOfItearions;
		}		
		
		System.out.println("\nRoot Set:");
		System.out.print(rootSet+"\n\nBase set:\n");
		System.out.println(baseSet);
		System.out.println("\nAuthority scores: " + authScores);
		System.out.println("Hub scores: " + hubScores);
		
		//Sort the authScores
		//Sort the page ranks and return top <noOfResults> results
		HashMap<Double, ArrayList<String>> map = new HashMap<>();
		ArrayList<Double> authScoresList = new ArrayList<>();
		for(String str: authScores.keySet()) {
			authScoresList.add(authScores.get(str));
			if(map.containsKey(authScores.get(str))) {
				map.get(authScores.get(str)).add(str);
			}else {
				ArrayList<String> names = new ArrayList<>();
				names.add(str);
				map.put(authScores.get(str), names);
			}				
		}
		
		Collections.sort(authScoresList, Collections.reverseOrder());
		//Write the top results to output file
		PrintWriter pw = new PrintWriter(new FileWriter(outputPath, true));
		pw.println("\n===========================================================================================================\n");
		pw.println("Output of HITS");
		pw.println("------------------\n");
		pw.println("Document name\t\tAuthority score");
		pw.println("-------------\t\t---------------");
		for(Double score: authScoresList) {
			for(String doc: map.get(score)) {
				pw.println(doc+"\t\t"+score);
				--noOfResults;
				if(noOfResults<=0)
					break;
			}
			if(noOfResults<=0)
				break;
		}
		pw.close();
	}
	
	private void normalizeMap(HashMap<String, Double> map) {
		double sumSquare=0.0, norm=0.0;
		for(String node: map.keySet()) {
			double score = map.get(node);
			sumSquare += score*score;
		}
		norm = Math.sqrt(sumSquare);
		for(String node: map.keySet()) {
			double curScore = map.get(node);
			double newScore = curScore/norm;
			map.put(node, newScore);
		}		
	}
}

