import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.text.DecimalFormat;
import java.lang.Object;
import java.util.regex.*;
import java.util.TreeMap;
import java.util.Map;
import java.util.*;


// Need a way of giving the 'document' to the user
// Could keep 'document' private, put a wrapper around it. arguments take in a Stats instance as argument?
// This prevents methods being used on anything but the entire document, unless they create multiple instances of 'Stats' - bad for Memory
//
class Stats{

    private String document = "";
    private int[] lineIndex;
    private int[] wordIndex;
    private int lineCount = 0;
    private int wordCount = 0;
    private int charCount = 0;
    private boolean initialised = false;

    Stats(){}
    Stats(String filePath){ Initialise(filePath);}

    /** Reads in a file ready to produce stats on it
    * @param filePath The path of the file that you wish to read in
    */
    public void Initialise(String filePath){
        if(initialised){ throw new Error("Cannot re-initialse an already initialised Stats object. call Reset() first");}
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

    /** Resets this Stats object to it's un-initialised state */
    public void Reset(){
        document = "";
        lineIndex = null;
        wordIndex = null;
        lineCount = 0;
        wordCount = 0;
        charCount = 0;
        initialised = false;
    }

    /** Gets the total number of non-whitespace characers*/
    public int CharCount(){ return charCount; }

    /** Gets the total number of characters, including whitespace */
    public int AllCharCount(){ return document.length(); }

    /** Gets the total number of words in the document*/
    public int WordCount(){ return wordCount; }

    /** Gets the total number of lines in the document*/
    public int LineCount(){ return lineCount; }

    /** Gets the average length of all words in the document*/
    public float AvgWordLen(){ return (float) charCount/wordCount; }



    /* returns histogram of words, takes list of words to make histogram of */
    public TreeMap<String, Integer> StringsCount(List<String> list, boolean caseSensitive){
        if(list == null){return null;}
        TreeMap<String, Integer> ret  = new  TreeMap<String, Integer>();
        for(String search : list){
            ret.put(search, CountOcurranceString(document , search, caseSensitive) );
        }
        return ret;
    }

    /* returns histogram of words, takes in any tree map where values are words to search for */
    public TreeMap<String, Integer> StringsCount(TreeMap<Integer, String> mapIn, boolean caseSensitive){
        if(mapIn == null){ return null; }
        List<String> list = new ArrayList<String>(mapIn.values());
        return StringsCount(list, caseSensitive);
    }

    public TreeMap<String, Integer> StringsCount(String[] arr, boolean caseSensitive){
        if(arr == null){ return null; }
        List<String> list = new ArrayList<>(Arrays.asList(arr));
        return StringsCount(list, caseSensitive);
    }

    /**
     * Finds which of the provided strings is the most common in the document
     * @param  list          The list of strings to search for
     * @param  caseSensitive Should this search be case sensitive?
     * @return               The string from arr that was most common. Null if none of the strings were present in the document
     */
        public String MostCommonString(List<String> list, boolean caseSensitive){
            if(list == null){return null;}
            String r = null;
            int max = 0;
            for(String search : list){
                int count = CountOcurranceString(document , search, caseSensitive);
                if(max < count){
                    max = count;
                    r = search;
                }
            }
            return r;
        }


//TODO add 'wholeWord' boolean argument to CountOccuranceString, at the moment it doesn't
//TODO  do this by default so the word 'a' appears 9 times instead of once

    /** Gets the number of ocurrances of one string inside another.
     * @param  in0           The String we are looking inside of
     * @param  match0        The String we are looking for
     * @param  caseSensitive Is this search case sensitive?
     * @return               The total number of ocurrances
     */
    public int CountOcurranceString(String in0, String match0, boolean caseSensitive){
        int index = 0;
        int count = 0;
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
                count++;
            }
        }
        return count;
    }

    /** Returns a TreeMap containing all the matched strings from the regex and their starting index
     * @param  in    The String to search
     * @param  regex your regex expression as a String
     * @return       TreeMap Key = Integer, Value = String. null if nothing found
     */
    public TreeMap<Integer, String> MapFromRegex(String in, String regex){
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

    /** Gets a list of unique characters used in this document
    * @param caseSensitive is this search case sensitive?
    */
    public List<String> GetCharactersUsed(boolean caseSensitive){
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

    public String GetStringFromLine(int startLineNumber, int endLineNumber){
        return document.substring(lineIndex[startLineNumber], lineIndex[endLineNumber]);
    }

    public String GetStringFromWord(int startWordNumber, int endWordNumber){
        return document.substring(wordIndex[startWordNumber], wordIndex[endWordNumber]);
    }

    public String GetStringFromDocument(int startIndex, int endIndex){
        if(startIndex < 0 || endIndex > document.length() ){throw new Error("Attempting to read beyond document bounds");}
        return document.substring(startIndex, endIndex);
    }

    public int GetLineIndex(int lineNumber){ return lineIndex[lineNumber]; }

    public int GetWordIndex(int wordNumber){ return wordIndex[wordNumber]; }

    //TODO  check GetWordIndex + GetLineIndex methods,
    //TODO  change GetStringFromLine and GetStringFromWord
    //TODO  to take endword number as second argument instead of lenght

    /** Scans the file into the relevant fields */
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

    /** Scans in a line of the file, populates lineIndex and lineCount fields */
    private void ProcessLine(String line, ArrayList<Integer> lineInd, int lastLineIndex){
        lineInd.add(1 + line.length() + lastLineIndex);
        lineCount++;
        document = document + line + '\n';
    }

    /** Scans in each word in a line, populates wordIndex, wordCount and charCount */
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

    /** Converts an ArrayList of Integers to primitive int array. Memory optimisation */
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

    ////////////////////////////   TESTING    ////////////////////////////////////

    public static void main(String... args){
        Stats s = new Stats();
        s.UnitTest();
    }

    private void claim(boolean b) { if(!b){ throw new Error("Test failure"); } }

    private void UnitTest(){
        System.out.print("Testing...");
        Initialise("test.txt");

        //Standard getters (checking that initialise and all sub methods work corrextly)
        claim(WordCount() == 8);
        claim(LineCount() == 5);
        claim(CharCount() == 29);
        claim(AllCharCount() == 43);
        claim(String.format("%.1f",AvgWordLen()).equals("3.6"));

        //Indecies
        claim(LineCount() == lineIndex.length);
        claim(WordCount() == wordIndex.length);
        claim(lineIndex[1] == 10);
        claim(lineIndex[4] == 34);
        claim(wordIndex[2] == 6);
        claim(wordIndex[4] == 16);
        claim(wordIndex[5] == 20);
        claim(wordIndex[7] == 34);

        //String Retrival
        claim("a".equals(MostCommonString(GetCharactersUsed(false),false)));
        claim("it has".equals(GetStringFromWord(3,6)));
        claim("AC".equals(GetStringFromLine(4,2)));
        claim(GetStringFromDocument(0,AllCharCount()).equals(document));

        //CountOccuranceString
        claim(CountOcurranceString("aaAAA","A", true) == 3);
        claim(CountOcurranceString("aaAAA","A", false) == 5);
        claim(CountOcurranceString("aaAAA","a", true) == 2);
        claim(CountOcurranceString("aaAAA","a", false) == 5);

        //MapFromRegex
        claim(MapFromRegex(document, "t\\w+").size() == 1);
        claim(MapFromRegex(document, "\\w+").size() == 8);
        claim(MapFromRegex(document, "(?m)^.*$").size() == 5);
        claim(MapFromRegex(document, "\\w").size() == 29);
        // checks that the key and value.length() from the map can be used
        // to get the exact same string by pulling a substring directly from document
        TreeMap<Integer, String> m =  MapFromRegex(document, "\\w+");
        for(Map.Entry<Integer,String> e : m.entrySet()){
            int wrdIndex = e.getKey();
            String docSubString = GetStringFromDocument(wrdIndex, wrdIndex + e.getValue().length());
            claim(e.getValue().equals(docSubString));
        }

        //StringsCount
        TreeMap<String, Integer> n = StringsCount(m,false);
        claim(n.size() == WordCount());
        for(Map.Entry<String, Integer> en : n.entrySet()){
            System.out.println(en.getKey() + " " + en.getValue());
            //claim(en.getValue() == 1);
        }
        System.out.println(" Successful");
    }


}

/*
eample Regex expressions:
"\\w+"                              - Any Word
"t\\w+"                             - Any word beginning with t
"\\w+s"                             - Any word ending in s
"\\w*[ic]\\w*"                      - Any word containing the letters c or i (case sensitive)
"\\w*[cC]{2,}\\w*"                  - Any word with two or more consecutive c's (case insensitive)
"\\b\\w{5}\\b"                      - Any 5 letter word
"\\b\\w{2,5}\\b"                    - Any word between 2 and 5 letters long
"(?m)^.*$"                          - Any Line
"(?m)^.*[one|two|three].*$"         - Any Line containing "one", "two" or "three" anywhere in the line
"(?m)^.*\\b(one|two|three)\\b.*$"   - Any line containing "one", "two", or "three" as whole words (e.g. won't return a line with "twentythree" in it)

For full explanation of regex see here: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#lt
*/
