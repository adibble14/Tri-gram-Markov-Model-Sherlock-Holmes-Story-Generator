import java.io.*;
import java.util.*;

public class Tester {

    private static HashMap<String, LinkedList<Node>> myHashMap;

    public static void main(String[] args) {

        File file1 = new File("sign.txt"); //the first text file
        File file2 = new File("stud.txt"); //the second text file

        file1 = setUpFile(file1);
        file2 = setUpFile(file2);

        Markov myMarkov = new Markov(file1, file2);
        myHashMap = myMarkov.myHashMap;

        createStory();

    }

    /**
     * removes empty lines in file, removes the headers from the file
     */
    public static File setUpFile(File theFile) {
        Scanner file;
        PrintWriter writer;
        File newFile = new File(getNewPath(theFile.getPath()));
        try {
            file = new Scanner(theFile); //source file
            writer = new PrintWriter(newFile); //destination file
            int i = 0;

            while (file.hasNext()) {
                String line = file.nextLine();
                if (!line.isEmpty()) { //if there are characters
                    if (i > 3) { //skipping the header lines

                        line = line.replaceAll("\\s+", " "); //removing sequence of whitespace
                        line = line.replaceAll("[!\"#$%&'()\\*+,/:;?@^_`{|}~]", ""); //removing punctuation
                        line = line.replaceAll("[-]", " "); //replacing - with " "
                        line = line.toLowerCase(); //making the string all lower case

                        if (line.contains(".")) {
                            String[] parts = line.split("\\.");
                            for (int j = 0; j < parts.length; j++) {
                                String str;
                                if (j != parts.length - 1)
                                    str = parts[j] + " .\n";
                                else
                                    str = parts[j];
                                writer.write(str);
                            }
                        } else {
                            writer.write(line);
                        }
                    } else
                        i++;
                }
            }
            file.close();
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println("file not found");
        }
        return newFile;
    }

    /**
     * updates the text file name and path
     */
    public static String getNewPath(String thePath) {
        StringBuilder str = new StringBuilder(thePath);
        str.delete(str.length() - 4, str.length());
        str.append("1.txt");
        return str.toString();
    }

    /**
     * creates a story with the words from the text files
     */
    public static void createStory() {
        PrintWriter writer = null;
        try {
            File outputFile = new File("Readme.txt"); //the output text file
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }

        int wordCount = 0;
        String word = getRandomWord(); //getting the first word randomly
        assert writer != null;
        writer.write(word);
        wordCount++;

        boolean newline = false;
        while (wordCount < 2000) { //creating a 2000-word story

            //getting the first word via a Bigram
            int maxSize = 0;
            Node nextWord = null;
            for (Node n : myHashMap.get(word)) {
                if (n.numOccurrences > maxSize) {
                    maxSize = n.numOccurrences;
                    nextWord = n;
                }
            }

            //formatting
            assert nextWord != null;
            if (nextWord.firstWord.contains(".") || newline) {
                writer.write(nextWord.firstWord);
                newline = false;
            } else
                writer.write(" " + nextWord.firstWord);
            wordCount++;

            //getting the second word via a Trigram
            secondaryNode secondWord = nextWord.secondWords.stream().max(Comparator.comparing(secondaryNode -> secondaryNode.numOccurrences)).get();

            //formatting periods
            if (secondWord.secondWord.contains("."))
                writer.write(secondWord.secondWord);
            else
                writer.write(" " + secondWord.secondWord);
            wordCount++;

            if (wordCount % 10 == 1) { //adding a new line after 20 words and getting a new random word after 10 words
                word = getRandomWord();
                if (wordCount % 20 == 1) {
                    writer.write("\n");
                    newline = true;
                }
            } else {
                word = secondWord.secondWord;
            }


        }

        writer.close();
    }

    /**
     * returns a random word from the hash map
     */
    public static String getRandomWord() {
        Collection<String> words = myHashMap.keySet();
        List<String> wordList = new ArrayList<>(words);
        Random random = new Random();
        int randomIndex = random.nextInt(myHashMap.size());
        return wordList.get(randomIndex);
    }


}
