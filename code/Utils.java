package code;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public class Utils {
	public void writeOutput(HashMap<String, Double> map, String outputPath) throws Exception{
		PrintWriter pw = new PrintWriter(outputPath);
		pw.println("Vector Space Model output");
		pw.println("-------------------------");
		pw.println("Document name\t\tCosine value");
		pw.println("-------------\t\t------------");
		for(String key: map.keySet()) {
			pw.println(key+"\t\t"+map.get(key));
		}
		pw.close();
	}
}
