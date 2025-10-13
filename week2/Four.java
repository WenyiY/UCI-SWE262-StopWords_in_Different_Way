import java.io.*;

public class Four {
  // Check if the word is a stop word
    public static boolean isStopWord(String word, String[] stopWords, int stopCount){
      for (int i = 0; i < stopCount; i++){
        if (stopWords[i] != null && stopWords[i].equals(word)) return true;
      }
      return false;
    }
    // Check if the word is already in the array, if yes, return the index, if not, return -1
    public static int isWordInArray(String word, String[][] wordFrequency, int wordCount){
      for (int i = 0; i < wordCount; i++){
        if (wordFrequency[i][0] != null && wordFrequency[i][0].equals(word)) return i;
      }
      return -1;
    }
    // Expand the array size
    public static String[][] expandArray(String[][] oldArray, int arrayLength){
      String[][] newArray = new String[arrayLength * 2][2];
      for (int i = 0; i < arrayLength; i++){
        newArray[i][0] = oldArray[i][0];
        newArray[i][1] = oldArray[i][1];
      }
      return newArray;
    }

  /* =================== Main function ===================== */
    public static void main(String[] args){
      if (args.length < 1){
        System.out.println("Please make sure the file is input correctly.");
        return;
      }

      // Set the array with [workd, frequency] pairs, and set a default size of 100000
      String[][] wordFrequency = new String[100000][2];
      int wordCount = 0;

      String[] stopWords = new String[1000];
      int stopWordCount = 0;

      
      try (BufferedReader stopWordReader = new BufferedReader(new FileReader("../stop_words.txt"))){
        String stopWordLine = stopWordReader.readLine();
        stopWordReader.close();

        // Split the stop words by comma
        if (stopWordLine != null){
          String word = "";
          for (int i = 0; i < stopWordLine.length(); i++){
            char c = stopWordLine.charAt(i);
            if (c == ','){
              stopWords[stopWordCount++] = word;
              word = "";
            } else {
              word += c;
            }
          }
          if (!word.isEmpty()) stopWords[stopWordCount++] = word;
        }

        // add single letters to stop words
        for (char c = 'a'; c <= 'z'; c++){
          stopWords[stopWordCount++] = String.valueOf(c);
        }

        // Read the content file
        BufferedReader contentReader = new BufferedReader(new FileReader(args[0]));
        String contentLine;
        while ((contentLine = contentReader.readLine()) != null) {
          Integer startChar = null;

          // Find the start and end of each word
          for (int i = 0; i < contentLine.length(); i++){
            char c = contentLine.charAt(i);

            if (startChar == null && Character.isLetterOrDigit(c)){
              startChar = i;
            }
            else {
              if (startChar != null && !Character.isLetterOrDigit(c)){
                String word = contentLine.substring(startChar, i).toLowerCase();
                startChar = null;

                // Check if the word is a stop word, if not, add it to the array
                if (!isStopWord(word, stopWords, stopWordCount)){
                  int index = isWordInArray(word, wordFrequency, wordCount);
                  if (index != -1){
                    wordFrequency[index][1] = String.valueOf(Integer.parseInt(wordFrequency[index][1]) + 1);
                    // Sort the array by frequency
                    for (int n = index -1; n >= 0; n--){
                      int currCount = Integer.parseInt(wordFrequency[index][1]);
                      int prevCount = Integer.parseInt(wordFrequency[n][1]);
                      if (currCount > prevCount){
                        String[] temp = wordFrequency[n];
                        wordFrequency[n] = wordFrequency[index];
                        wordFrequency[index] = temp;
                        index = n;
                      }
                      else break;
                    }
                  } else {
                    if (wordCount >= wordFrequency.length){
                      wordFrequency = expandArray(wordFrequency, wordCount);
                    }
                    // Add new word to the array if it does not exist
                    wordFrequency[wordCount][0] = word;
                    wordFrequency[wordCount][1] = "1";
                    wordCount++;
                  }
                }
              }
            }
          }

          // Check the last word in the line
          if (startChar != null) {
            String word = contentLine.substring(startChar).toLowerCase();
            if (!isStopWord(word, stopWords, stopWordCount)){
              int index = isWordInArray(word, wordFrequency, wordCount);
              if (index != -1){
                wordFrequency[index][1] = String.valueOf(Integer.parseInt(wordFrequency[index][1]) + 1);

                // repeat same sorting logic for last word
                for (int n = index - 1; n >= 0; n--){
                  int currCount = Integer.parseInt(wordFrequency[index][1]);
                  int prevCount = Integer.parseInt(wordFrequency[n][1]);
                  if (currCount > prevCount){
                    String[] temp = wordFrequency[n];
                    wordFrequency[n] = wordFrequency[index];
                    wordFrequency[index] = temp;
                    index = n;
                  }
                  else break;
                }
              } else {
                if (wordCount >= wordFrequency.length){
                  wordFrequency = expandArray(wordFrequency, wordCount);
                }
                wordFrequency[wordCount][0] = word;
                wordFrequency[wordCount][1] = "1";
                wordCount++;
              }
            }
          }
        }
        contentReader.close();
      } catch (IOException e){
        System.out.println("Error: " + e.getMessage());
        return;
      }
      // Print the top 25 words
      for (int i = 0; i < 25; i++){
        System.out.println(wordFrequency[i][0] + " - " + wordFrequency[i][1]);
      }
    }
}