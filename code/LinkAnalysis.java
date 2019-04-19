package code;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The code compares link analysis algorithms PageRank and HITS
 
 * Group Members:
 * 1) Varun Sridhar - 1610110427
   2) Aditya Tiwari - 1610110046
 */
public class LinkAnalysis{

    private static final String dataSetPath="D:\\SEM 6\\Information Retrieval\\Project\\DataSet";
    private static final String outputPath="D:\\SEM 6\\Information Retrieval\\Project\\Output\\output.txt";
    private static final String linkPath="D:\\SEM 6\\Information Retrieval\\Project\\DataSet\\Links\\Dataset - PR and HITS.xlsx";

    private static final int noOfResults = 10;
    private static final double resetProbability = 0.15;
    private static final double error = 0.00001;
    private static final int maxPageRankIterations = 100;
    private static final int maxHitsItearions = 5;
    
    private static HashMap<String, LinkedList<String>> network = new LinkedHashMap<>();
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Utils util = new Utils();
        
        //Form the document link network
        try {
            formLinkNetwork();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        //Input the query
        System.out.println("Enter the query");
        String query = sc.nextLine();
        
        //Pass the query to vector space model
        VectorSpaceModel vectorSpaceModel = new VectorSpaceModel();
        try {
        	System.out.println("\nRunning vector space model....");
        	
            HashMap<String, Double> vectorSpaceOutput = vectorSpaceModel.vectorSpace(query, dataSetPath, noOfResults);            
            //Write the vector space output to file
            util.writeOutput(vectorSpaceOutput, outputPath);
            
            //Run page rank
        	System.out.println("\nRunning PageRank....");
            PageRank pageRank = new PageRank();
            pageRank.runPageRank(vectorSpaceOutput, network, resetProbability, error, maxPageRankIterations, noOfResults, outputPath);        	
            
            System.out.println("Running HITS algorithm....");
            //Run HITS algorithm
            Set<String> nodes = vectorSpaceOutput.keySet();
            List<String> rootSet = new ArrayList<>();
            for(String str: nodes) {
            	rootSet.add(str);
            }
            HITS hits = new HITS();
            hits.runHits(network, rootSet, maxHitsItearions, outputPath, noOfResults);            
            
		} catch (Exception e) {
			System.out.println("Some error occurred while running the vector space model or HITS algorithm !!");
			e.printStackTrace();
		}
    }
    
    private static void formLinkNetwork() throws Exception {
    	
    	
    	//Read the Excel file
    	XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(linkPath)));
    	Iterator<Row> rowIterator = workbook.getSheetAt(0).rowIterator();
    	while(rowIterator.hasNext()) {
    		Row row = rowIterator.next();
    		
    		Iterator<Cell> cellIterator = row.cellIterator();
    		int currCell=0;
    		String from="", to="";
    		while(cellIterator.hasNext()) {
    			Cell cell = cellIterator.next();    			
    			if(currCell==0) {
    				from = cell.getStringCellValue();
    				++currCell;
    			}else {
    				to = cell.getStringCellValue();
    			}
    		}
    		
    		if(network.containsKey(from)) {
    			if(!network.get(from).contains(to))
    				network.get(from).add(to);
    		}else {
    			LinkedList<String> fresh = new LinkedList<>();
    			fresh.add(to);
    			network.put(from, fresh);
    		}

    		if(!network.containsKey(to)) {
				LinkedList<String> list = new LinkedList<>();
				network.put(to, list);
			}
    	}
    	workbook.close();
    }
}


















