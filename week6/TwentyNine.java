import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Helper class to send messages to ActiveWFObjects
class MessageSender {
    public static void send(ActiveWFObject receiver, Object... message) {
        try {
            receiver.queue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Base class for ActiveWFObjects
abstract class ActiveWFObject extends Thread {
    protected BlockingQueue<Object[]> queue = new LinkedBlockingQueue<>();
    protected volatile boolean stopMe = false;

    public ActiveWFObject() {
        // Automatically start the thread upon creation
        start();
    }

    protected abstract void dispatch(Object[] message); // To be implemented by subclasses

    @Override
    public void run() {
        while (!stopMe) {
            try {
                Object[] message = queue.take(); // Block until a message is available
                dispatch(message);

                if ("die".equals(message[0])) {
                    stopMe = true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stopMe = true;
            }
        }
    }
}

// Models the data storage and initial processing of the input file.
class DataStorageManager extends ActiveWFObject {
    private String data = "";
    private ActiveWFObject stopWordManager;

    @Override
    protected void dispatch(Object[] message) {
        String methodName = (String) message[0];
        Object[] args = Arrays.copyOfRange(message, 1, message.length);

        if ("readFile".equals(methodName)) {
            readFile(args);
        } else if ("send_word_freqs".equals(methodName)) {
            processWords(args);
        } else {
            // Forward to the next in the pipeline
            MessageSender.send(this.stopWordManager, message);
        }
    }

    // Initialize the data storage manager with the file path and the stop word manager
    private void readFile(Object[] message) {
        String pathToFile = (String) message[0];
        this.stopWordManager = (ActiveWFObject) message[1];
        try {
            this.data = new String(Files.readAllBytes(Paths.get(pathToFile)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            this.data = "";
        }

        // Replace non-alphanumeric/underscore with space, convert to lower case
        Pattern pattern = Pattern.compile("[\\W_]+");
        this.data = pattern.matcher(this.data).replaceAll(" ").toLowerCase();
    }

    // Process the words in the data and send them to the stop word manager
    private void processWords(Object[] message) {
        ActiveWFObject recipient = (ActiveWFObject) message[0];
        String[] words = this.data.split("\\s+");

        for (String w : words) {
            if (!w.isEmpty()) {
                MessageSender.send(this.stopWordManager, "filter", w);
            }
        }
        MessageSender.send(this.stopWordManager, "top25", recipient);
    }
}

// Filters out stop words and forwards the rest to the word frequency manager 
class StopWordManager extends ActiveWFObject {
    private Set<String> stopWords = new HashSet<>();
    private ActiveWFObject wordFreqsManager;

    @Override
    protected void dispatch(Object[] message) {
        String methodName = (String) message[0];
        Object[] args = Arrays.copyOfRange(message, 1, message.length);

        if ("readStopWords".equals(methodName)) {
            readStopWords(args);
        } else if ("filter".equals(methodName)) {
            filter(args);
        } else {
            // Forward
            MessageSender.send(this.wordFreqsManager, message);
        }
    }

    // Initialize the stop word manager with the word frequency manager
    private void readStopWords(Object[] message) {
        this.wordFreqsManager = (ActiveWFObject) message[0];
        try {
            // Read stop words
            String stopWordsContent = new String(Files.readAllBytes(Paths.get("../stop_words.txt")));
            stopWords.addAll(Arrays.asList(stopWordsContent.split(",")));

            // Add single letters
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
        } catch (IOException e) {
            System.err.println("Error reading stop words file: " + e.getMessage());
        }
    }

    // Filter out stop words and send the rest to the word frequency manager
    private void filter(Object[] message) {
        String word = (String) message[0];
        if (!stopWords.contains(word)) {
            MessageSender.send(this.wordFreqsManager, "word", word);
        }
    }
}

// Manages the frequency of words and provides the top 25 words
class WordFrequencyManager extends ActiveWFObject {
    // Map<Word, Count>
    private Map<String, Integer> wordFreqs = new HashMap<>();

    @Override
    protected void dispatch(Object[] message) {
        String methodName = (String) message[0];
        Object[] args = Arrays.copyOfRange(message, 1, message.length);

        if ("word".equals(methodName)) {
            incrementCount(args);
        } else if ("top25".equals(methodName)) {
            top25(args);
        }
    }

    // Increment the count of the word
    private void incrementCount(Object[] message) {
        String word = (String) message[0];
        wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
    }

    @SuppressWarnings("unchecked")
    private void top25(Object[] message) {
        ActiveWFObject recipient = (ActiveWFObject) message[0];

        // Sort the map entries by value (frequency) in descending order
        List<Map.Entry<String, Integer>> freqsSorted = wordFreqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        MessageSender.send(recipient, "top25", freqsSorted);
    }
}

// Controls the workflow and displays the top 25 words
class WordFrequencyController extends ActiveWFObject {
    private ActiveWFObject storageManager;

    @Override
    protected void dispatch(Object[] message) {
        String methodName = (String) message[0];
        Object[] args = Arrays.copyOfRange(message, 1, message.length);

        if ("run".equals(methodName)) {
            runWorkflow(args);
        } else if ("top25".equals(methodName)) {
            display(args);
        } else {
            throw new IllegalArgumentException("Message not understood: " + methodName);
        }
    }

    // Run the workflow by sending the initial message to the storage manager
    private void runWorkflow(Object[] message) {
        this.storageManager = (ActiveWFObject) message[0];
        MessageSender.send(this.storageManager, "send_word_freqs", this);
    }

    @SuppressWarnings("unchecked")
    private void display(Object[] message) {
        List<Map.Entry<String, Integer>> wordFreqs = (List<Map.Entry<String, Integer>>) message[0];

        // Print the top 25
        for (int i = 0; i < Math.min(25, wordFreqs.size()); i++) {
            Map.Entry<String, Integer> entry = wordFreqs.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        // Shut down the storage manager and itself
        MessageSender.send(this.storageManager, "die");
        this.stopMe = true;
    }
}

// Main class to run the Word Frequency Framework
public class TwentyNine {
/* ============================ Main function ================================ */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java WordFrequencyFramework <path_to_file>");
            return;
        }

        // Instantiate the 'things' (Active Objects)
        WordFrequencyManager wordFreqManager = new WordFrequencyManager();
        StopWordManager stopWordManager = new StopWordManager();
        DataStorageManager storageManager = new DataStorageManager();
        WordFrequencyController wfController = new WordFrequencyController();

        // Initialize the pipeline by sending initial messages
        MessageSender.send(stopWordManager, "readStopWords", wordFreqManager);
        MessageSender.send(storageManager, "readFile", args[0], stopWordManager);
        MessageSender.send(wfController, "run", storageManager);

        // Wait for all active objects to finish their execution
        try {
            wordFreqManager.join();
            stopWordManager.join();
            storageManager.join();
            wfController.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}