import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class Twenty{
  // Define interfaces for the extractor and counter
  public interface WordExtractor{
    List<String> extractWords(String filePath) throws Exception;  
  }
  public interface FrequencyCounter{
    List<Map.Entry<String, Integer>> top25(List<String> words);
  }

  /* WordExtractor plugins*/
  // Plugin 1: normal extraction
  public static class NormalWordExtractor implements WordExtractor{
    @Override
    public List<String> extractWords(String filePath) throws Exception {
        String data = Files.readString(Paths.get(filePath)).toLowerCase();

        // find words
        Pattern p = Pattern.compile("[a-z]{2,}");
        List<String> words =
            p.matcher(data).results().map(m -> m.group()).collect(Collectors.toList());

        // load stop words
        List<String> stopWords = Arrays.asList(
            Files.readString(Paths.get("../stop_words.txt")).split(",")
        );
        return words.stream()
                .filter(w -> !stopWords.contains(w))
                .collect(Collectors.toList());
    }
  }

  // Plugin 2: extract ONLY words containing 'z'
  public static class ZWordExtractor implements WordExtractor{
    @Override
    public List<String> extractWords(String filePath) throws Exception{
      String data = Files.readString(Paths.get(filePath)).toLowerCase();
      Pattern p = Pattern.compile("[a-z]*z[a-z]*");
      List<String> words = p.matcher(data).results().map(m -> m.group()).collect(Collectors.toList());
      List<String> stopWords = Arrays.asList(
            Files.readString(Paths.get("../stop_words.txt")).split(",")
        );
      return words.stream()
                .filter(w -> !stopWords.contains(w))
                .collect(Collectors.toList());
    }
  }

  /* FrequencyCounter plugins */
  // Plugin 1: normal counting
  public static class NormalFrequencyCounter implements FrequencyCounter{
    @Override
    public List<Map.Entry<String, Integer>> top25(List<String> words){
      Map<String, Integer> freqMap = new HashMap<>();
      for (String word : words){
        freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
      }
      return freqMap.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(25)
        .collect(Collectors.toList());
    }
  }

  // Plugin 2: count by first letter (a, b, c…)
   public static class FirstLetterFrequencyCounter implements FrequencyCounter{
     @Override
     public List<Map.Entry<String, Integer>> top25(List<String> words){
       Map<String, Integer> freqMap = new HashMap<>();
       for (String word : words){
         String firstLetter = word.substring(0, 1);
         freqMap.put(firstLetter, freqMap.getOrDefault(firstLetter, 0) + 1);
       }
       return freqMap.entrySet().stream()
         .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
         .limit(25)
         .collect(Collectors.toList());
     }
   }

  /* ============================ Main Function ================================ */
  public static void main(String[] args) throws Exception{
    if (args.length == 0){
      System.err.println("Usage: java Twenty <file>");
      return;
    }

    // Load configuration
    Properties config = new Properties();
    config.load(new FileInputStream("config.ini"));
    String wordsPluginName = config.getProperty("words").trim();
    String freqPluginName = config.getProperty("frequencies").trim();

    // Load plugins using reflection
    WordExtractor wordExtractor = (WordExtractor) Class.forName(wordsPluginName).getDeclaredConstructor().newInstance();
    FrequencyCounter freqCounter = (FrequencyCounter) Class.forName(freqPluginName).getDeclaredConstructor().newInstance();

    // Run plugins
    List<String> words = wordExtractor.extractWords(args[0]);
    List<Map.Entry<String, Integer>> top25 = freqCounter.top25(words);

    // Print results
    for (Map.Entry<String, Integer> entry : top25){
      System.out.println(entry.getKey() + " - " + entry.getValue());
    }
  }
}