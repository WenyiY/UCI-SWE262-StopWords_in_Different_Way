import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;

// Framework class for word frequency analysis
class WordFrequencyFramework {
  private List<Consumer<String>> loadEventHandlers = new ArrayList<>();
  private List<Runnable> doWorkEventHandlers = new ArrayList<>();
  private List<Runnable> endEventHandlers = new ArrayList<>();

  public void registerForLoadEvent(Consumer<String> handler){
    loadEventHandlers.add(handler);
  }
  public void registerForDoWorkEvent(Runnable handler){
    doWorkEventHandlers.add(handler);
  }
  public void registerForEndEvent(Runnable handler){
    endEventHandlers.add(handler);
  }
  // Run the event handlers
  public void run(String fileName){
    loadEventHandlers.forEach(handler -> handler.accept(fileName));
    doWorkEventHandlers.forEach(Runnable::run);
    endEventHandlers.forEach(Runnable::run);
  }
}

// Stop word filter class
class StopWordFilter{
  private Set<String> stopWords = new HashSet<>();

  // Constructor
  public StopWordFilter(WordFrequencyFramework wordFreq){
      wordFreq.registerForLoadEvent(this :: loadStopWords);
    }
  // Load stop words from a file
  public void loadStopWords(String fileName){
    fileName = "../stop_words.txt";
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
      String line;
      while ((line = br.readLine()) != null){
        String[] words = line.split(",");
        for (String w : words) stopWords.add(w.toLowerCase());
      }
    } catch (IOException e){
      System.out.println("Error: " + e.getMessage());
    }
  }
  // Check if a word is a stop word
  public boolean isStopWord(String word){
    return stopWords.contains(word);
  }
}

// Data storage class
class DataStorage{
  private String data = "";
  private StopWordFilter stopWordFilter;
  private List<Consumer<String>> wordHandlers = new ArrayList<>();

  // Constructor
  public DataStorage(WordFrequencyFramework wordFreq, StopWordFilter stopWordFilter){
    wordFreq.registerForLoadEvent(this :: loadData);
    wordFreq.registerForDoWorkEvent(this :: processData);
    this.stopWordFilter = stopWordFilter;
  }
  
  // Load data from a file
  public void loadData(String filePath){
    try{
      data = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
      Pattern wordPattern = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");
      data = wordPattern.matcher(data).replaceAll(" $0 ").replaceAll("'s", "").toLowerCase();
      data = data.replaceAll("[^a-z\\s]", "");
      data = data.replaceAll("\\s+", " ");
    } catch (IOException e){
      System.out.println("Error: " + e.getMessage());
    }
  }
  // Process data and pass result to next function
  public void processData(){
    String[] words = data.split("\\s+");
    for (String word : words){
      if (!stopWordFilter.isStopWord(word)){
        wordHandlers.forEach(handler -> handler.accept(word));
      }
    }
  }
  // Register for word event
  public void registerForWordEvent(Consumer<String> handler){
    wordHandlers.add(handler);
  }
}

// Word frequency counter class
class WordFrequencyCounter{
  private Map<String, Integer> wordCount = new HashMap<>();

  // Constructor
  public WordFrequencyCounter(WordFrequencyFramework wordFreq, DataStorage dataStorage){
    dataStorage.registerForWordEvent(this :: countWord);
    wordFreq.registerForEndEvent(this :: printTop25);
  }
  // Count words and pass result to next function
  public void countWord(String word){
    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
  }
  // Print top 25 words and pass result to next function
  public void printTop25(){
    wordCount.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(25).forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
  }
}

// Main class
public class Fifteen{
  /* =================== Main function ===================== */
  public static void main(String[] args) throws Exception{
    WordFrequencyFramework wordFreq = new WordFrequencyFramework();
    StopWordFilter stopWordFilter = new StopWordFilter(wordFreq);
    DataStorage dataStorage = new DataStorage(wordFreq, stopWordFilter);
    WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter(wordFreq, dataStorage);

    // Run the event handlers
    wordFreq.run(args[0]);
  }
}