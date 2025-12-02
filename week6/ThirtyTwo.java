import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.AbstractMap;

public class ThirtyTwo{
  public static final Set<String> stopWords = new HashSet<>();
  
  // Load stop words and add single letters
  static {
    try {
      String stopWordsContent = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
      stopWords.addAll(Arrays.asList(stopWordsContent.split(",")));
      for (char c = 'a'; c <= 'z'; c++) {
        stopWords.add(String.valueOf(c));
      }
    } catch (IOException e) {
      System.err.println("Error reading stop words file: " + e.getMessage());
    }
  }

  // Filter out stop words and words with length less than 2
  public static String readFile(String path) throws IOException{
    return new String(Files.readAllBytes(Paths.get(path)));
  }

  // Parallelize the word processing
  public static List<String> filterWords(String data, int lines){
    String[] line = data.split("\\R");
    List<String> words = new ArrayList<>();
    int nLines = 1000; 
    for (int i = 0; i < line.length; i+=nLines){
      int end = Math.min(i + nLines, line.length);
      String[] chunk = Arrays.copyOfRange(line, i, end);

      words.add(String.join("\n", chunk));
    }
    return words;
  }

  // Process words in parallel, map function
  public static List<Map.Entry<String, Integer>> processWords(String words){
    Pattern pattern = Pattern.compile("[\\W_]+");
    String lowerContent = pattern.matcher(words).replaceAll(" ").toLowerCase();

    return Arrays.stream(lowerContent.split("\\s+"))
      .filter(w -> !w.isEmpty()) 
      .filter(w -> !stopWords.contains(w))
      .map(w -> new AbstractMap.SimpleEntry<>(w, 1))
      .collect(Collectors.toList());
  }

  // Regroup the results
  public static Map<String, List<Map.Entry<String, Integer>>> regroup(List<List<Map.Entry<String, Integer>>> pairsList){
    // Flatten the list of maps and regroup by word
    return pairsList.stream().flatMap(List::stream).collect(Collectors.groupingBy(Map.Entry::getKey));
  }

  // Reduce Function
  public static Map.Entry<String, Integer> countWords(Map.Entry<String, List<Map.Entry<String, Integer>>> grouped){
    String word = grouped.getKey();
    int count = grouped.getValue().stream().mapToInt(Map.Entry::getValue).sum();
    return new AbstractMap.SimpleEntry<>(word, count);
  }

  /* ============================ Main function ================================ */
  public static void main(String[] args) throws IOException{
    if (args.length < 1){
      System.err.println("Usage: java ThirtyTwo <path_to_file>");
      return;
    }

    try {
      String data = readFile(args[0]);
      int lines = data.split("\\R").length;
      List<String> words = filterWords(data, lines);
      
      // Parallelize the word processing
      List<List<Map.Entry<String, Integer>>> pairsList = words.parallelStream().map(ThirtyTwo::processWords).collect(Collectors.toList());
      Map<String, List<Map.Entry<String, Integer>>> grouped = regroup(pairsList);
      
      // Parallelize the counting
      List<Map.Entry<String, Integer>> wordFreqs = grouped.entrySet().parallelStream().map(ThirtyTwo::countWords).collect(Collectors.toList());
      wordFreqs.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

      // Print the top 25
      for (int i = 0; i < Math.min(25, wordFreqs.size()); i++){
        System.out.println(wordFreqs.get(i).getKey() + " - " + wordFreqs.get(i).getValue());
      }
    }
    catch (IOException e){
      System.err.println("Error reading file: " + e.getMessage());
    }
  }
}