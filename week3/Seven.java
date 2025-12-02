import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class Seven{
  public static void main(String[] args) throws Exception{
    // Read the stop words from a file
    var stopWords = Files.readString(Paths.get("../stop_words.txt")).split(",");
    
    // Read the article, extract words as stream, filter out stop words, count the frequency of each word, sort by frequency, and print the top 25 words
    Pattern.compile("[a-z]{2,}").matcher(Files.readString(Paths.get(args[0])).toLowerCase()).results().map(MatchResult::group).filter(keepWords -> !Arrays.asList(stopWords).contains(keepWords)).collect(Collectors.groupingBy(word -> word, Collectors.summingInt(word -> 1))).entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(25).forEach(e -> System.out.println(e.getKey() + "  -  " + e.getValue()));
  }
}