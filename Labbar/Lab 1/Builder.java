import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/*
    Rakin Ali CINTE19, KTH
    Adeel Hussain CINTE19, KTH
    Påbörjad 2021-09-11
*/


public class Builder 
{

    //Compare word lengths 
     public static long Iterate(String word_lookUP, int startPos, int lastPos, RandomAccessFile i_index) throws IOException
     {
        
        // Points to the I position we are currently looking
        long indexPointer = startPos;

        // 
        long index = 0; 
        
        //The length of the word we are looking for 
        int wordLength = word_lookUP.length();
        int checkLength = 0;
        boolean found = false;

        // First instance of the same three alfabets 
        i_index.seek(indexPointer);

        // Length of the word we iterate to 
        checkLength = i_index.read();

        // Iterate through startPos to lastPos to find the find  
        while((!found) && i_index.getFilePointer() < lastPos)
        {
            // If they have the same length, check the word else continue iterateration
            if(wordLength == checkLength)
            {
                //System.out.println("OK");
                // Bring the word
                byte[] checkWordByte = new byte[checkLength];
                indexPointer = i_index.getFilePointer();
                i_index.readFully(checkWordByte);
                String checkWord = new String(checkWordByte, "ISO-8859-1");
                
                if (word_lookUP.equals(checkWord))
                {
                    found = true;
                    // Move back 1 byte to begin pointer in start of the wordlength
                    index =  indexPointer - 1;
                }
                else
                {
                    indexPointer += checkLength + 4 + 4;
                    i_index.seek(indexPointer);
                    //System.out.println("FilePointer ELSE:" + i_index.getFilePointer());
                    checkLength = i_index.readByte();

                }
            }
            // If they do not have the same length
            else
            {
                //Iterate the word we are NOT looking for
                //Iterate wordlengthnumber + wordlength + 4 bytes of pos + 4 bytes of freq
                indexPointer += 1 + checkLength + 4 + 4;
                i_index.seek(indexPointer);
                //System.out.println("FilePointer:" + i_index.getFilePointer());
                checkLength = i_index.readByte();
             
            }            
        }
        return index; 
    }


    public static void find(String findWord, int[] hashTable)
    {
        try
        {
            // Access the files
            RandomAccessFile raf_I = new RandomAccessFile("Index_I", "r");
            RandomAccessFile raf_P = new RandomAccessFile("Index_P", "r");
            RandomAccessFile raf_L = new RandomAccessFile("korpus", "r");

            //Pull the 3 letters out then get the hash value
            String seekLetters = findWord.substring(0,3);
            int hashVal = hasher(seekLetters);
           
           // Go to I and get the word length 
            int look_I = hashTable[hashVal];
            int look_j = hashTable[hashVal+1];
            //raf_I.seek(look_I);
            long pos_found = Iterate(findWord, look_I, look_j, raf_I);
            raf_I.seek(pos_found);
            System.out.println("Where in I to look for word being searched : " + pos_found);
            int lengthOfWord = raf_I.read();
            System.out.println("Length of word were seeking is: " + lengthOfWord);

            // Word found! -> Reads the word and puts in the byte array. Print out later
            byte[] wordFromI = new byte[lengthOfWord];
            raf_I.readFully(wordFromI, 0, lengthOfWord);
            String wordFound = new String(wordFromI,"ISO-8859-1");

            System.out.println("The word we are searching for is found! : " + wordFound);

            // Extract information 
            int posOfP = raf_I.readInt();
            System.out.println("Where in P we are looking for: " + posOfP);
            
            int freqOfWord = raf_I.readInt();
            System.out.println("FreqOfTheWord: " + freqOfWord);

            raf_P.seek(posOfP);
            int whereInL = raf_P.readInt();
            System.out.println("Where In Index L Word occurs: " + whereInL);
    
            // Where the sentece will be stored
            byte[] scentenceFromL = new byte[60 + lengthOfWord];

            // Special case when word is before the 30th byte
            if(whereInL < 30) 
            {
                raf_L.seek(0);
                raf_L.readFully(scentenceFromL, 0, whereInL + lengthOfWord + 30 );

            }
            else // OBS!! NO SPECIAL CASE FOR LAST 30 BYTES
            {
                raf_L.seek(whereInL-30);
                raf_L.readFully(scentenceFromL, 0, 60 + lengthOfWord);
            }

            String scentenceFound = new String(scentenceFromL,"ISO-8859-1");
            System.out.println("The scentence we are seaching for is found! : " + scentenceFound);
            
            raf_I.close();
            raf_P.close();
            raf_L.close();
            

        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Här körs konstruktionsprogrammet
    public static void konstruktor() throws IOException 
    {
        String encoding = "ISO-8859-1";
        String fileToRead = "ISO-8859-1.txt";

        // textReader läser filen med encodning ISO-8859-1
        BufferedReader textReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead), encoding));
        
        // Create Index I, A and P files
        File a_index = new File("Index_A");
        FileOutputStream p_index= new FileOutputStream("Index_P");
        FileOutputStream i_index= new FileOutputStream("Index_I");

        // Creating writers to I, P and A - index-files 
        //OutputStreamWriter iWriter = new OutputStreamWriter(new BufferedOutputStream(i_index), encoding);
        DataOutputStream aWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a_index)))  ;
        DataOutputStream pWriter = new DataOutputStream(new BufferedOutputStream(p_index));
        DataOutputStream iWriter = new DataOutputStream(new BufferedOutputStream(i_index));

        // Temp variable that stores the word previous and then compares it to see if the word is unique or not.
        String word = "";
        
        // Temp variable that stores 3 alfabet combo and then compares with the new distint word
        String a_word = "";
        String a_check_word = " ";
        
        // Counts the frequencies of the word
        int wordfreq = 0;

        // The byte position of P, where in P we are writing to 
        int p_position = 0;
        int p_byteCounter = 0;

        // The byte position of I, where in I we are writing to 
        int i_position = 0;
        int i_byteCounter = 0;

        // Space in byte
        String space = " ";

        // New line in bytes
        String newLine = "\n";
        
        // Where we store the word read from textReader
        String data;
        
        // The byteIndex in Index L to Integer
        int binIndex;

        int hashval;

        int maxHashVal = 30*900 + 30*30 + 30;
        int[] hashTable = new int[maxHashVal + 1];
        
        // Pointers for redirecting empty non-used hash_positions of hashtable
        int pekareA = 0;
        int pekareB;

        try 
        {
            // While textDoc adds a new word and that word isn't null
            while ((data = textReader.readLine()) != null) 
            {
                // Splits line into 2 words
                String[] text_line = data.split(" ");
                
                // Index_Word stores the word in String. ByteIndex stores the byteIndex in Korpus file
                String index_Word = text_line[0];
                String byteIndex = text_line[1];

                // byteIndex converted from String to Int and stored in binIndex
                binIndex = Integer.parseInt(byteIndex);

                // Length of the word
                int lengthOfWord = index_Word.length();

                // Takes first 3 letters of the word writing to I
                if(lengthOfWord < 3)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(index_Word);
                    for(int i = 3 - lengthOfWord; i <= 3; i++)
                    {
                        sb.append(" ");
                    }

                    a_word = sb.toString();
                }
                else
                {
                    a_word = index_Word.substring(0, 3);
                }

                // If the word is Distinkt
                if (!(word.equals(index_Word))) 
                {
                    // and not the first word of the text, we have to add wordfreq to the previous word then continue with the algo
                    if (!(word.equals(""))) 
                    {
                       
                        //Add frequence
                        iWriter.writeInt(wordfreq);

                        // Reset the wordfreq
                        wordfreq = 0;

                        //
                        i_byteCounter += 4;
                    }
                    
                    // if a_word doesn't match check_word. add it to A_list array and file
                    if(!(a_check_word.equals(a_word)))
                        {
                            // Handles special case first time running the constructor
                            if(!(a_check_word.equals("")))
                            {
                                i_position = i_byteCounter;
                            }
                            //Get HashValue and insert it to Hashtable
                            hashval = hasher(a_word);
                            
                            pekareB = hashval;
                            // Fyll på tomma arrayPlatser med nästa ordet så 901 som är tom blir 930
                            while(pekareA < pekareB && pekareA < maxHashVal+1)
                            {
                                pekareA++;
                                aWriter.writeInt(pekareA);
                                aWriter.writeInt(pekareB);
                                
                            }
                            hashTable[hashval] = i_position;
                            pekareA = pekareB;
                            
                            // Now writing to A
                            aWriter.writeInt(hashval);
                            aWriter.writeInt(i_position);

                            //Update a_check_word
                            a_check_word = a_word;
                        }

                    // Updates the word variable, it checks the word behind.
                    word = index_Word;
                    
                    if(lengthOfWord < 3)
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(index_Word);
                        for(int i = 3 - lengthOfWord; i <= 3; i++)
                        {
                            sb.append(" ");
                        }
    
                        a_word = sb.toString();
                    }
                    else
                    {
                        a_word = index_Word.substring(0, 3);
                    }

                    // Writes the length of word were putting in Index I - 1 BYTE
                    iWriter.writeByte(lengthOfWord);
                    
                    //Converts word ISO-8859-1 to bytes
                    byte[] wordInBytes = index_Word.getBytes(encoding);

                    // Here we write the word to file I IN BYTES
                    iWriter.write(wordInBytes);

                    // Sets p_position to p_bytecounter before writing it out
                    p_position = p_byteCounter;

                    // Now we add the position
                    iWriter.writeInt(p_position);

                    // We write the ByteIndex in L to P - 4 Bytes
                    pWriter.writeInt(binIndex);
                    p_byteCounter += 4;

                    // Increments the frequency of the word
                    wordfreq++;

                    // Increment 1 byte for letter amount, 1 byte for each letter, 4 bytes for Integer containing pos
                    i_byteCounter += 1 + lengthOfWord + 4;

                }
                // If the word is not unique
                else 
                {
                    // We write the ByteIndex in L to P
                    pWriter.writeByte(binIndex);
                    p_byteCounter++;

                    // Counts the frequency of the word
                    wordfreq++;
                }
            }
            // Special case. WordFreq on the last word will not be written. We can fix this by adding it manually
            if ((data = textReader.readLine()) == null) 
            {
                iWriter.writeInt(wordfreq);
            }
            // Close the writers 
            iWriter.close();
            pWriter.close();
            aWriter.close();
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }    
        textReader.close();
        
    }

    public static int hasher(String tre_alfabetCombo)
    {
        char[] toHash;
        toHash = tre_alfabetCombo.toCharArray();
        int letter1 = toHash[0];
        int letter2 = toHash[1];
        int letter3 = toHash[2];

        //ASCII mellan a-z
        if (letter1 > 96 && letter1 < 123)
        {
            letter1 = letter1- 96;
        }
        else if (letter1 == 229) // Letter å
        {
            letter1 = 28;
        }
        else if (letter1 == 228) // Letter ä
        {
            letter1 = 29;
        }
        else if (letter1 == 246) // Letter ö
        {
            letter1 = 30;
        }
        else if (letter1 == 32) // Space
        {
            letter1 = 0;
        }

        //ASCII mellan a-z
        if (letter2 > 96 && letter2 < 123)
        {
            letter2 = letter2- 96;
        }
        else if (letter2 == 229) // Letter å
        {
            letter2 = 28;
        }
        else if (letter2 == 228) // Letter ä
        {
            letter2 = 29;
        }
        else if (letter2 == 246) // Letter ö
        {
            letter2 = 30;
        }
        else if (letter2 == 32) // Space
        {
            letter2 = 0;
        }

        //ASCII mellan a-z
        if (letter3 > 96 && letter3 < 123)
        {
            letter3 = letter3- 96;
        }
        else if (letter3 == 229) // Letter å
        {
            letter3 = 28;
        }
        else if (letter3 == 228) // Letter ä
        {
            letter3 = 29;
        }
        else if (letter3 == 246) // Letter ö
        {
            letter3 = 30;
        }
        else if (letter3 == 32) // Space
        {
            letter3 = 0;
        }

        int hashval = (letter1 * 900) + (letter2 * 30) + letter3;

        return hashval;
    }


    /*
                            TODO

        -Konstruktorfilen ska endast köras 1 gång (börja läsa från fil A)
        -Edge cases för första och sista orden är ej fixade
        -Små och stora bokstäver ska ej skiljas när vi söker ord
        -Newline ska ersättas med space vi utskrift av ord
        -Vi ska skriva ut 25 rader av förekomster
        -Ifall mer än 25 rader fråga användaren
        -Sökningar och utskriften sker under 1 sekund ifall förekomster < 25

    */

    public static void main(String[] args) throws IOException 
    {
        konstruktor();
        
    }
}
