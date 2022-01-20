import java.io.RandomAccessFile;
import java.util.Locale;
import java.io.IOException;

public class Konkordans 
{

    // Iterares through all the 3 alfabets combo till it finds the right word in index I 
    public static long Iterate(String word_lookUP, int startPos, int lastPos, RandomAccessFile i_index) throws IOException 
    {
        // Points to the I position we are currently looking
        long indexPointer = startPos;

        //
        long index = 0;

        // The length of the word we are looking for
        int wordLength = word_lookUP.length();
        int checkLength = 0;
        boolean found = false;

        // First instance of the same three alfabets
        i_index.seek(indexPointer);

        // Length of the word we iterate to
        checkLength = i_index.read();

        // Iterate through startPos to lastPos to find the find
        while ((!found) && i_index.getFilePointer() < lastPos) 
        {
            // If they have the same length, check the word else continue iterateration
            if (wordLength == checkLength) {
                // System.out.println("OK");
                // Bring the word
                byte[] checkWordByte = new byte[checkLength];
                indexPointer = i_index.getFilePointer();
                i_index.readFully(checkWordByte);
                String checkWord = new String(checkWordByte, "ISO-8859-1");

                if (word_lookUP.equals(checkWord)) {
                    found = true;
                    // Move back 1 byte to begin pointer in start of the wordlength
                    index = indexPointer - 1;
                } else {
                    indexPointer += checkLength + 4 + 4;
                    i_index.seek(indexPointer);
                    // System.out.println("FilePointer ELSE:" + i_index.getFilePointer());
                    checkLength = i_index.readByte();

                }
            }
            // If they do not have the same length
            else {
                // Iterate the word we are NOT looking for
                // Iterate wordlengthnumber + wordlength + 4 bytes of pos + 4 bytes of freq
                indexPointer += 1 + checkLength + 4 + 4;
                i_index.seek(indexPointer);
                // System.out.println("FilePointer:" + i_index.getFilePointer());
                checkLength = i_index.readByte();

            }
        }
        return index;
    }

    public static void find(String findWord) 
    {
        try {
            // Access the files
            RandomAccessFile raf_A = new RandomAccessFile("Index_A","r");
            RandomAccessFile raf_I = new RandomAccessFile("Index_I", "r");
            RandomAccessFile raf_P = new RandomAccessFile("Index_P", "r");
            RandomAccessFile raf_L = new RandomAccessFile("korpus.txt", "r");

            // Pull the 3 letters out then get the hash value
            String seekLetters = findWord.substring(0, 3);
            int hashVal = hasher(seekLetters);

            // Go to I and get the word length 
            raf_A.seek(hashVal*4);
            int startPosition = raf_A.readInt();
            raf_A.seek((hashVal*4)+4);
            int endPosition = raf_A.readInt();
            
            // Go to I and get the word length

            /*      Översättningen 
            int look_I = hashTable[hashVal];
            int look_j = hashTable[hashVal + 1];
            */

            long position_Found = Iterate(findWord, startPosition, endPosition, raf_I);
            raf_I.seek(position_Found);
            int lengt_word = raf_I.read();
            System.out.println("Length of the word we're seeking is: " + lengt_word);
            
            // ---- Översättningen ---- 
            /*
            long pos_found = Iterate(findWord, look_I, look_j, raf_I);
            raf_I.seek(pos_found);
            System.out.println("Where in I to look for word being searched : " + pos_found);
            int lengthOfWord = raf_I.read();
            System.out.println("Length of word were seeking is: " + lengthOfWord);
            */


            // Word found! -> Reads the word and puts in the byte array. Print out later
            byte[] wordFromI = new byte[lengt_word];
            raf_I.readFully(wordFromI, 0, lengt_word);
            String wordFound = new String(wordFromI, "ISO-8859-1");

            System.out.println("The word we are searching for is found! : " + wordFound);

            // Extract information
            int posOfP = raf_I.readInt();
            System.out.println("Where in P we are looking for: " + posOfP);

            int freqOfWord = raf_I.readInt();
            System.out.println("FreqOfTheWord: " + freqOfWord);

            raf_P.seek(posOfP);
            int whereInL_first = raf_P.readInt();
            System.out.println("Where In Index L Word occurs: " + whereInL_first);

            // Where the sentece will be stored
            byte[] scentenceFromL = new byte[60 + lengt_word];

            // Special case when word is before the 30th byte
            if (whereInL_first < 30) {
                raf_L.seek(0);
                raf_L.readFully(scentenceFromL, 0, whereInL_first + lengt_word + 30);

            } else // OBS!! NO SPECIAL CASE FOR LAST 30 BYTES
            {
                raf_L.seek(whereInL_first - 30);
                raf_L.readFully(scentenceFromL, 0, 60 + lengt_word);
            }

            String scentenceFound = new String(scentenceFromL, "ISO-8859-1");
            System.out.println("The scentence we are seaching for is found! : " + scentenceFound);

            raf_I.close();
            raf_P.close();
            raf_L.close();
            raf_A.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static int hasher(String tre_alfabetCombo) {
        char[] toHash;
        toHash = tre_alfabetCombo.toCharArray();
        int letter1 = toHash[0];
        int letter2 = toHash[1];
        int letter3 = toHash[2];

        // ASCII mellan a-z
        if (letter1 > 96 && letter1 < 123) {
            letter1 = letter1 - 96;
        } else if (letter1 == 229) // Letter å
        {
            letter1 = 28;
        } else if (letter1 == 228) // Letter ä
        {
            letter1 = 29;
        } else if (letter1 == 246) // Letter ö
        {
            letter1 = 30;
        } else if (letter1 == 32) // Space
        {
            letter1 = 0;
        }

        // ASCII mellan a-z
        if (letter2 > 96 && letter2 < 123) {
            letter2 = letter2 - 96;
        } else if (letter2 == 229) // Letter å
        {
            letter2 = 28;
        } else if (letter2 == 228) // Letter ä
        {
            letter2 = 29;
        } else if (letter2 == 246) // Letter ö
        {
            letter2 = 30;
        } else if (letter2 == 32) // Space
        {
            letter2 = 0;
        }

        // ASCII mellan a-z
        if (letter3 > 96 && letter3 < 123) {
            letter3 = letter3 - 96;
        } else if (letter3 == 229) // Letter å
        {
            letter3 = 28;
        } else if (letter3 == 228) // Letter ä
        {
            letter3 = 29;
        } else if (letter3 == 246) // Letter ö
        {
            letter3 = 30;
        } else if (letter3 == 32) // Space
        {
            letter3 = 0;
        }

        int hashval = (letter1 * 900) + (letter2 * 30) + letter3;

        return hashval;
    }

    public static void main(String[] args) 
    {
        String wordToFind = "atdt";
        find(wordToFind);
    }

}
