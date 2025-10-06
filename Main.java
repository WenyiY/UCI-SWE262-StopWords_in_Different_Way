import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> lines = Files.readAllLines(Path.of("stop_words.txt"));
            
            System.out.println("Content of stop_words.txt:");
            System.out.println("==========================");
            
            if (lines.isEmpty()) {
                System.out.println("(The file is empty)");
            } else {
                for (String line : lines) {
                    System.out.println(line);
                }
            }
            
            System.out.println("==========================");
            System.out.println("Total lines: " + lines.size());
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
