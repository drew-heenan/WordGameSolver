import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.swing.JOptionPane;


public class WordSolver {
	public static void main(String[] args) {
		String[] input = queryUser();
		new WordSolver(input[0], input[1]);
	}
	
	//Asks the user for their two words.These should but may not be in the dictionary.
	private static String[] queryUser() {
		String from = null, to = null;
		while(from == null || from.isEmpty())
			from = JOptionPane.showInputDialog("What word are you starting with?").trim();
		while(to == null || to.isEmpty())
			to = JOptionPane.showInputDialog("What word are you ending with?").trim();
		return new String[]{from, to};
	}
	
	//Maximum steps the program will take towards a solution before giving up on a particular solution path.
	private static final int MAX_STACK_SIZE = 2000;
	
	private Stack<String> wordStack = new Stack<>();
	private ArrayList<String> words = new ArrayList<>();
	private String from, to;
	private String[] lastStack = null;
	
	public WordSolver(String from, String to) {
		if(from.length() != to.length())
			return;
		this.from = from;
		this.to = to;
		loadAllStringsOfSize(from.length());
		solve(from);
		if(lastStack == null) {
			System.err.println("I looked real hard. There is no way to do this.");
			JOptionPane.showMessageDialog(null, "Oops, I couldn't find a solution!");
		} else {
			String pathTrace = "Here's how I did it:\n";
			for(String s : lastStack)
				pathTrace += "- " + s + "\n";
			pathTrace += "- " + to;
			JOptionPane.showMessageDialog(null, lastStack.length > 45 ? "I found a solution in " + lastStack.length + " steps.\n(That's a lot - check the console for the full solution)" : pathTrace);
			System.out.println(pathTrace);
		}
	}
	
	//Recursive function that finds all available paths to the target word. Saves the best option it finds to lastStack.
	private void solve(String word) {
		List<String> matchingWords = findWordsWithOneLetterDifference(word);
		wordStack.push(word);
		if(matchingWords.isEmpty() || wordStack.size() >= MAX_STACK_SIZE) {
			return;
		}
		for(String s : matchingWords) {
			if(s.equals(to)) {
				System.out.println("FOUND ONE! I did it in " + wordStack.size() + " steps. The last one was done in " + ((lastStack == null) ? "infinite":lastStack.length) + " steps.");
				if(lastStack == null || lastStack.length > wordStack.size()) {
					lastStack = new String[wordStack.size()];
					wordStack.copyInto(lastStack);
				}
			} else {
				solve(s);
				wordStack.pop();
				words.remove(s);
			}
		}
	}
	
	//Finds all words from the list of loaded words that have only a difference of one character from the given word.
	private List<String> findWordsWithOneLetterDifference(String from) {
		List<String> words = new ArrayList<>();
		for(String s : this.words) {
			int diffLetters = 0;
			for(int i = 0; i < from.length(); i++) {
				if(from.charAt(i) != s.charAt(i))
					diffLetters++;
			}
			if(diffLetters == 1 && !wordStack.contains(s))
				words.add(s);
		}
		return words;
	}
	
	//Loads all strings that have the same size as the given words.
	private void loadAllStringsOfSize(int size) {
		File wordFile = new File(".\\src\\words");
		try {
			BufferedReader br = new BufferedReader(new FileReader(wordFile));
			String line = br.readLine();
			while(line != null) {
				line = line.trim();
				if(line.length() == size && !line.equals(from) && !words.contains(line))
					words.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
