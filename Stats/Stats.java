import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.text.DecimalFormat;
import java.lang.Object;
import java.util.regex.*;
import java.util.TreeMap;
import java.util.Map;

class Stats{
    private String document = "";
    private int[] lineIndex;
    private int[] wordIndex;
    private int lineCount = 0;
    private int wordCount = 0;
    private int charCount = 0;
    public static void main(String... args){
        Stats s = new Stats();
        s.UnitTest();
    }

    private void Initialise(String filePath){
        if(filePath == null){throw new Error("Please specify text file path as argument, e.g : 'java Stats test.txt'");}
        Scanner scan;
        ArrayList<Integer> lineInd = new ArrayList<>();
        ArrayList<Integer> wordInd = new ArrayList<>();
        try{ scan = new Scanner(new File(filePath)); }
        catch(Exception e){ e.printStackTrace(); return; }
        ScanIn(scan, lineInd, wordInd);
        scan.close();
        lineIndex = ConvertToArray(lineInd);
        wordIndex = ConvertToArray(wordInd);
    }

    private void ScanIn(Scanner scan, ArrayList<Integer> lineInd, ArrayList<Integer> wordInd){
        String line;
        lineInd.add(0);
        while(scan.hasNextLine()){
            line = scan.nextLine();
            int lastLineIndex = lineInd.get(lineInd.size()-1);
            ProcessLine(line, lineInd, lastLineIndex);
            ProcessWords(line, wordInd, lastLineIndex);
        }
        lineInd.remove(lineInd.size()-1);
    }

    private void ProcessLine(String line, ArrayList<Integer> lineInd, int lastLineIndex){
        lineInd.add(1 + line.length() + lastLineIndex);
        lineCount++;
        document = document + line + '\n';
    }

    private void ProcessWords(String line, ArrayList<Integer> wordInd, int thisLineIndex){
        String[] words;
        int linelen = line.length();
        line = line.replaceAll("^\\s+", "");
        words = line.split("\\s");
        for(String word : words){
            if(word.trim().length() == 0){ continue; }
            wordCount++;
            charCount += word.length();
            int thisWordIndex = linelen - line.replaceAll("^\\s+", "").length();
            line = line.substring(word.length()).replaceAll("^\\s+", "");
            wordInd.add(thisLineIndex + thisWordIndex);
        }
    }

    private int[] ConvertToArray( ArrayList<Integer> al){
        if(al == null){throw new Error("Cannot convert from null");}
        int[] ar = new int[al.size()];
        int i = 0;
        for(Integer in : al){
            ar[i] = in.intValue();
            i++;
        }
        return ar;
    }

    /** Gets the total number of non-whitespace characers*/
    private int CharCount(){ return charCount; }

    /** Gets the total number of characters, including whitespace */
    private int AllCharCount(){ return document.length(); }

    /** Gets the total number of words in the document*/
    private int WordCount(){ return wordCount; }

    /** Gets the total number of lines in the document*/
    private int LineCount(){ return lineCount; }

    /** Gets the average length of all words in the document*/
    private float AvgWordLen(){ return (float) charCount/wordCount; }

    /**TODO: make this a map with key, value ... string: count rename to string count
     * Finds which of the provided strings is the most common in the document
     * @param  list          The list of strings to search for
     * @param  caseSensitive Should this search be case sensitive?
     * @return               The string from arr that was most common. Null if none of the strings were present in the document
     */
    private String MostCommonString(List<String> list, boolean caseSensitive){
        String r = null;
        int max = 0;
        for(String search : list){
            int count = CountOcurranceString(document , search, caseSensitive).length;
            if(max < count){
                max = count;
                r = search;
            }
        }
        return r;
    }

    /**
     * Gets the number of ocurrances of one string inside another.
     * @param  in0           The String we are looking inside of
     * @param  match0        The String we are looking for
     * @param  caseSensitive Is this search case sensitive?
     * @return               The total number of ocurrances
     */
    private int[] CountOcurranceString(String in0, String match0, boolean caseSensitive){
        int index = 0;
        ArrayList<Integer> countarr = new ArrayList<Integer>();
        String in = in0;;
        String match = match0;
        if(!caseSensitive){
             in = in.toLowerCase();
             match = match.toLowerCase();
         }
        while(index != -1){
            index = in.indexOf(match,index);
            if(index != -1){
                index += match.length();
                countarr.add(index -1);
            }
        }
        return ConvertToArray(countarr);
    }

    /**
     * Returns a TreeMap containing all the matched strings from the regex and their starting index
     * @param  in    The String to search
     * @param  regex your regex expression as a String
     * @return       TreeMap Key = Integer, Value = String. null if nothing found
     */
    private TreeMap<Integer, String> MapFromRegex(String in, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(in);
        TreeMap<Integer, String> ret = new TreeMap<Integer, String>();
        boolean found = false;
        while(m.find()){
            ret.put(m.start(), m.group());
            found = true;
        }
        if(!found){ return null;}
        return ret;
    }

    private List<String> GetCharactersUsed(boolean caseSensitive){
        List<String> characters = new ArrayList<String>();
        for(int i = 0; i < document.length(); i++){
            String s = Character.toString(document.charAt(i));
            if(!caseSensitive){ s = s.toLowerCase(); }
            if(!characters.contains(s) && !Character.isWhitespace(s.charAt(0))){
                     characters.add(s);
             }
        }
        return characters;
    }

    private String GetStringFromLine(int lineNumber, int length){
        return document.substring(lineIndex[lineNumber], lineIndex[lineNumber]+length);
    }

    private String GetStringFromWord(int wordNumber, int length){
        return document.substring(wordIndex[wordNumber], wordIndex[wordNumber]+length);
    }

    private String GetStringFromDocument(int startIndex, int endIndex){
        if(startIndex < 0 || endIndex > document.length() ){throw new Error("Attempting to read beyond document bounds");}
        return document.substring(startIndex, endIndex);
    }

    void claim(boolean b) { if (!b) throw new Error("Test failure"); }

    private void UnitTest(){
        Initialise("test.txt");
        claim(WordCount() == 8);
        claim(LineCount() == 5);
        claim(CharCount() == 29);
        claim(AllCharCount() == 43);

        claim(String.format("%.1f",AvgWordLen()).equals("3.6"));
        claim("a".equals(MostCommonString(GetCharactersUsed(false),false)));
        claim("it has".equals(GetStringFromWord(3,6)));
        claim("AC".equals(GetStringFromLine(4,2)));
        claim(GetStringFromDocument(0,AllCharCount()).equals(document));

        TreeMap<Integer, String> m =  MapFromRegex(document, "\\w+");
        for(Map.Entry<Integer,String> e : m.entrySet()){
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }

}
