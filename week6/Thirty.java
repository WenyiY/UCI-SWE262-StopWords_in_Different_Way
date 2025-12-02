import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Worker class that processes words from the wordSpace and updates the freqSpace
class WordProcessor implements Runnable {
    // Shared Data Spaces
    private final BlockingQueue<String> wordSpace;
    private final BlockingQueue<Map<String, Integer>> freqSpace;

    // Shared, Read-Only Data
    private final Set<String> stopWords;

    // Local, Private Data
    private final Map<String, Integer> wordFreqs = new HashMap<>();

    private static final int TIMEOUT_SECONDS = 1;

    public WordProcessor(BlockingQueue<String> wordSpace, BlockingQueue<Map<String, Integer>> freqSpace, Set<String> stopWords) {
        this.wordSpace = wordSpace;
        this.freqSpace = freqSpace;
        this.stopWords = stopWords;
    }

    @Override
    public void run() {
        // Process words until the wordSpace is empty
        try {
            while (true) {
                // Use poll with timeout to detect when the wordSpace is empty
                String word = wordSpace.poll(TIMEOUT_SECONDS, TimeUnit.SECONDS);

                if (word == null) {
                    // Queue is empty for the timeout period, assume all words processed
                    break;
                }

                // Filtering and Counting (Local Operation)
                if (!stopWords.contains(word)) {
                    wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
                }
            }

            // Put the partial results back into the shared frequency space
            freqSpace.put(wordFreqs);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Worker interrupted: " + Thread.currentThread().getName());
        }
    }
}

/* =========================== Main Function ================================ */
public class Thirty {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java <path_to_file>");
            return;
        }

        // Stores individual words to be processed
        BlockingQueue<String> wordSpace = new LinkedBlockingQueue<>();
        // Stores partial frequency maps computed by each worker
        BlockingQueue<Map<String, Integer>> freqSpace = new LinkedBlockingQueue<>();

        // Load Static Data
        Set<String> stopWords = new HashSet<>();
        try {
            // Load stop words and add single letters
            String stopWordsContent = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
            stopWords.addAll(Arrays.asList(stopWordsContent.split(",")));
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
        } catch (IOException e) {
            System.err.println("Error loading stop words: " + e.getMessage());
        }

        // Populate the word space with words from the input file
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(args[0])));

            // Text normalization, lowercase and regex to extract words of length 2 or more
            String lowerContent = fileContent.toLowerCase();
            Pattern pattern = Pattern.compile("[a-z]{2,}");
            Matcher matcher = pattern.matcher(lowerContent);

            while (matcher.find()) {
                wordSpace.put(matcher.group());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error processing file or populating word space: " + e.getMessage());
            return;
        }

        // Create and Launch Concurrent Units
        final int NUM_WORKERS = 5;
        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < NUM_WORKERS; i++) {
            Thread t = new Thread(new WordProcessor(wordSpace, freqSpace, stopWords), "Worker-" + (i + 1));
            workers.add(t);
            t.start();
        }

        // Wait for Workers to Finish
        for (Thread t : workers) {
            try {
                t.join(); // Wait for the worker to complete its run method
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Final Aggregation and Display 
        Map<String, Integer> finalWordFreqs = new HashMap<>();

        // Consume all partial results from the frequency space
        while (!freqSpace.isEmpty()) {
            try {
                Map<String, Integer> freqs = freqSpace.take();

                // Merge the partial map into the final map
                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    String word = entry.getKey();
                    int count = entry.getValue();
                    finalWordFreqs.put(word, finalWordFreqs.getOrDefault(word, 0) + count);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Sort the final results
        List<Map.Entry<String, Integer>> sortedFreqs = finalWordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // Display the top 25
        for (int i = 0; i < Math.min(25, sortedFreqs.size()); i++) {
            Map.Entry<String, Integer> entry = sortedFreqs.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}