import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Clustering {

	public static void main(String[] args) { 
		String filename = "input.txt";
		File inputFile = new File(filename); 
		if (!inputFile.canRead()) {
            		System.out.println("Required input file " + filename + " not found; exiting.\n" + inputFile.getAbsolutePath());
            		System.exit(1);
       		}
		try {
			Scanner input = new Scanner(inputFile);
			ArrayList<String[]> lines = new ArrayList<String[]>(); 
			ArrayList<String> fullLine = new ArrayList<String>();
			readInput(input, lines, fullLine); 
			HashMap<ArrayList<Integer>, Integer> sorted = sort(lines, fullLine); 
			PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
			createOutput(sorted, output, lines, fullLine); 
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/* Reads in each line of input, stores each full line in the ArrayList fullLine.
	 * Splits just the sentence into an array of words, and stores each in the ArrayList lines
	 * for easy comparison. After method executes, fullLine will contain every line of the file 
	 * and lines will contain an ArrayList of arrays of Strings containing each word of each sentence. 
	 * Assumes that the first two elements on the line that are separated by spaces will be the date and time.
	 * The index of each sentence is the same in lines and fullLine. 
	 * 
	 * @param input a scanner over the input file
	 * @param lines an ArrayList<String[]> to put all sentences of the input into, separated by word
	 * @param fullLine an ArrayList<String> to put all lines of the input into
	 */
	public static void readInput(Scanner input, ArrayList<String[]> lines, ArrayList<String> fullLine) { 
		while(input.hasNextLine()) { 
			String line = input.nextLine();
			fullLine.add(line);
			String[] lineArray = line.split(" "); 
			String[] arrWithoutDate = Arrays.copyOfRange(lineArray, 2, lineArray.length); //Assumes first two words are date and time
			lines.add(arrWithoutDate);
		}
	}
	
	/* Takes in an ArrayList<String> containing the lines of a file, and an ArrayList<String[]> containing
	 * the sentences in those lines but divided by word. Then, it groups together similar sentences 
	 * (sentences where only a single word has changed) and extracts the changes. 
	 * 
	 * @param lines an ArrayList<String[]> containing all sentences of the input, separated by word
	 * @param fullLine an ArrayList<String> containing all lines of the input
	 * @returns a HashMap that maps an ArrayList<Integer> to Integer. The key contains a list of the indices 
	 * 			in lines and fullLine of all sentences that match a pattern, and the value is the index of the word 
	 * 			in the sentences that changes.
	 * */
	public static HashMap<ArrayList<Integer>, Integer> sort(ArrayList<String[]> lines, ArrayList<String> fullLine) { 
		HashMap<ArrayList<Integer>, Integer> sorted = new HashMap<ArrayList<Integer>, Integer>(); 		
		//Go through each line in lines, compare it to each other line. 
		//Keep track of a map of Set<Integer> to Integer where each set 
		//contains the indices of sentences that differ by only the word 
		//at index of the Integer it maps to. 
		for(int i = 0; i < lines.size(); i++) { 
			String[] line = lines.get(i); 
			HashMap<ArrayList<Integer>, Integer> currMap = new HashMap<ArrayList<Integer>, Integer>();  
			for(int j = i; j < lines.size(); j++) { 
				String[] currLine = lines.get(j); 
				int ind = checkStrings(line, currLine); 
				if(ind >= 0) { 
					if(!currMap.containsValue(ind)) { 
						ArrayList<Integer> newSet = new ArrayList<Integer>(); 
						newSet.add(i); 
						newSet.add(j); 
						currMap.put(newSet, ind); 
						
					} else { 
						for(ArrayList<Integer> set : currMap.keySet()) { 
							if(currMap.get(set) == ind) { 
								set.add(j); 
								break; 
							}
						}
					}
				} 
			}
			//Add all of this sentences map to the overall map. First check if the set already contains the 
			//integer we are mapping to, and if it does, check if the set is the same. If not, add this mapping. 
			//if yes, don't add this mapping. 
			for(ArrayList<Integer> key: currMap.keySet()) { 
				//Check if this is already part of a set in the overall sorted map. 
				//If it is, we don't want to add it again
				for(ArrayList<Integer> sortKey : sorted.keySet()) { 
					if(sortKey.containsAll(key)) { 
						currMap.remove(key);
						break;
					}
				}
				//If this set is not already in sorted, we want to add it to sorted
				if(currMap.containsKey(key)) { 
					sorted.put(key, currMap.get(key)); 
				}
			}
		}		 		
		return sorted; 
	}
	
	
	
	/* Checks two arrays of Strings to see how many differences there are between 
	 * the two arrays. 
	 * 
	 * @param sArray the first array of strings to check against the other
	 * @param lineArray the second array of strings to check against the first
	 * @returns the index of the difference between the two arrays if there is exactly 
	 * one string different between the two. If there are no differences or more than one 
	 * difference, returns -1. 
	 */
	public static int checkStrings(String[] sArray, String[] lineArray) { 
		int diffs = 0; 
		int index = -1;
		if(sArray.length == lineArray.length) { 
			for(int i = 0; i < sArray.length && i < lineArray.length; i++) { 
				if(!sArray[i].equals(lineArray[i])) { 
					diffs++; 
					index = i; 
				}
			}
			if(diffs == 1) { 
				return index;
			}
		} 
		return -1;
	}
	
	/* Takes in a HashMap<ArrayList<Integer>, Integer> that contains data about matching lines,
	 * a PrintWriter that will write to an output file, an ArrayList<String[]> containing sentences
	 * of a file separated by word, and an ArrayList<String> containing full lines of a file. Writes
	 * to an output file the groupings of sentences where only a single word has changed and the 
	 * differences between them.
	 * 
	 * @param lines an ArrayList<String[]> containing all sentences of the input, separated by word
	 * @param fullLine an ArrayList<String> containing all lines of the input
	 * @param sorted contains data about groupings of sentences. The key contains a list of the indices 
	 * 			in lines and fullLine of all sentences that match a pattern, and the value is the index
	 * 			of the word in the sentences that changes.
	 * @param output PrintWriter to create the output file
	 * */
	public static void createOutput(HashMap<ArrayList<Integer>, Integer> sorted, PrintWriter output, 
			ArrayList<String[]> lines, ArrayList<String> fullLine) throws IOException { 
		String chgStr = "The changing word was ";
		for(ArrayList<Integer> group : sorted.keySet()) { 
			for(Integer i: group) { 
				String toWrite = fullLine.get(i)+ " \n";
				output.write(toWrite, 0, toWrite.length());
			} 
			output.write(chgStr, 0, chgStr.length());
			for(int i = 0; i < group.size()-1; i++) { 
				String toWrite = lines.get(group.get(i))[sorted.get(group)] + ", ";
				output.write(toWrite, 0, toWrite.length());
			}
			String toWrite = lines.get(group.get(group.size()-1))[sorted.get(group)] + "\n \n"; 
			output.write(toWrite, 0, toWrite.length());
		}
		output.flush();
	}

}
