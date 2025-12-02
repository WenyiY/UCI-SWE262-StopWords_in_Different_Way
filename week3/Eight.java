import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Eight{
  private static final int TOP_N = 25;

  // Parse letters and words recursively
  public static void parseWords(char[] chars, int index, Set<String> stopWords, List<String> words){
    // base case
    if (index >= chars.length) {
      return;
    }

    char c = chars[index];
    
    if (Character.isLetter(c)){
      StringBuilder word = new StringBuilder();
      while (index < chars.length && Character.isLetter(chars[index])){
        word.append(Character.toLowerCase(chars[index]));
        index++;
      }
      String pureWord = word.toString();
      if (word.length() > 1 && !stopWords.contains(pureWord)){
        words.add(pureWord);
      }
      // continue parsing the rest of the characters
      parseWords(chars, index, stopWords, words);
    }
    else {
      // skip non-letter characters
      parseWords(chars, index + 1, stopWords, words);
    }

  }

  // Sort the word count by frequency ascending order, and print the top n words out
  public static void printTop25(Map<String, Integer> wordCount){
    List<Map.Entry<String, Integer>> wordList = new ArrayList<>(wordCount.entrySet());
    wordList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

    int min = Math.min(TOP_N, wordList.size());
    for (int i = 0; i < min; i++){
      Map.Entry<String, Integer> entry = wordList.get(i);
      System.out.println(entry.getKey() + "  -  " + entry.getValue());
    }
  }

  /* =================== Main function ===================== */
  public static void main(String[] args) throws IOException{
    Set<String> stopWords = new HashSet<>(Arrays.asList(Files.readString(Paths.get("../stop_words.txt")).split(",")));
    List<String> words = new ArrayList<>();
    // Read the file line by line
    try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
        String line;
        while ((line = br.readLine()) != null) {
            // Recursive parsing for this line
            parseWords(line.toCharArray(), 0, stopWords, words);
        }
    }
        
    // Count words iteratively
    Map<String, Integer> wordCount = new HashMap<>();
    for (String word : words) {
        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
    }
    printTop25(wordCount);
  }
}