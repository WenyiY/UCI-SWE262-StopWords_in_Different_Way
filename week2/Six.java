import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Six{
  private static final int TOP_N = 25;
  private static final Pattern wordPattern = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");

  // Read the stop words from a file
  public static Set<String> readStopWords(String stopFile) throws IOException{
    Set<String> stopWords = new HashSet<>();
    try (BufferedReader br = new BufferedReader(new FileReader(stopFile))){
      String line;
      while ((line = br.readLine()) != null){
        String[] words = line.split(",");
        for (String w : words) stopWords.add(w.toLowerCase());
      }
    } catch (IOException e){
      System.out.println("Error: " + e.getMessage());
    }
    return stopWords;
  }
  // Read the article and count the word frequency without stop words
  public static Map<String, Integer> filterFile(String fileName, Set<String> stopWords) throws IOException{
    Map<String, Integer> wordCount = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = br.readLine()) != null){
         Matcher match = wordPattern.matcher(line);
        while (match.find()){
          String pureWord = match.group();
          pureWord = pureWord.replaceAll("'s", "");
          pureWord = pureWord.toLowerCase();
          if (!pureWord.isEmpty() && !stopWords.contains(pureWord)){
            wordCount.put(pureWord, wordCount.getOrDefault(pureWord, 0) + 1);
          }
        }
      }
      br.close();
    }
      catch (IOException e){
        System.out.println("Error: " + e.getMessage());
      }
    return wordCount;
  }

  // Sort the word count by frequency ascending order, and print the top n words out
  public static void printTop25(Map<String, Integer> wordCount){
    List<Map.Entry<String, Integer>> wordList = new ArrayList<>(wordCount.entrySet());
    wordList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
    for (int i = 0; i < TOP_N; i++){
      Map.Entry<String, Integer> entry = wordList.get(i);
      System.out.println(entry.getKey() + "  -  " + entry.getValue());
    }
  }

  /* =================== Main function ===================== */
  public static void main(String[] args) throws IOException{
    printTop25(filterFile(args[0], readStopWords("../stop_words.txt")));
  }
}