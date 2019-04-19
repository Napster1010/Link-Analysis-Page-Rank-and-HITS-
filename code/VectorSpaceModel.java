package code;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

//Implements vector space model (lnc.ltc)
public class VectorSpaceModel {
	private static final String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};

	public HashMap<String, Double> vectorSpace(final String input, final String directoryPath, int noOfResults) throws Exception{		
		String query = documentTextPreProcessor(input);
		
		File searchDirectory = new File(directoryPath);
		//Initialize document frequency and term frequency hash maps
		HashMap<String, Long> queryDocumentFrequency = new HashMap<>();
		HashMap<String, Long> queryTermFrequency = new HashMap<>();
		StringTokenizer queryTokenizer = new StringTokenizer(query, " ");
		int queryTokens = queryTokenizer.countTokens();
		ArrayList<String> queryTokensList = new ArrayList<>();
		while(queryTokenizer.hasMoreTokens()) {
			String token = queryTokenizer.nextToken();
			queryTokensList.add(token);
			if(queryTermFrequency.containsKey(token)) {
				queryTermFrequency.put(token, queryTermFrequency.get(token) + 1L);
			}else {
				queryTermFrequency.put(token, 1L);
				queryDocumentFrequency.put(token, 0L);				
			}
		}
		
		//Final result
		HashMap<Double, ArrayList<String>> cosineDocMap = new HashMap<>();		
		ArrayList<Double> cosineArrayList = new ArrayList<>();
		
		int documentLength=0;
		//Document frequency algorithm
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		for (File document : searchDirectory.listFiles()) {
			if(!document.isDirectory()) {
				PDDocument pdDocument = PDDocument.load(document);
				String documentText = pdfTextStripper.getText(pdDocument);
				String processedDocumentText = documentTextPreProcessor(documentText);
				StringTokenizer documentTokenizer = new StringTokenizer(processedDocumentText, " .,?");				
				while(documentTokenizer.hasMoreTokens()) {
					String token = documentTokenizer.nextToken();
					if(queryTokensList.contains(token)) {
						queryDocumentFrequency.put(token, queryDocumentFrequency.get(token) + 1L);
						break;
					}
				}			
				++documentLength;
				pdDocument.close();				
			}
		}
		
		//Create tf-raw array for query term frequency
		long[] tfRaw = new long[queryTokens];
		int i=0;
		for(long tf: queryTermFrequency.values()) {
			tfRaw[i] = tf;
			++i;
		}
		
		//Create df array for query
		long[] df = new long[queryDocumentFrequency.size()];
		i=0;
		for(long d: queryDocumentFrequency.values()) {
			df[i] = d;
			++i;
		}
		
		double[] tfWeight = calculateTfWeight(tfRaw);
		double[] idf = calculateIDF(df, documentLength);
		double[] weight = calculateProduct(tfWeight, idf);
		double[] normalizedQuery = calculateNormalizedVector(weight);
		
		
		//Term frequencies for the document
		for(File document: searchDirectory.listFiles()) {
			if(!document.isDirectory()) {
				PDDocument pdDocument = PDDocument.load(document);
				String documentText = pdfTextStripper.getText(pdDocument);
				String processedDocumentText = documentTextPreProcessor(documentText);
				StringTokenizer documentTokenizer = new StringTokenizer(processedDocumentText, " .,?");				

				HashMap<String, Long> documentTermFrequency = new HashMap<>();
				for(int m=0;m<queryTokens;m++) {
					documentTermFrequency.put(queryTokensList.get(m), 0L);
				}
				while(documentTokenizer.hasMoreTokens()) {
					String token = documentTokenizer.nextToken();
					if(queryTokensList.contains(token)) {
						documentTermFrequency.put(token, documentTermFrequency.get(token) + 1L);
					}
				}
				
				
				//Calculations
				//Create tf-raw array for query term frequency
				long[] dtfRaw = new long[queryTokens];
				int k=0;
				for(long tf: documentTermFrequency.values()) {
					dtfRaw[k] = tf;
					++k;
				}
				
				double[] dWeight = calculateTfWeight(dtfRaw);
				double[] normalizedDocument = calculateNormalizedVector(dWeight);
				
				double[] productVector = calculateProduct(normalizedQuery, normalizedDocument);
				double cosine=0.0;
				for(int l=0;l<productVector.length;l++) {
					cosine += productVector[l];
				}
				
				cosineArrayList.add(cosine);
				
				ArrayList<String> newArrList;
				if(cosineDocMap.containsKey(cosine)) {
					newArrList = cosineDocMap.get(cosine);
					newArrList.add(document.getName().substring(0, document.getName().length()-4));
					cosineDocMap.put(cosine, newArrList);
				}else {
					newArrList = new ArrayList<>();
					newArrList.add(document.getName().substring(0, document.getName().length()-4));
					cosineDocMap.put(cosine, newArrList);
				}
				
				pdDocument.close();				
			}
		}
		
		Collections.sort(cosineArrayList, Collections.reverseOrder());
		//Hash map which has to be returned
		HashMap<String, Double> vectorSpaceResult = new LinkedHashMap<>();
		for(Double cosine: cosineArrayList) {
			if(cosine==0.0)
				break;
			
			for(String doc: cosineDocMap.get(cosine)) {
				vectorSpaceResult.put(doc, cosine);
				noOfResults--;
				if(noOfResults<=0)
					break;
			}
			if(noOfResults<=0)
				break;
		}
		
		return vectorSpaceResult;
	}
	
	//Calculate product
	private double[] calculateProduct(double[] arr1, double[] arr2) {
		double[] prodArr = null;
		if(arr1.length == arr2.length) {
			prodArr = new double[arr1.length];
			for(int i=0;i<arr1.length;i++) {
				prodArr[i] = arr1[i]*arr2[i];				
			}
		}		
		return prodArr;
	}
	
	//Calculate normalized vector
	private double[] calculateNormalizedVector(double[] weight) {
		double[] normalizedWeights = new double[weight.length];
		double sumSquares=0.0;
		for(int i=0;i<weight.length;i++) {
			sumSquares += weight[i]*weight[i];
		}
		double normal = Math.sqrt(sumSquares);
		
		if(normal==0.0) {
			for(int i=0;i<weight.length;i++)
				normalizedWeights[i] = 0.0;
		}else {
			for(int i=0;i<weight.length;i++) {
				normalizedWeights[i] = weight[i]/normal;
			}
		}
				
		return normalizedWeights;
	}
	
	//Calculate logarithmic weights
	private double[] calculateTfWeight(long[] tfRaw) {
		double[] tfWeight = new double[tfRaw.length];
		for(int i=0;i<tfRaw.length;i++) {
			if(tfRaw[i] == 0)
				tfWeight[i] = 0;
			else
			tfWeight[i] = 1 + Math.log10(tfRaw[i]);			
		}
		
		return tfWeight;
	}
	
	//Calculate idf
	private double[] calculateIDF(long[] df, long totDocuments) {
		double[] idf = new double[df.length];
		for(int i=0;i<df.length;i++) {
			if(df[i]==0)
				idf[i] = 0;
			else
				idf[i] = Math.log10(totDocuments/df[i]);
		}
		
		return idf;
	}
	
	private String documentTextPreProcessor(String documentText) {
		//Perform case folding
		documentText = documentText.toLowerCase();
		
		//Perform Normalization
		String normalizedDocumentText = Normalizer.normalize(documentText, Normalizer.Form.NFD);
		
		//Create tokens
		ArrayList<String> tokens = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(normalizedDocumentText, " ,.?");
		while(tokenizer.hasMoreTokens())
			tokens.add(tokenizer.nextToken());
		
		//Stop word removal
		for(String stop: stopwords) {
			if(tokens.contains(stop)) {
				tokens.removeAll(Collections.singleton(stop));				
			}
		}

		String returnText="";
		for(String str: tokens) {
			returnText += " " + str;
		}
		
		return returnText.trim();
	}
}
