import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main{

  /* Read the stop word from a file and check if the word is a stop word*/
  static class StopWords {
    private Set<String> stopWords;

    public StopWords(String fileName) throws IOException{
      stopWords = new HashSet<>();
      try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
        String line;
        while ((line = br.readLine()) != null){
          String[] words = line.split(",");
          for (String w : words) stopWords.add(w.toLowerCase());
        }
      }
    }
    public boolean isStopWord(String word){
      if (word == null || word.isEmpty()) return false;
      return stopWords.contains(word.toLowerCase());
    }
  }

  /* Read the article, filter the stop words, and count word frequency */
  static class WordCount {
    private Map<String, Integer> wordCount;

    // Constructor
    public WordCount(){
      this.wordCount = new HashMap<>();
    }

    // Read the article and count the word frequency without stop words
    public void readArticle(String fileName, StopWords stopWords) throws IOException {
      // find out the punctuation and split the words, such as "Elizabeth's"
      Pattern wordPattern = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");
      
      try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
        String line;
        while ((line = br.readLine()) != null) {
          Matcher match = wordPattern.matcher(line);
          while (match.find()){
            String pureWord = match.group();
            pureWord = pureWord.replaceAll("'s", ""); // remove "'s"
            pureWord = pureWord.toLowerCase(); // convert to lower case

            if (!pureWord.isEmpty() && !stopWords.isStopWord(pureWord)){
              wordCount.put(pureWord, wordCount.getOrDefault(pureWord, 0) + 1);
            }
          }
        }
      }
    }

    // Sort the word count by frequency ascending order, and print the top n words out
    public void printWordCount(int topN){
      List <Map.Entry<String, Integer>> wordList = new ArrayList<>(wordCount.entrySet());
      wordList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
      int count = 0;
      
      for (Map.Entry<String, Integer> list : wordList){
        System.out.println(list.getKey() + "  -  " + list.getValue());
        count++;
        if (count >= topN) break;
      }
    }
  }

  /* =================== Main function ===================== */
  public static void main(String[] args) throws IOException{
    StopWords stopWords = new StopWords("../stop_words.txt");
    WordCount wordCount = new WordCount();
    System.out.println("Please enter the file name you want to count the word frequency:");
    Scanner sc = new Scanner(System.in);
    wordCount.readArticle("../" + sc.nextLine(), stopWords);
    wordCount.printWordCount(25);
  }
}