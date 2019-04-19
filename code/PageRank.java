package code;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class PageRank {
	public void runPageRank(HashMap<String, Double> vectorSpaceOutput, HashMap<String, LinkedList<String>> network, double resetProbability, double maxError, int maxIterations, int noOfResults, String outputPath) throws Exception {		
		//Create and initialize the page ranks of all nodes to 1/N where N is the number of nodes in the network
		HashMap<String, Double> curPageRanks = new HashMap<>();
		HashMap<String, Double> prevPageRanks = new HashMap<>();
		double startPageRank = (double)1/network.size();
		for(String node: network.keySet()) {
			curPageRanks.put(node, startPageRank);
			prevPageRanks.put(node, startPageRank);
		}
		int noOfIterations=0;
		//In each iteration, compute and update the page ranks of all nodes
		while(maxIterations>0) {
			++noOfIterations;
			
			//Calculate page ranks of each node
			for(String node: network.keySet()) {
				double curPageRank=resetProbability/network.size();
				for(String itr: network.keySet()) {
					if(!itr.equals(node)) {
						if(network.get(itr).contains(node)) {
							curPageRank += curPageRanks.get(itr)/network.get(itr).size();
						}
					}
				}
				curPageRank *= (1-resetProbability);
				curPageRanks.put(node, curPageRank);
			}
			
			//Calculate errors
			boolean exit=false;
			for(String nodeC: curPageRanks.keySet()) {
				double newVal = curPageRanks.get(nodeC);
				double prevVal = prevPageRanks.get(nodeC);
				double error = Math.abs(newVal - prevVal);
				if(error<=maxError)
					exit = true;
				else 
					exit = false;				
			}
			if(exit)
				break;
			
			prevPageRanks.putAll(curPageRanks);
			--maxIterations;
		}
		
		//Sort the page ranks and return top <noOfResults> results
		HashMap<Double, ArrayList<String>> map = new HashMap<>();
		ArrayList<Double> pageRanks = new ArrayList<>();
		for(String str: vectorSpaceOutput.keySet()) {
			pageRanks.add(curPageRanks.get(str));
			if(map.containsKey(curPageRanks.get(str))) {
				map.get(curPageRanks.get(str)).add(str);
			}else {
				ArrayList<String> names = new ArrayList<>();
				names.add(str);
				map.put(curPageRanks.get(str), names);
			}				
		}
		
		Collections.sort(pageRanks, Collections.reverseOrder());
		
		//Write the top results to output file
		PrintWriter pw = new PrintWriter(new FileWriter(outputPath, true));
		pw.println("\n===========================================================================================================\n");
		pw.println("Output of PageRank (Reset Probability = " + resetProbability +", Margin of error = " + maxError + ")");
		pw.println("-----------------------------------------------------------------------\n");
		pw.println("Document name\t\tPageRank");
		pw.println("-------------\t\t--------");
		for(Double rank: pageRanks) {
			for(String doc: map.get(rank)) {
				pw.println(doc+"\t\t"+rank);
				--noOfResults;
				if(noOfResults<=0)
					break;
			}
			if(noOfResults<=0)
				break;
		}
		pw.close();
	}
}
