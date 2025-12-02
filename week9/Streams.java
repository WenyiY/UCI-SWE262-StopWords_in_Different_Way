import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

public class Streams {
    // Read characters from a file
    static Stream<Character> characters(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName))
                .flatMap(line -> (line + "\n").chars().mapToObj(c -> (char) c));
    }

    // Read words from a file
    static Stream<String> allWords(String fileName) throws IOException {
        AtomicInteger state = new AtomicInteger(0);  // 0 = looking for start, 1 = in word
        StringBuilder word = new StringBuilder();

        return characters(fileName)
                .flatMap(c -> {
                    if (state.get() == 0) {
                        if (Character.isLetterOrDigit(c)) {
                            word.setLength(0);
                            word.append(Character.toLowerCase(c));
                            state.set(1);
                        }
                        return Stream.empty();
                    } else {
                        if (Character.isLetterOrDigit(c)) {
                            word.append(Character.toLowerCase(c));
                            return Stream.empty();
                        } else {
                            state.set(0);
                            return Stream.of(word.toString());
                        }
                    }
                });
    }

    // Read non-stop words from a file
    static Stream<String> nonStopWords(String fileName) throws IOException {
        Set<String> stops = new HashSet<>(
                Arrays.asList(Files.readString(Paths.get("../stop_words.txt")).split(",")));

        for (char c = 'a'; c <= 'z'; c++) stops.add(String.valueOf(c));

        return allWords(fileName)
                .filter(w -> w.length() > 0 && !stops.contains(w));
    }

    // Count and sort words
    static Stream<List<Map.Entry<String, Integer>>> countAndSort(String fileName) throws IOException {
        Map<String, Integer> freqs = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(1);

        // first stream: emit intermediate sorted lists every 5000 words
        Stream<List<Map.Entry<String, Integer>>> intermediate =
                nonStopWords(fileName)
                        .flatMap(w -> {
                            freqs.put(w, freqs.getOrDefault(w, 0) + 1);
                            if (counter.getAndIncrement() % 5000 == 0) {
                                return Stream.of(sorted(freqs));
                            }
                            return Stream.empty();
                        });

        // second stream: deferred final sorted list (computed lazily)
        Stream<List<Map.Entry<String, Integer>>> finalOne =
                Stream.generate(() -> sorted(freqs)).limit(1);

        // concat: intermediate results first, then the final full result
        return Stream.concat(intermediate, finalOne);
    }

    // Sort the map by value
    private static List<Map.Entry<String, Integer>> sorted(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list;
    }

    /* ============================ Main Function ================================ */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java Streams <input-file>");
            System.exit(1);
        }
        String filename = args[0];

        countAndSort(filename).forEach(list -> {
            System.out.println("-----------------------------");
            list.stream().limit(25).forEach(e ->
                    System.out.println(e.getKey() + " - " + e.getValue()));
        });
    }
}
