import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class Markov {

    private static File myFile1;
    private static File myFile2;
    HashMap<String, LinkedList<Node>> myHashMap;

    public Markov(File file1, File file2) {
        myFile1 = file1;
        myFile2 = file2;
        myHashMap = createMyHashMap();
    }

    /**
     * creates the hash map by going through the two files
     */
    public static HashMap<String, LinkedList<Node>> createMyHashMap() {
        HashMap<String, LinkedList<Node>> myHashMap = new HashMap<>();
        try {
            Scanner fileScanner1 = new Scanner(myFile1);
            Scanner fileScanner2 = new Scanner(myFile2);

            //adding words from the two text files to hash map
            addWords(fileScanner1, myHashMap);
            addWords(fileScanner2, myHashMap);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }

        return myHashMap;
    }

    /**
     * adding words to the hashmap from the text files
     *
     * @param theFileScanner the text file as a Scanner
     */
    public static void addWords(Scanner theFileScanner, HashMap<String, LinkedList<Node>> theHashMap) {
        //initializing words
        String word = theFileScanner.next();
        String nextWord = theFileScanner.next();
        String secondWord = theFileScanner.next();

        while (theFileScanner.hasNext()) {
            LinkedList<secondaryNode> secondWords = new LinkedList<>();
            secondWords.add(new secondaryNode(secondWord));

            if (theHashMap.containsKey(word)) { //checking to see if key already exists
                boolean hasNextWord = false;

                for (int i = 0; i < theHashMap.get(word).toArray().length; i++) {
                    if (Objects.equals(theHashMap.get(word).get(i).firstWord, nextWord)) { //if the first two words are already in hash map
                        hasNextWord = true;
                        theHashMap.get(word).get(i).numOccurrences++; //incrementing number times this word has shown up
                        try {
                            boolean inSecondWords = false;
                            for (secondaryNode n : theHashMap.get(word).get(i).secondWords) {
                                if (Objects.equals(n.secondWord, secondWord)) {
                                    n.numOccurrences++;
                                    inSecondWords = true;
                                    break;
                                }
                            }
                            if (!inSecondWords)
                                theHashMap.get(word).get(i).secondWords.add(new secondaryNode(secondWord)); //if first two words are in hash map but last one isn't
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                if (!hasNextWord) { //if the first word is in the hash map but not the second
                    theHashMap.get(word).add(new Node(nextWord, secondWords));
                }
            } else { //if the word isn't in the hash map
                //updating LinkedLists
                LinkedList<Node> firstWords = new LinkedList<>();
                firstWords.add(new Node(nextWord, secondWords));
                theHashMap.put(word, firstWords); //adding to hashmap
            }

            //updating word values
            word = nextWord;
            nextWord = secondWord;
            secondWord = theFileScanner.next();
        }

        //adding the last few words of text file
        LinkedList<Node> firstWords = new LinkedList<>();
        LinkedList<secondaryNode> secondWords = new LinkedList<>();
        secondWords.add(new secondaryNode(secondWord));
        firstWords.add(new Node(nextWord, secondWords));

        theHashMap.put(word, firstWords); //adding to hashmap
    }

}

/**
 * node used to store the next word in the sequence
 */
class Node {
    LinkedList<secondaryNode> secondWords;
    int numOccurrences;
    String firstWord;

    public Node(String theFirstWord, LinkedList<secondaryNode> theSecondWords) {
        firstWord = theFirstWord;
        secondWords = theSecondWords;
        numOccurrences = 1;
    }

}

/**
 * node used to store the third word in the sequence
 */
class secondaryNode {
    int numOccurrences;
    String secondWord;

    public secondaryNode(String theFirstWord) {
        secondWord = theFirstWord;
        numOccurrences = 1;
    }


}

