
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.Scanner;

class Konkordans2 {

    private static File positionFil = new File("/var/tmp/positionFils.txt");
    private static File indexFil = new File("/var/tmp/indexFilsen.txt");
    private static File latmanArray = new File("/var/tmp/latmanArrays.txt");

    public static void main(String[] args) throws IOException, InterruptedException {

        if(args.length == 0){
            System.out.println("Påbörjar Konkordans");
            createKonkordans(positionFil, indexFil, latmanArray);
            System.exit(1);
            }

        String sokOrd = args[0].toLowerCase();
        if (sokOrd == null || (sokOrd.matches("^[^a-zå-ö]+$")) || args.length > 1) {
            System.out.println("Inget Ord, Sorry. Lämnar programmet!");
            System.exit(0);
        }


        RandomAccessFile rKorpus = new RandomAccessFile("/afs/kth.se/misc/info/kurser/DD2350/adk21/labb1/korpus", "r");
        RandomAccessFile rPos = new RandomAccessFile(positionFil, "r");

        String[] indexOrd = sokAlgoritm(sokOrd);

        if (indexOrd == null) {
            System.out.println("Ditt ord finns inte");
            System.exit(1);
        }
        int ipPos = Integer.parseInt(indexOrd[1]);
        int antal = Integer.parseInt(indexOrd[2]);
        if (antal > 25) {
            System.out.println("Ditt ord har " + antal + " förekomster, vill du skriva ut alla?Skriv: ja eller nej");
            Scanner sc = new Scanner(System.in);
            String svar = sc.nextLine();
            sc.close();
            if (svar.equals("ja")) {    

            } else {
                System.out.println("okej, hejdå!");
                System.exit(1);
            }
        }
        System.out.println("Ditt ord har " + antal + " förekomster");
        rPos.seek(ipPos);
        byte[] mening = new byte[30 + sokOrd.length() + 30];
        int line = 0;
        for (int i = 0; i < antal; i++) {
            line = rPos.readInt();
            if (line < 30) {
                rKorpus.seek(line);
            } else {
                rKorpus.seek(line - 30);
            }
            rKorpus.read(mening);
            System.out.println(new String(mening, "ISO-8859-1").replaceAll("[\\n\\t]", " "));
        }
        rKorpus.close();
        rPos.close();

    }

    /*
     * Skapar konkordans databas baserat på en vald text och dess tokenizerade fil
     * givet från rawindex.txt
     */
    private static void createKonkordans(File positionFil, File indexFil, File latmanArray)throws IOException, InterruptedException {
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("/afs/kth.se/misc/info/kurser/DD2350/adk21/labb1/rawindex.txt"), StandardCharsets.ISO_8859_1));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFil), StandardCharsets.ISO_8859_1));
        DataOutputStream Ipfil = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(positionFil)));
        RandomAccessFile latmanOut = new RandomAccessFile(latmanArray, "rw");
        latmanOut.setLength((26999) * 8);

        String[] row = in.readLine().split(" ");
        String currentWord = row[0], newWord = "", latmanWord = row[0];
        long startPos = 0, currentPos = 0;
        int startIpPos = 0, IpPos = 0;
        int occurrences = 1;

        while (in.ready()) {
            Ipfil.writeInt(Integer.parseInt(row[1]));
            row = in.readLine().split(" ");
            newWord = row[0];
            IpPos += 4;
            if (!newWord.equals(currentWord)) {
                out.write((currentWord + " " + startIpPos + " " + occurrences + "\n"));
                currentPos += (currentWord + " " + startIpPos + " " + occurrences + "\n").length();
                if (preCalcFunc(latmanWord) != preCalcFunc(newWord)) {
                    latmanOut.seek(preCalcFunc(latmanWord));
                    latmanOut.writeLong(startPos);
                    for (long a = preCalcFunc(latmanWord) + 8; a < preCalcFunc(newWord); a += 8) {
                        latmanOut.seek(a);
                        latmanOut.writeLong(currentPos);
                    }
                    latmanWord = newWord;
                    startPos = currentPos;
                }

                currentWord = newWord;
                startIpPos = IpPos;
                occurrences = 0;
            } else if (!in.ready()) {
                occurrences++;
                Ipfil.writeInt(Integer.parseInt(row[1]));
                out.write((currentWord + " " + startIpPos + " " + occurrences + "\n"));
                latmanOut.seek(preCalcFunc(latmanWord));
                latmanOut.writeLong(startPos);
                for (long a = preCalcFunc(latmanWord) + 8; a <= preCalcFunc(newWord); a += 8) {
                    System.out.println(a);
                    latmanOut.seek(a);
                    latmanOut.writeLong(currentPos);
                }

            }
            occurrences++;
        }

        System.out.println("konkordans klar");
        latmanOut.close();
        out.close();
        in.close();
        Ipfil.close();

    }

    /*
     * Tar emot ett ord som parameter och hashar det ordet med hjälp av en
     * förberäknad funktion på dem 3 första bokstäverna i ordet.
     */
    private static long preCalcFunc(String sokOrd) {
        long n = 0;
        char[] wprefix;
        if (sokOrd.length() < 3) {
            wprefix = sokOrd.toCharArray();
        } else {
            sokOrd = sokOrd.substring(0, 3);
            wprefix = sokOrd.toCharArray();
        }

        for (int i = 0; i < wprefix.length; i++) {
            if ((int) wprefix[i] == 228) {
                n += 27 * Math.pow(30, 2 - i);
            } else if ((int) wprefix[i] == 229) {
                n += 28 * Math.pow(30, 2 - i);
            } else if ((int) wprefix[i] == 246) {
                n += 29 * Math.pow(30, 2 - i);
            } else {
                n += ((int) wprefix[i] - 96) * Math.pow(30, 2 - i);
            }
        }
        return (n - 900) * 8;
    }

    private static String[] sokAlgoritm(String sokOrd) throws IOException {
        RandomAccessFile rLatman = new RandomAccessFile("/var/tmp/latmanArrays.txt", "r");
        RandomAccessFile rIndex = new RandomAccessFile("/var/tmp/indexFilsen.txt", "r");
        rLatman.seek(preCalcFunc(sokOrd));
        long i = rLatman.readLong();
        rLatman.seek(preCalcFunc(sokOrd) + 8);
        long j = rLatman.readLong();
        long m = 0;
        String[] s = new String[3];
        while (i - j > 1000 && j > i) {
            m = (i + j) / 2;
            rIndex.seek(m);
            s = rIndex.readLine().split(" ");
            int comp = s[0].compareTo(sokOrd);
            if (comp == 0) {
                break;
            } else if (comp < 0) {
                i = m;
            } else {
                j = m;
            }
        }
        rIndex.seek(i);
        while (true) {
            s = rIndex.readLine().split(" ");
            if (s[0].compareTo(sokOrd) == 0) {
                rLatman.close();
                rIndex.close();
                return s;
            } else if (s[0].compareTo(sokOrd) > 0) {
                rLatman.close();
                rIndex.close();
                return null;
            }
        }
    }
}