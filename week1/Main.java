import java.io.*;
import java.util.*;

public class Main{

  /* Read the stop word from a file and check if the word is a stop word*/
  static class StopWords {
    private Set<String> stopWords;

    public StopWords(String fileName) throws IOException{
      stopWords = new HashSet<>();
      try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
        String word;
        while ((word = br.readLine()) != null){
          stopWords.add(word.trim().toLowerCase());
        }
      }
    }
    public boolean isStopWord(String word){
      return stopWords.contains(word.toLowerCase());
    }
  }

  /* Read the article, filter the stop words, and count word frequency */
  static class WordCount {
    private Map<String, Integer> wordCount;

    // Read the article and count the word frequency without stop words
    public void readArticle(String fileName, StopWords stopWords) throws IOException {
      try(Scanner sc = new Scanner(new File(fileName))){
        while (sc.hasNext()){
          String revisedWord = sc.next().replaceAll("[^a-zA-Z]", "").toLowerCase();
          if (!revisedWord.isEmpty() && !stopWords.isStopWord(revisedWord)){
            wordCount.put(revisedWord, wordCount.getOrDefault(revisedWord, 0) + 1);
          }
        }
      }
    }

    // Print the valid words and their frequency
    public void printWordCount(){
      for (String key : wordCount.keySet()){
        System.out.println(key + " " + wordCount.get(key));
      }
    }
  }

  public static void main(String[] args) throws IOException{
    StopWords stopWords = new StopWords("stop_words.txt");
  }
  
}