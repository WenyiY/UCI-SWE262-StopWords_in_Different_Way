# Java Word Frequency Counter

This project contains a Java program that counts word frequencies in article files while filtering out common stop words in different ways. 

## Project Structure

```
.
├── week1/
│   ├── Main.java              # Main program with word counting logic
├── week2/
│   ├── Four.java              # No abstractions or library functions
│   ├── Five.java              # Decomposed in procedural abstractions
│   ├── Six.java               # Larger problem solved as a pipeline of function applications
├── week3/
│   ├── Seven.java             # As few lines of code as possible
│   ├── Eight.java             # Problem is modelled by induction
├── week4/
│   ├── Nine.java              # Larger problem is solved as a pipeline of functions, but where the next function to be applied is given as parameter to the current function
│   ├── Ten.java               # Larger problem is solved as a pipeline of functions bound together, with unwrapping happening at the end
│   ├── Fifteen.java           # At certain points of the computation, the entities call on the other entities that have registered for callbacks
├── week5/
│   ├── JarClasses.java        # Take a jar file as command line argument and prints out a listing of all the classes inside
│   ├── org.json-1.6-20240205.jar   # Online jar file
│   ├── gradle-wrapper.jar     # Online jar file
├── week6/
│   ├── TwentyNine.java        # Similar to the letterbox style, but where the 'things' have independent threads of execution
│   ├── Thirty.java            # Existence of one or more units that execute, store and retrieve data concurrently
│   ├── ThirtyTwo.java         # Input data is divided in chunks, a map function applies a given worker function to each chunk of data, potentially in parallel
├── week8/
│   ├── Twenty.java            # Main program uses functions/objects from the dynamically-loaded packages, without knowing which exact implementations will be used. New implementations can be used without having to adapt or recompile the main program
│   ├── config.ini             # Load plugin classes dynamically
├── week9/
│   ├── Iterators.java         # Done with iterators that mimic the Python reference implementation (with generators)
│   ├── Streams.java           # Done with Java stream API
├── stop_words.txt             # Stop words file in root
├── pride-and-prejudice.txt    # Sample article
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
**For all Week 2 exercise**: Open the Shell and run command:
```bash
cd week2
```
**4.1**: Run these commands:

```bash
javac Four.java
```
```
java Four ../pride-and-prejudice.txt
```

**5.1**: Run these commands:

```bash
javac Five.java
```
```
java Five ../pride-and-prejudice.txt
```

**6.1**: Run these commands:

```bash
javac Six.java
```
```
java Six ../pride-and-prejudice.txt
```

### Week 3:
**For all Week 3 exercise**: Open the Shell and run command:
```bash
cd week3
```
**7.1**: Run these commands:

```bash
javac Seven.java
```
```
java Seven ../pride-and-prejudice.txt
```

**8.1**: Run these commands:

```bash
javac Eight.java
```
```
java Eight ../pride-and-prejudice.txt
```

### Week 4:
**For all Week 4 exercise**: Open the Shell and run command:
```bash
cd week4
```
**9.1**: Run these commands:

```bash
javac Nine.java
```
```
java Nine ../pride-and-prejudice.txt
```

**10.1**: Run these commands:

```bash
javac Ten.java
```
```
java Ten ../pride-and-prejudice.txt
```

**15.1**: Run these commands:

```bash
javac Fifteen.java 
```
```
java Fifteen ../pride-and-prejudice.txt
```

### Week 5:
Open the Shell and run command:
```bash
cd week5
```
```
javac JarClasses.java
```
```
java JarClasses org.json-1.6-20240205.jar 
```

### Week 6:
**For all Week 6 exercise**: Open the Shell and run command:
```bash
cd week6
```
**29.1**: Run these commands:

```bash
javac TwentyNine.java 
```
```
java TwentyNine ../pride-and-prejudice.txt
```

**30.1**: Run these commands:

```bash
javac javac Thirty.java
```
```
java Thirty ../pride-and-prejudice.txt
```

**32.1**: Run these commands:

```bash
javac ThirtyTwo.java 
```
```
java ThirtyTwo ../pride-and-prejudice.txt
```

### Week 8:
**Open the Shell and run command:**
```bash
cd week8
```
```bash
javac Twenty.java 
```
```
java Twenty ../pride-and-prejudice.txt
```
Change the plugin classes inside config.ini

### Week 9:
**Open the Shell and run command:**
```bash
cd week9
```
```bash
javac Iterators.java 
```
```
java Iterators ../pride-and-prejudice.txt
```
```bash
javac Streams.java
```
```
java Streams ../pride-and-prejudice.txt
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
2. Type: `../pride-and-prejudice.txt`
3. The program will display the top 25 words and their frequencies

## File Paths

The program runs from the `week1/` directory, so:
- Files in `week1/` can be accessed directly: `filename.txt`
- Files in the root directory need `../` prefix: `../filename.txt` (It is default setted for both txt)

## Requirements

- Java Development Kit (JDK) 
- Text files for analysis
- Stop words file (already provided)
