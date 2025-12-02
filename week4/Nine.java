import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

public class Nine {
    // Read file and pass result to next function
    public static void readFile(String path, Consumer<String> nextFuc) throws IOException {
        String data = Files.readString(Paths.get(path));
        nextFuc.accept(data); 
    }

    // Filter out non-letter characters and convert to lower case
    public static void filterChars(String data) {
        Pattern pattern = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");
        List<String> words = pattern.matcher(data).results()
                                    .map(MatchResult::group)
                                    .toList();
        words = words.stream().map(String::toLowerCase).toList();
        words = words.stream().map(w -> w.replaceAll("'s", "")).toList();
        removeStopWords(words, Nine::countWords);
    }

    // Remove stop words, and pass result to next function
    public static void removeStopWords(List<String> words, Consumer<List<String>> nextFuc) {
        try {
            List<String> stopWords = Arrays.asList(Files.readString(Paths.get("../stop_words.txt")).split(","));
            List<String> filtered = words.stream().filter(w -> !stopWords.contains(w)).toList();
            nextFuc.accept(filtered);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Count words and pass result to next function
    public static void countWords(List<String> words) {
        Map<String, Integer> wordCount = words.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.summingInt(w -> 1)));
        printTop25(wordCount, Nine::noOp);
    }

    // Print top 25 words and pass result to next function
    public static void printTop25(Map<String, Integer> wordCount, Runnable nextFuc) {
        wordCount.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(25).forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
        nextFuc.run(); 
    }

    // no-op continuation
    public static void noOp() {
        // does nothing
    }

    /* =================== Main function ===================== */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please provide a file path");
            return;
        }
        // start the function chain
        readFile(args[0], Nine::filterChars);
    }
}
