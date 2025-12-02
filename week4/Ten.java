import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

// Abstraction class for binding
class TheOne<T> {
  private T value;

  public TheOne(T value){
    this.value = value;
  }
  
  // Bind the next function to the current function
  public <R> TheOne<R> bind(Function<T, R> function){
    return new TheOne<>(function.apply(value));
  }

  // Print the result
  public void print(){
    System.out.println(value);
  }
}

public class Ten{
  // Read file and pass result to next function
  public static String readFile(String path){
    try{
      return Files.readString(Paths.get(path));
    }
    catch (IOException e){
      throw new RuntimeException(e);
    }
  }
  // Filter out non-letter characters and convert to lower case
  public static List<String> filterChars(String data){
    Pattern pattern = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");
    return pattern.matcher(data).results().map(MatchResult::group).map(String::toLowerCase).map(w -> w.replaceAll("'s", "")).toList();
  }
  // Remove stop words, and pass result to next function
  public static List<String> removeStopWords(List<String> words){
    try {
      List<String> stopWords = Arrays.asList(Files.readString(Paths.get("../stop_words.txt")).split(","));
      return words.stream().filter(w -> !stopWords.contains(w)).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  // Count words and pass result to next function
  public static Map<String, Integer> countWords(List<String> words){
    return words.stream().collect(Collectors.groupingBy(w -> w, Collectors.summingInt(w -> 1)));
  }
  // Print top 25 words and pass result to next function
  public static String printTop25(Map<String, Integer> wordCount){
    return wordCount.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(25).map(e -> e.getKey() + " - " + e.getValue()).collect(Collectors.joining("\n"));
  }
  
  /* =================== Main function ===================== */
  public static void main(String[] args) throws Exception{
    if (args.length == 0){
      System.out.println("Please provide a file path");
      return;
    }
    // start the function chain
    new TheOne<>(args[0]).
      bind(Ten::readFile).
      bind(Ten::filterChars).
      bind(Ten::removeStopWords).
      bind(Ten::countWords).
      bind(Ten::printTop25).
      print();
  }
}