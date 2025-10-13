# Java Word Frequency Counter

This project contains a Java program that counts word frequencies in article files while filtering out common stop words.

## Project Structure

```
.
├── week1/
│   ├── Main.java              # Main program with word counting logic
├── week2/
│   ├── Four.java              # Main program with 4.1
│   ├── Five.java              # Main program with 5.1
│   ├── Six.java               # Main program with 6.1
├── stop_words.txt             # Stop words file in root
├── pride-and-prejudice.txt    # Sample article in root
└── README.md                  # This file
```

## How to Run

### Week 1:
Open the Shell and run these commands:

```bash
cd week1
```
```
javac Main.java
```
```
java Main
```
```
pride-and-prejudice.txt
```

### Week 2:
**4.1**: Open the Shell and run these commands:

```bash
cd week2
```
```
javac Four.java
```
```
java Four ../pride-and-prejudice.txt
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
2. Type: `pride-and-prejudice.txt`
3. The program will display the top 25 words and their frequencies

## File Paths

The program runs from the `week1/` directory, so:
- Files in `week1/` can be accessed directly: `filename.txt`
- Files in the root directory need `../` prefix: `../filename.txt` (It is default setted for both txt)

## Requirements

- Java Development Kit (JDK) - Already installed in this Replit
- Text files for analysis
- Stop words file (already provided)
