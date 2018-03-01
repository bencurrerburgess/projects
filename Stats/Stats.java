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

/** A class for reading in text files and producing statistics on them
 *  <p>Standard statistics are available directly through getters, see all
 *  methods ending 'Count'.
 *  </p>
 *  Other methods provide:
 *  <ul>
 *  <li> Find Strings matching Regex - see MapFromRegex </li>
 *  <li> Find Strings using index of words and lines - see 'GetStringFrom' methods </li>
 *  <li> Count of strings - see StringsCount </li>
 *  <li> Convert String list / map to unique string occurances - see UniqueStrings </li>
 *  </ul>
 */
class Stats{

    private String document = "";
    private int[] lineIndex;
    private int[] wordIndex;
    private int lineCount = 0;
    private int wordCount = 0;
    private int charCount = 0;
    private boolean initialised = false;

    /** Creates an empty Stats instance
     * @see     Stats(String)
     *          <li>Initialise(String)</li>
     */
    Stats(){}

    /** Initialises the Stats object with the file at the provided file path
         * @param filePath The path of the file that you wish to read in
         * @see Initialise(String)
         */
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
        if(!initialised){ return; }
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

    /** Gets the entire document as a single string */
    public String GetDocument(){ return document; }

    /** Produces a histogram of words in the form of a TreeMap
     * @param  in            The String to search
     * @param  list          The list of Strings to search for
     * @param  caseSensitive Should this search be case sensitive?
     * @param  wholeWord     Should this search only count whole words?
     * @return <Strong>Key</Strong> is the word
     *         <li> <strong>Integer</Strong> is the count of occurances of that word </li>
     */
    public TreeMap<String, Integer> StringsCount(String in, List<String> list, boolean caseSensitive, boolean wholeWord){
        if(list == null){return null;}
        TreeMap<String, Integer> ret  = new  TreeMap<String, Integer>();
        for(String search : list){
            ret.put(search, CountOcurranceString(in , search, caseSensitive, wholeWord ) );
        }
        return ret;
    }

    /** Produces a histogram of words in the form of a TreeMap
     * @param  in            The String to search
     * @param  mapIn         A Map String Values to search for
     * @param  caseSensitive Should this search be case sensitive?
     * @param  wholeWord     Should this search only count whole words?
     * @return <Strong>Key</Strong> is the word
     *         <li> <strong>Integer</Strong> is the count of occurances of that word </li>
     */
    public TreeMap<String, Integer> StringsCount(String in, TreeMap<Integer, String> mapIn, boolean caseSensitive, boolean wholeWord){
        if(mapIn == null){ return null; }
        List<String> list = new ArrayList<String>(mapIn.values());
        return StringsCount(in, list, caseSensitive, wholeWord);
    }

    /** Produces a histogram of words in the form of a TreeMap
     * @param  in            The String to search
     * @param  arr           An array of String Values to search for
     * @param  caseSensitive Should this search be case sensitive?
     * @param  wholeWord     Should this search only count whole words?
     * @return <Strong>Key</Strong> is the word
     *         <li> <strong>Integer</Strong> is the count of occurances of that word </li>
     */
    public TreeMap<String, Integer> StringsCount(String in, String[] arr, boolean caseSensitive, boolean wholeWord){
        if(arr == null){ return null; }
        List<String> list = new ArrayList<>(Arrays.asList(arr));
        return StringsCount(in, list, caseSensitive, wholeWord);
    }

    /** Finds which of the provided strings is the most common in the document.
     * <p>If two or more strings from the list are joint max, the first string found will be returned</p>
     * @param  list          The list of strings to search for
     * @param  caseSensitive Should this search be case sensitive?
     * @param  wholeWord     Should this search only count whole words?
     * @return               The string from arr that was most common. Null if none of the strings were present in the document
     */
        public String MostCommonString(List<String> list, boolean caseSensitive, boolean wholeWord){
            if(list == null){return null;}
            String ret = null;
            int max = 0;
            for(String search : list){
                int count = CountOcurranceString(document , search, caseSensitive, wholeWord);
                if(max < count){
                    max = count;
                    ret = search;
                }
            }
            return ret;
        }

    /** Gets the number of ocurrances of one string inside another.
     * @param  in0           The String we are looking inside of
     * @param  match0        The String we are looking for
     * @param  caseSensitive Is this search case sensitive?
     * @return               The total number of ocurrances
     */

    /** Gets the number of ocurrances of one string inside another.
     * @param  in            The String to search
     * @param  match         The String to find
     * @param  caseSensitive Should this search be case sensitive?
     * @param  wholeWord     Should this search only count whole words?
     * @return               The count of 'match's in 'in'
     */
    public int CountOcurranceString(String in, String match, boolean caseSensitive, boolean wholeWord){
        String regex = match;
        if(!caseSensitive){ regex = "(?i)" + regex; }
        if(wholeWord){ regex = ".*\\b" + regex + "\\b.*"; }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(in);
        int count = 0;
        while(m.find()){ count++; }
        return count;
    }

    /** Returns a TreeMap containing all the matched strings from the regex and their starting index
     * @param  in    The String to search
     * @param  regex The regex expression to match as a string
     * @return <Strong>Key</Strong> is the starting index of the word
     *         <li> <strong>Value</Strong> is a String that matched the regex expression </li>
     */
    public TreeMap<Integer, String> MapFromRegex(String in, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(in);
        TreeMap<Integer, String> ret = new TreeMap<Integer, String>();
        while(m.find()){
            ret.put(m.start(), m.group());
        }
        if(ret.size() < 1){ return null;}
        return ret;
    }

    /** Gets a list of unique characters used in this document
    * @param caseSensitive Should this search be case sensitive?
    * @return  List of characters stored as String in the order that those
    * characters appear in the document
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

    /** Converts a collection of strings to a list of unique strings
     * @param  list          The list of strings to be made unique
     * @param  caseSensitive Should this search be case sensitive? If flase, the returned list will be in lower case
     * @return               A unique list of strings, in the order they appear in the input list
     */
    public List<String> UniqueStrings(List<String> list, boolean caseSensitive){
        if(list == null){ return null; }
        List<String> ret = new ArrayList<String>();
        for(String s : list){
            if(!caseSensitive){ s = s.toLowerCase(); }
            if(!ret.contains(s)){ ret.add(s); }
        }
        return ret;
    }

    /** Converts a collection of strings to a list of unique strings
     * @param  map           A map containing strings to be made unique
     * @param  caseSensitive Should this search be case sensitive? If flase, the returned list will be in lower case
     * @return               A unique list of strings, in the order they appear in the input map
     */
    public List<String> UniqueStrings(TreeMap<Integer, String> map, boolean caseSensitive){
        if(map == null){ return null; }
        List<String> list = new ArrayList<String>(map.values());
        return UniqueStrings(list, caseSensitive);
    }

    /** Converts a collection of strings to a list of unique strings
     * @param  arr           An array containing strings to be made unique
     * @param  caseSensitive Should this search be case sensitive? If flase, the returned list will be in lower case
     * @return               A unique list of strings, in the order they appear in the input map
     */
    public List<String> UniqueStrings(String[] arr, boolean caseSensitive){
        if(arr == null){ return null;}
        List<String> list = new ArrayList<String>(Arrays.asList(arr));
        return UniqueStrings(list, caseSensitive);
    }

    /** Gets the contents between the given line numbers
     * @param  startLineNumber The line number to start at (0 is the first line)
     * @param  endLineNumber   The line number to end at (finishes at the start of that line)
     * @return                 The string between the two provided points in the document
     */
    public String GetStringFromLine(int startLineNumber, int endLineNumber){
        if(startLineNumber > endLineNumber || endLineNumber < 0 || startLineNumber < 0 || endLineNumber > lineCount){ return null; }
        return TrimEnd(document.substring(lineIndex[startLineNumber], lineIndex[endLineNumber]));

    }

    /** Gets the contents between the given word numbers
     * @param  startWordNumber The word number to start at (0 is the first word)
     * @param  endWordNumber   The word number to end at (finishes at the start of that word)
     * @return                 The string between the two provided points in the document
     */
    public String GetStringFromWord(int startWordNumber, int endWordNumber){
        if(startWordNumber > endWordNumber || endWordNumber < 0 || startWordNumber < 0 || endWordNumber > wordCount){ return null; }
        return TrimEnd(document.substring(wordIndex[startWordNumber], wordIndex[endWordNumber]));
    }

    /** Gets a substring from the document based on the start and end indexes
     * @param  startIndex The index to start at (0 is the beginning of the document)
     * @param  endIndex   The index to End at
     * @return            The selected substring
     */
    public String GetStringFromDocument(int startIndex, int endIndex){
        if(startIndex < 0 || endIndex > document.length() ){throw new Error("Attempting to read beyond document bounds");}
        return document.substring(startIndex, endIndex);
    }

    /** Gets the index in the document at the start of the given line number
     * @param  lineNumber The line number that you want to get the index of
     * @return            The index at the beginning of the given line number
     */
    public int GetLineIndex(int lineNumber){ return lineIndex[lineNumber]; }

    /** Gets the index in the document at the start of the given word number
     * @param  wordNumber The word number that you want to get the index of
     * @return            The index at the beginning of the given word number
     */
    public int GetWordIndex(int wordNumber){ return wordIndex[wordNumber]; }

    /** Trims only the end of a provided string */
    private String TrimEnd(String s){
        return s.replaceFirst("\\s++$", "");
    }

    /** Scans in the file and populates all fields appropriately */
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

    ////////////////////////////   TESTING    //////////////////////////////////
    public static void main(String... args){
        Stats s = new Stats();
        s.UnitTest();
    }

    // A replacement for Assert to avoid having to use the compile flag
    private void claim(boolean b) { if(!b){ throw new Error("Test failure"); } }

    // Automated tests to ensure the class is working as expected
    private void UnitTest(){
        System.out.print("Testing...");
        Initialise("test.txt");

        ///////////////////////////////  Initialise  ///////////////////////////
        claim(WordCount() == 8);
        claim(LineCount() == 5);
        claim(CharCount() == 29);
        claim(AllCharCount() == 43);
        claim(String.format("%.1f",AvgWordLen()).equals("3.6"));

        //////////////////////////////  Indecies  //////////////////////////////
        claim(LineCount() == lineIndex.length);
        claim(lineCount == lineIndex.length);
        claim(WordCount() == wordIndex.length);
        claim(wordCount == wordIndex.length);
        //GetLineIndex
        claim(GetLineIndex(0) == 0);
        claim(GetLineIndex(1) == 10);
        claim(GetLineIndex(2) == 32);
        claim(GetLineIndex(3) == 33);
        //GetWordIndex
        claim(GetWordIndex(0) == 0);
        claim(GetWordIndex(1) == 3);
        claim(GetWordIndex(2) == 6);
        claim(GetWordIndex(7) == 34);

        claim(GetWordIndex(7) == GetLineIndex(4));

        //////////////////////////  String retrival  ///////////////////////////
        claim("a".equals(MostCommonString(GetCharactersUsed(false),false,false)));

        //GetStringFromWord
        claim("it".equals(GetStringFromWord(3,4)));
        claim("it has three lines".equals(GetStringFromWord(3,7)));
        claim(GetStringFromWord(-1,1) == null);
        claim(GetStringFromWord(2,1) == null);
        claim(GetStringFromWord(1,-1) == null);
        claim(GetStringFromWord(1, 15) == null);
        //GetStringFromLine
        claim("a  bb ccc".equals(GetStringFromLine(0,1)));
        claim(GetStringFromLine(-1,1) == null);
        claim(GetStringFromLine(2,1) == null);
        claim(GetStringFromLine(1,-1) == null);
        claim(GetStringFromLine(1,6) == null);

        claim(GetStringFromDocument(0,AllCharCount()).equals(document));

        //////////////////////  CountOccuranceString  //////////////////////////
        String str = "a  bb ccc\n   it has three lines\n\n\nACaaaaaa";
        //case sensitivity
        claim(CountOcurranceString("aaAAA","A", true, false) == 3);
        claim(CountOcurranceString("aaAAA","A", false, false) == 5);
        claim(CountOcurranceString("aaAAA","a", true, false) == 2);
        claim(CountOcurranceString("aaAAA","a", false, false) == 5);
        claim(CountOcurranceString("a bb ccc", "bb", true, true) == 1);
        claim(CountOcurranceString("a bb ccc", "BB", true, true) == 0);
        //multi-word Strings
        claim(CountOcurranceString("a bb ccc", "a bb", true, true) == 1);
        claim(CountOcurranceString("a bb  ccc", "bb  ccc", true, true) == 1);
        claim(CountOcurranceString(str,"ccc\n   it",false,true) == 1);
        claim(CountOcurranceString(str,"ccc\n   it has",true,true) == 1);
        claim(CountOcurranceString(str,"ccc\n   it has thre",true,true) == 0);
        claim(CountOcurranceString(str,"ccc\n   it has three",true,true) == 1);
        //whole word identification
        claim(CountOcurranceString("a ba cac", "a", true, true) == 1);
        claim(CountOcurranceString("a ba cac", "a", true, false) == 3);
        claim(CountOcurranceString(str,"a",false,true) == 1);
        claim(CountOcurranceString(str,"bb",false,true) == 1);
        claim(CountOcurranceString(str,"ccc",false,true) == 1);
        claim(CountOcurranceString(str,"it",false,true) == 1);
        claim(CountOcurranceString(str,"has",false,true) == 1);
        claim(CountOcurranceString(str,"three",false,true) == 1);
        claim(CountOcurranceString(str,"lines",false,true) == 1);
        claim(CountOcurranceString(str,"ACaaaaaa",false,true) == 1);

        //////////////////////////  MapFromRegex  //////////////////////////////
        // words beginning t
        claim(MapFromRegex(document, "t\\w+").size() == 1);
        //all words
        claim(MapFromRegex(document, "\\w+").size() == 8);
        //all lines
        claim(MapFromRegex(document, "(?m)^.*$").size() == 5);
        //all non-whitespace characters
        claim(MapFromRegex(document, "\\w").size() == 29);

        // checks that the key and value.length() from the map can be used
        // to get the exact same string by pulling a substring directly from document
        TreeMap<Integer, String> m =  MapFromRegex(document, "\\w+");
        for(Map.Entry<Integer,String> e : m.entrySet()){
            int wrdIndex = e.getKey();
            String docSubString = GetStringFromDocument(wrdIndex, wrdIndex + e.getValue().length());
            claim(e.getValue().equals(docSubString));
        }

        //inter-compatability of  MapFromRegex & StringsCount
        TreeMap<String, Integer> n = StringsCount(GetDocument(), m ,false, true);
        claim(n.size() == WordCount());
        for(Map.Entry<String, Integer> en : n.entrySet()){
         //  System.out.println(en.getKey() + " " + en.getValue());
            claim(en.getValue() == 1);
        }

        //////////////////////////  UniqueStrings  /////////////////////////////
        String[] strArr = {"a", "b", "aa", "bb", "Aa", "bB", "a", "b" };
        List<String> strList = new ArrayList<String>(Arrays.asList(strArr));
        claim(UniqueStrings(strArr, true).size() == 6);
        claim(UniqueStrings(strArr, false).size() == 4);
        claim(UniqueStrings(strList, true).size() == 6);
        claim(UniqueStrings(strList, false).size() == 4);

        //most common letter
        claim("a".equals(MostCommonString(UniqueStrings(MapFromRegex(document,"."), true),false,false)));
        //most common 2 letter sequence
        claim("aa".equals(MostCommonString(UniqueStrings(MapFromRegex(document,".."), true),false,false)));
        //most common 2 letter word (first one found when there is more than 1 with the same count)
        claim("bb".equals(MostCommonString(UniqueStrings(MapFromRegex(document,"\\w{2}"), true),false,true)));
        // as above, but get map of all 2 letter words, to confirm that "bb" is in there, as well as "it"
        TreeMap<String, Integer> tm = StringsCount(document,UniqueStrings(MapFromRegex(document,"\\w{2}"), true),true,true);
        tm.containsKey("bb");
        tm.containsKey("it");
        claim( tm.get("bb") == 1);
        claim( tm.get("it") == 1);
        // total count of 2 letter words
        claim( tm.values().stream().mapToInt(Integer::intValue).sum() == 2);

        Reset();
        claim(document.equals(""));
        claim(charCount == 0);
        claim(wordCount == 0);
        claim(lineCount == 0);
        claim(lineIndex == null);
        claim(wordIndex == null);
        claim(!initialised);
        System.out.println(" Successful");
    }
}
