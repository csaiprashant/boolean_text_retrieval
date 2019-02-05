import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.io.*;
import java.util.*;

public class BooleanRetrieval {
	
	HashMap<String, Set<Integer>> invIndex;
	int [][] docs;
	HashSet<String> vocab;
	HashMap<Integer, String> map;  // int -> word
	HashMap<String, Integer> i_map; // inv word -> int map

	public BooleanRetrieval() throws Exception{
		// Initialize variables and Format the data using a pre-processing class and set up variables
		invIndex = new HashMap<String, Set<Integer>>();
		DatasetFormatter formater = new DatasetFormatter();
		formater.textCorpusFormatter("./all.txt");
		docs = formater.getDocs();
		vocab = formater.getVocab();
		map = formater.getVocabMap();
		i_map = formater.getInvMap();
	}

	void createPostingList(){
		//Initialze the inverted index with a SortedSet (so that the later additions become easy!)
		for(String s:vocab){
			invIndex.put(s, new TreeSet<Integer>());
		}
		//for each doc
		for(int i=0; i<docs.length; i++){
			//for each word of that doc
			for(int j=0; j<docs[i].length; j++){
				//Get the actual word in position j of doc i
				String w = map.get(docs[i][j]);
				
				/*Get the existing posting list for this word w and add the new doc in the list. 
				Keep in mind doc indices start from 1, we need to add 1 to the doc index , i
				 */

				TreeSet<Integer> PostingList_a = (TreeSet<Integer>)invIndex.get(w);
				PostingList_a.add(i + 1);
				invIndex.put(w, PostingList_a);
			}
		}
	}

	Set<Integer> intersection(Set<Integer> a, Set<Integer> b){
		/*
		First convert the posting lists from sorted set to something we 
		can iterate easily using an index. I choose to use ArrayList<Integer>.
		One can also use other enumerable.
		 */
		ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
		ArrayList<Integer> PostingList_b = new ArrayList<Integer>(b);
		TreeSet result = new TreeSet();

		//Set indices to iterate two lists. I use i, j
		int i = 0;
		int j = 0;

		while(i!=PostingList_a.size() && j!=PostingList_b.size()){
		
			//Implement the intersection algorithm
			int x = PostingList_a.get(i);
			int y = PostingList_b.get(j);
			if(x == y){
				result.add(x);
				i++;
				j++;
			}
			else if(x < y)
				i++;
			else
				j++;
		}
		return result;
	}

	Set <Integer> evaluateANDQuery(String a, String b){
		return intersection(invIndex.get(a), invIndex.get(b));
	}

	Set<Integer> union(Set<Integer> a, Set<Integer> b){

		ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
		ArrayList<Integer> PostingList_b = new ArrayList<Integer>(b);
		TreeSet result = new TreeSet();
		// Implement Union here
		int i = 0;
		int j = 0;

		while(i!=PostingList_a.size() && j!=PostingList_b.size()){
			int x = PostingList_a.get(i);
			int y = PostingList_b.get(j);
			if(x == y){
				result.add(x);
				i++;
				j++;
			}
			else if(x < y){
				result.add(x);
				i++;
			}
			else{
				result.add(y);
				j++;
			}
		}

		if (i == PostingList_a.size()){
			while (j != PostingList_b.size()){
				int u = PostingList_b.get(j);
				result.add(u);
				j++;
			}
		}
		else{
			while (i != PostingList_a.size()){
				int v = PostingList_a.get(i);
				result.add(v);
				i++;
			}
		}
		return result;
	}

	Set <Integer> evaluateORQuery(String a, String b){
		return union(invIndex.get(a), invIndex.get(b));
	}
	
	Set<Integer> not(Set<Integer> a){
		TreeSet result = new TreeSet();
		/*
		 First convert the posting lists from sorted set to something we 
		 can iterate easily using an index. I choose to use ArrayList<Integer>.
		 One can also use other enumerable.
		*/
		ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
		int total_docs = docs.length;
		// Implement the not method

		int i = 0;		
		while(i < PostingList_a.size()){
			if(i == 0){
				for(int x = 1; x < PostingList_a.get(i + 1); x++)
					result.add(x);
				result.remove(PostingList_a.get(i));
			}
			else if(i == PostingList_a.size() - 1){
				for(int x = PostingList_a.get(i) + 1; x <= total_docs; x++)
					result.add(x);
			}
			else{
				for(int x = PostingList_a.get(i) + 1; x < PostingList_a.get(i + 1); x++)
					result.add(x);
			}
			i++;
		}
		return result;
	}

	Set <Integer> evaluateNOTQuery(String a){
		return not(invIndex.get(a));
	}
	
	Set <Integer> evaluateAND_NOTQuery(String a, String b){
		return intersection(invIndex.get(a), not(invIndex.get(b)));
	}
	public static void main(String[] args) throws Exception {

		//Initialize parameters
		BooleanRetrieval model = new BooleanRetrieval();

		//Generate posting lists
		model.createPostingList();

		//Print the posting lists from the inverted index
		
		System.out.println("\nPrinting posting list:");
		for(String s : model.invIndex.keySet()){
			System.out.println(s + " -> " + model.invIndex.get(s));	
		}
		
		//Print test cases

		System.out.println();
		
		System.out.println("\nTesting AND queries \n");
		System.out.println("1) " + model.evaluateANDQuery("mouse", "keyboard"));
		System.out.println("2) " + model.evaluateANDQuery("mouse", "wifi"));
		System.out.println("3) " + model.evaluateANDQuery("button", "keyboard"));
				
		System.out.println("\nTesting OR queries \n");
		System.out.println("4) " + model.evaluateORQuery("wifi", "scroll"));
		System.out.println("5) " + model.evaluateORQuery("youtube", "reported"));
		System.out.println("6) " + model.evaluateORQuery("errors", "report"));
		
		System.out.println("\nTesting AND_NOT queries \n");
		System.out.println("7) " + model.evaluateAND_NOTQuery("mouse", "scroll"));
		System.out.println("8) " + model.evaluateAND_NOTQuery("scroll", "mouse"));
		System.out.println("9) " + model.evaluateAND_NOTQuery("lenovo", "logitech"));
		

		Scanner scan = new Scanner(System.in);
		String queryType = args[0];
		String outputFile = args[args.length - 1];
		
		if(queryType.equals("PLIST")){
			String queryString=args[1];
			PrintStream o = new PrintStream(new File(outputFile));
            System.setOut(o);
            System.out.println(queryString+" "+"->"+model.invIndex.get(queryString));
        }		
		else if(queryType.equals("AND")){
			String word1=args[1];
			String word2=args[2];
            PrintStream o = new PrintStream(new File(outputFile));
            System.setOut(o);
            System.out.println(word1+" "+"AND"+" "+word2+" "+"->"+model.evaluateANDQuery(word1,word2));
		}        		
        else if(queryType.equals("OR")){
			String word1=args[1];
			String word2=args[2];
            PrintStream o = new PrintStream(new File(outputFile));
            System.setOut(o);
            System.out.println(word1+" "+"OR"+" "+word2+" "+"->"+model.evaluateORQuery(word1,word2));
		}	       
		else if(queryType.equals("AND-NOT")){
			String word1=args[1];
			String word2=args[2];
			PrintStream o = new PrintStream(new File(outputFile));
            System.setOut(o);
            System.out.println(word1+" "+"AND-NOT"+" "+word2+" "+"->"+model.evaluateAND_NOTQuery(word1,word2));
		}
		else			
	    	System.out.println("\n Please enter a valid command");

		scan.close();
	}
}