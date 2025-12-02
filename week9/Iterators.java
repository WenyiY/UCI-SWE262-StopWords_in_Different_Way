import java.io.*;
import java.util.*;
import java.util.function.Function;

public class Iterators{
  // Read characters from a file
  static Iterable<Character> characters(String fileName){
    return () -> new Iterator<Character>() {
      BufferedReader reader;
      String line = null;
      int idx = 0;

      {
          try {
              reader = new BufferedReader(new FileReader(fileName));
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
      }
      @Override
          public boolean hasNext() {
              try {
                  while (line == null || idx >= line.length()) {
                      line = reader.readLine();
                      idx = 0;
                      if (line == null) return false;
                      line += "\n";
                  }
                  return true;
              } catch (IOException e) {
                  return false;
              }
          }

          @Override
          public Character next() {
              return line.charAt(idx++);
          }
      };
  }

  // Read words from a file
  static Iterable<String> allWords(String fileName) {
      return () -> new Iterator<String>() {
          Iterator<Character> chars = characters(fileName).iterator();
          boolean start = true;
          StringBuilder word = new StringBuilder();

          @Override
          public boolean hasNext() {
              return chars.hasNext();
          }

          @Override
          public String next() {
              while (chars.hasNext()) {
                  char c = chars.next();
                  if (start) {
                      if (Character.isLetterOrDigit(c)) {
                          word = new StringBuilder();
                          word.append(Character.toLowerCase(c));
                          start = false;
                      }
                  } else {
                      if (Character.isLetterOrDigit(c)) {
                          word.append(Character.toLowerCase(c));
                      } else {
                          start = true;
                          return word.toString();
                      }
                  }
              }
              return "";
          }
      };
  }

  // Read non-stop words from a file
  static Iterable<String> nonStopWords(String fileName) {
      return () -> new Iterator<String>() {

          Iterator<String> words = allWords(fileName).iterator();
          Set<String> stops;

          {
              try {
                  stops = new HashSet<>(
                          Arrays.asList(new String(
                                  java.nio.file.Files.readAllBytes(
                                          java.nio.file.Paths.get("../stop_words.txt")
                                  )).split(","))
                  );
                  // Add a–z
                  for (char c = 'a'; c <= 'z'; c++) stops.add(String.valueOf(c));
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }
          }

          String nextWord = null;

          @Override
          public boolean hasNext() {
              while (words.hasNext()) {
                  String w = words.next();
                  if (w.length() > 0 && !stops.contains(w)) {
                      nextWord = w;
                      return true;
                  }
              }
              return false;
          }

          @Override
          public String next() {
              return nextWord;
          }
      };
  }

  // Count and sort words
  static Iterable<List<Map.Entry<String, Integer>>> countAndSort(String fileName) {
    return () -> new Iterator<List<Map.Entry<String, Integer>>>() {

        Iterator<String> words = nonStopWords(fileName).iterator();
        Map<String, Integer> freqs = new HashMap<>();
        int i = 1;

        @Override
        public boolean hasNext() {
            return words.hasNext();
        }

        @Override
        public List<Map.Entry<String, Integer>> next() {
            while (words.hasNext()) {
                String w = words.next();
                freqs.put(w, freqs.getOrDefault(w, 0) + 1);
                if (i % 5000 == 0) {
                    i++;
                    return sorted(freqs);
                }
                i++;
            }
            return sorted(freqs);
        }

        // Sort the map by value
        private List<Map.Entry<String, Integer>> sorted(Map<String, Integer> f) {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(f.entrySet());
            list.sort((a, b) -> b.getValue() - a.getValue());
            return list;
        }
    };
  }

  /* ============================ Main Function ================================ */
  public static void main(String[] args) {
    String filename = args[0];
    for (List<Map.Entry<String, Integer>> top : countAndSort(filename)) {
        System.out.println("-----------------------------");
        top.stream().limit(25).forEach(e ->
                System.out.println(e.getKey() + " - " + e.getValue())
        );
    }
  }

}