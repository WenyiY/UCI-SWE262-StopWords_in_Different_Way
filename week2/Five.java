import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Five{
  private static Set<String> stopWords = new HashSet<>();
  private static Map<String, Integer> wordCount = new HashMap<>();
  private static final int TOP_N = 25;
  private static final Pattern wordPattern = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");
  
  // Read the stop words from a file
  public static void readStopWords(String stopFile) throws IOException{
    try (BufferedReader br = new BufferedReader(new FileReader(stopFile))){
      String line;
      while ((line = br.readLine()) != null){
        String[] words = line.split(",");
        for (String w : words) stopWords.add(w.toLowerCase());
      }
    } catch (IOException e){
      System.out.println("Error: " + e.getMessage());
    }
  }
  // Read the article and count the word frequency without stop words
  public static void filterFile() throws IOException{
    System.out.println("Please enter the file name you want to count the word frequency: ");
    Scanner sc = new Scanner(System.in);
    String Filename = "../" + sc.nextLine();
    while (true){
      try (BufferedReader br = new BufferedReader(new FileReader(Filename))) {
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
      }
      catch (IOException e){
        System.out.println("Error: " + e.getMessage());
      }
    }
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
    readStopWords("../stop_words.txt");
    filterFile();
    printTop25(wordCount);
  }
}