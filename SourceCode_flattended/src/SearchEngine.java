import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

	public class SearchEngine {
		
	public static void main(String[] args) throws IOException {
		
		String y;	
		do {
		System.out.print("Enter search query> ");
		@SuppressWarnings("resource")
		Scanner kb = new Scanner(System.in);
		String search = kb.nextLine();
			
		String [] forAnd = search.split(" AND ");	
		String [] forOr = search.split(" OR ");		
		String [] forNot = search.split(" ");

		if ((search.indexOf("NOT") != -1) && (search.indexOf("AND") != -1)){
			searchForAkeyNOTAND(forNot[1].toLowerCase(), forAnd[1].toLowerCase());
		}
		else if(search.indexOf("OR") != -1) {
			searchForAkeyOR(forOr[0].toLowerCase(), forOr[1].toLowerCase());
		}
		else if (search.indexOf("NOT") != -1) {
			searchForAkeyNOT(forNot[1].toLowerCase());
		}
		else if(search.indexOf("AND") != -1) {
			searchForAkeyAND(forAnd[0].toLowerCase(), forAnd[1].toLowerCase());
		}
		else {
			search = search.toLowerCase();
			searchForAkey(search);
		}
			System.out.print("Do you want to search again? (y/n) ");
			y = kb.nextLine();
			
		} while(y.matches("y"));
		
		System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT+"Thank you for using my Search Engine ... :)"+ ConsoleColors.RESET);
	}

	// prepare filesList array to go through them..
	public static File[] getAllFiles() {
		final File path = new File("src/files/");
		File[] filesArray = path.listFiles();
		return filesArray;
	}
	
	/* This method is response for putting each files' words in a single hashMap and create array contains all
 	hashMaps of the files .. */
	public static HashMap<String, Double>[] arrayOfHashes() throws FileNotFoundException {
		
		File [] files = getAllFiles();

		@SuppressWarnings("unchecked")
		HashMap<String, Double>[] arrayOfHashes = new HashMap[getAllFiles().length]; // array of hashMap 
		for (int i = 0; i < arrayOfHashes.length; i++) {
			arrayOfHashes[i] = new HashMap<String, Double>();
			@SuppressWarnings("resource")
			Scanner input = new Scanner(new File(files[i] + ""));
			String nWord = "";
			while (input.hasNext()) {
				nWord = input.next().toLowerCase().replaceAll("[^a-zA-Z]", "");
				nWord = removeStopword(nWord);
				if (nWord.length() > 2) {
					
					Double occurence = arrayOfHashes[i].get(nWord);
					
					if (occurence == null)
						occurence = 1.0;
					else 
						occurence++;
					arrayOfHashes[i].put(nWord, occurence);
 
				}
			}
				    
			// to get the relative frequency and put it in hashmap
			for (Entry<String, Double> hashMap : arrayOfHashes[i].entrySet()) {
				Double occurence = hashMap.getValue();
				arrayOfHashes[i].put(hashMap.getKey(), (occurence)/arrayOfHashes[i].size());
			} 
						
		}	
		return arrayOfHashes;
	}
	
	//this method is response for extracting a sample text based on searchKey
	public static String getTerms(int index, String key) throws IOException { 
		File [] files = getAllFiles();
		key = key.toLowerCase();
		@SuppressWarnings("resource")
		BufferedReader bf = new BufferedReader(new FileReader(files[index]));
		String sentence = bf.readLine();
		//int indexOfWord = sentence.toLowerCase().indexOf(" " + key + " " );
		
		 Pattern p = Pattern.compile("\\b"+key+"\\b");
	      Matcher matcher = p.matcher(sentence.toLowerCase());
	      matcher.find();
	      int indexOfWord = matcher.end();
		
		String Fsentence = sentence.toLowerCase().substring((indexOfWord-20)<0?0:indexOfWord-20, 
				(indexOfWord+20)>sentence.length()? sentence.length():indexOfWord+20);
		
		String searchKey = ConsoleColors.YELLOW_BACKGROUND_BRIGHT+ /*"'"+*/ key /*+"'"*/ +ConsoleColors.RESET;
		
		Fsentence = Fsentence.replaceAll(key, searchKey);
		
		return "..." + Fsentence +  "...";
		}
	
	public static String removeStopword(String s) {
    	Pattern p = Pattern.compile("\\b(the|and|or|of|for|to|be)\\b\\s?");
        Matcher m = p.matcher(s);
        String s1 = m.replaceAll("");
        return s1;
    }

	//single query 
	public static void searchForAkey(String searchKey) throws IOException {
		System.out.println("(Single term query: " + ConsoleColors.PURPLE  + searchKey + ConsoleColors.RESET+")");
		
		File [] files = getAllFiles();
		ArrayList<QueryWord> result = new ArrayList<QueryWord>();
		
		long startTime = System.currentTimeMillis();
		int total = 0;
		HashMap<String, Double>[] arrayOfHashes2 = arrayOfHashes();
		for (int j = 0; j < files.length; j++) {
			HashMap<String, Double> arrayOfHashes = arrayOfHashes2[j];
			if(arrayOfHashes.containsKey(searchKey)) {
				total += 1;
				double occurs =  arrayOfHashes.get(searchKey);
			result.add(new QueryWord(occurs,"Found in "+ files[j].getName() +
					/*" | Relative Frequncy = "+ occurs + */ "\n"  + getTerms(j,searchKey) ));
			}
		}
		System.out.println("the term is found in " + total + " files");
		Collections.sort(result);
		int i = 1;
		for (QueryWord q : result) {
			System.out.println(i+ "-"+ q.toStringOne());
			i++;
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The Calculated time for the O.P. is " + ConsoleColors.CYAN +totalTime/1000.0 + "s" + ConsoleColors.RESET+"\n");			
	}
	
	//Not query
	public static void searchForAkeyNOT(String searchKey) throws FileNotFoundException{
		System.out.println("(NOT queries: " + ConsoleColors.PURPLE  + "!"+searchKey+ ConsoleColors.RESET+")");
		
		File [] files = getAllFiles();
		long startTime = System.currentTimeMillis();
		int total = 0;
		HashMap<String, Double>[] arrayOfHashes2 = arrayOfHashes();
		for (int j = 0; j < files.length; j++) {
			HashMap<String, Double> arrayOfHashes = arrayOfHashes2[j];
			if(!arrayOfHashes.containsKey(searchKey)) {
				total += 1;
				System.out.println(total + "-NOT Found in "+ files[j].getName()); // File index for the word
			}
		}
		System.out.println("NOT Found in " + total + " files");
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The Calculated time for the O.P. is " + ConsoleColors.CYAN +totalTime/1000.0 + "s" + ConsoleColors.RESET+"\n");		
	}
	
	//Not-AND query 
	public static void searchForAkeyNOTAND(String searchKey1, String searchKey2) throws FileNotFoundException{
		System.out.println("(NOT/AND queries: " + ConsoleColors.PURPLE+ "!("+searchKey1 +" && "+searchKey2+ ConsoleColors.RESET+ "))");
		
		File [] files = getAllFiles();

		long startTime = System.currentTimeMillis();
		int total = 0;
		HashMap<String, Double>[] arrayOfHashes2 = arrayOfHashes();
		for (int j = 0; j < files.length; j++) {
			HashMap<String, Double> arrayOfHashes = arrayOfHashes2[j];
			if(!(arrayOfHashes.containsKey(searchKey1)&&arrayOfHashes.containsKey(searchKey2))){
				total += 1;
				System.out.println(total + "-NOT Found in "+ files[j].getName()); // File index for the word
			}
		}
		System.out.println("NOT Found in " + total + " files");
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The Calculated time for the O.P. is " + ConsoleColors.CYAN +totalTime/1000.0 + "s" + ConsoleColors.RESET+"\n");		
	}
	//AND query 
	public static void searchForAkeyAND(String searchKey1, String searchKey2) throws IOException{
		System.out.println("(AND queries: "+ ConsoleColors.PURPLE+ searchKey1 + " && " + searchKey2+ ConsoleColors.RESET+")");
		
		File [] files = getAllFiles();
		ArrayList<QueryWord> result = new ArrayList<QueryWord>();

		
		long startTime = System.currentTimeMillis();
		int total = 0;
		HashMap<String, Double>[] arrayOfHashes2 = arrayOfHashes();
		for (int j = 0; j < files.length; j++) {
			HashMap<String, Double> arrayOfHashes = arrayOfHashes2[j];
			if(arrayOfHashes.containsKey(searchKey1) && arrayOfHashes.containsKey(searchKey2)) {
				total += 1;
				double occurs =  arrayOfHashes.get(searchKey1)*arrayOfHashes.get(searchKey2);

				result.add(new QueryWord(occurs ,"Found in "+ files[j].getName() + 
			/*" | Relative Frequncy = "+ occurs + */ "\n" + getTerms(j,searchKey1) + " | " ,  getTerms(j,searchKey2)));		
			}
		}
		System.out.println("the terms are found in " + total + " files");
		Collections.sort(result);
		int i = 1;
		for (QueryWord q : result) {
			System.out.println(i +"-"+q.toStringTwo());
			i++;
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The Calculated time for the O.P. is " + ConsoleColors.CYAN +totalTime/1000.0 + "s" + ConsoleColors.RESET+"\n");		
	}
	
	//OR query 
	public static void searchForAkeyOR(String searchKey1, String searchKey2) throws IOException{
		System.out.println("(OR queries: "+ ConsoleColors.PURPLE+ searchKey1 + " || " + searchKey2+ ConsoleColors.RESET+")");	
		File [] files = getAllFiles();	
		ArrayList<QueryWord> resultS = new ArrayList<QueryWord>();
		ArrayList<QueryWord> resultC = new ArrayList<QueryWord>();

		double occurs = 0.0;
		long startTime = System.currentTimeMillis();
		int total = 0;
		HashMap<String, Double>[] arrayOfHashes2 = arrayOfHashes();
		for (int j = 0; j < files.length; j++) {
			HashMap<String, Double> arrayOfHashes = arrayOfHashes2[j];
			if(arrayOfHashes.containsKey(searchKey1) && arrayOfHashes.containsKey(searchKey2)){
				 occurs =  arrayOfHashes.get(searchKey1) + arrayOfHashes.get(searchKey2);
				 total += 1;
				 resultC.add(new QueryWord(occurs ,"Found in "+ files[j].getName() + /*" | Relative Frequncy = "+ occurs +*/
						"\n"+ getTerms(j,searchKey1) + " | " ,  getTerms(j,searchKey2))); 
			} 
			if(arrayOfHashes.containsKey(searchKey1)){
				total += 1;
				 occurs =  arrayOfHashes.get(searchKey1);
				 resultS.add(new QueryWord(occurs ,"Found in "+ files[j].getName() + /*" | Relative Frequncy = "+ occurs + */
						"\n" + getTerms(j,searchKey1)));
			}
			if (arrayOfHashes.containsKey(searchKey2)) {
				total += 1;
				 occurs =  arrayOfHashes.get(searchKey2);
				 resultS.add(new QueryWord(occurs ,"Found in "+ files[j].getName() + /*" | Relative Frequncy = "+ occurs + */
						"\n" + getTerms(j,searchKey2)));
			}
		}
		System.out.println("the terms are found in " + total + " files");
		System.out.println("Separate Files: ");
		Collections.sort(resultS);
		int i = 1;
		for (QueryWord q : resultS) {
			System.out.println(i +"-"+ q.toStringOne());
			i++;
		}
		System.out.println("Common Files: ");
		Collections.sort(resultC);
		int j = 1;
		for (QueryWord q : resultC) {
			System.out.println(j +"-"+ q.toStringTwo());
			j++;
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The Calculated time for the O.P. is " + ConsoleColors.CYAN +totalTime/1000.0 + "s" + ConsoleColors.RESET+"\n");		
	}
}
	
class QueryWord implements Comparable<Object> {
	private double rFrequncy;
	private String sentenceOne;
	private String sentenceTwo;
	//private int relativeF;
	
	public QueryWord(double occurence, String sentenceOne){
		this.rFrequncy = occurence;
		this.sentenceOne = sentenceOne;
	}
	
	public QueryWord(double occurence, String sentenceOne, String sentenceTwo){
		this.rFrequncy = occurence;
		this.sentenceOne = sentenceOne;
		this.sentenceTwo = sentenceTwo;
	}
	
	
//	public void setRelativeFrequncy(int count) {
//		relativeF = occurence*1000/count;
//	}
//	
//	public int getRelativeFrequncy() {
//		return this.relativeF;
//	}
	
	public String getSentence() {
		return this.sentenceOne;
	}

	@Override
	public int compareTo(Object o) {
		QueryWord p = (QueryWord) o; 
		if(this.rFrequncy > p.rFrequncy)
			return -1;
		else if(this.rFrequncy == p.rFrequncy)
			return 0;
		else
			return 1;
	}
	
	public String toStringOne() {
		return sentenceOne;
		
	}
	
	public String toStringTwo() {
		return sentenceOne + "" + sentenceTwo ;
		
	}
}