# Java Word Frequency Counter

This project contains a Java program that counts word frequencies in text files while filtering out common stop words.

## Project Structure

```
.
├── week1/
│   ├── Main.java              # Main program with word counting logic
│   ├── stop_words.txt         # List of stop words to filter out
│   └── pride-and-prejudice.txt # Sample text file for analysis
├── stop_words.txt             # Stop words file in root
├── pride-and-prejudice.txt    # Sample text in root
└── README.md                  # This file
```

## How to Run

### Option 1: Using the Run Button (Easiest)
Simply click the **Run** button at the top of the Replit interface. The workflow is already configured to:
- Navigate to the `week1/` directory
- Compile `Main.java`
- Run the program

### Option 2: Using the Command Line
Open the Shell and run these commands:

```bash
cd week1
javac Main.java
java Main
```

### Option 3: Direct Command
You can also run everything in one line:

```bash
cd week1 && javac Main.java && java Main
```

## How the Program Works

1. **Load Stop Words**: Reads common words (like "the", "a", "an") from `stop_words.txt`
2. **Input File**: Prompts you to enter a filename to analyze
3. **Process Text**: 
   - Extracts words using regex pattern (handles contractions like "Elizabeth's")
   - Converts to lowercase
   - Filters out stop words
4. **Count & Display**: Shows word frequencies sorted from most to least common

## Sample Usage

When you run the program:
1. It will ask: `Please enter the file name you want to count the word frequency:`
2. Type: `pride-and-prejudice.txt` (or `../pride-and-prejudice.txt` if files are in root)
3. The program will display all words and their frequencies

## File Paths

The program runs from the `week1/` directory, so:
- Files in `week1/` can be accessed directly: `filename.txt`
- Files in the root directory need `../` prefix: `../filename.txt`

## Requirements

- Java Development Kit (JDK) - Already installed in this Replit
- Text files for analysis
- Stop words file (already provided)
