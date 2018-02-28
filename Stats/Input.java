import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.text.DecimalFormat;
import java.util.regex;

class Input{
    private Scanner scan;
    private ArrayList<String[]> document;
    private String unbroken = "";

    public static void main(String... args){
        Input i = new Input();
        i.Initialise(args);
        i.Run();
    }

    private void Initialise(String... args){
        if(args == null || args.length < 1){throw new Error("Please specify text file path, e.g : 'java Import test.txt'");}
        document = new ArrayList<String[]>();
        String line;
        String[] words;
        try{
            scan = new Scanner(new File(args[0]));
        }
        catch(Exception e){ e.printStackTrace();}
        // Scan in the entire file, line by line. Splits document up into words as it scans in.
        // Each line is an element in document, each word is an entry in the String[] inside the list
        while(scan.hasNextLine()){
            line = scan.nextLine();
            unbroken = unbroken + line;
            words = line.split("\\s");
            document.add(words);
        }
        scan.close();
    }

    private void Run(){
        System.out.println("Word Count: " + WordCount());
        System.out.println("Line Count: " + LineCount());
        System.out.printf("avg Word len: %.1f%n",WordLen());
        System.out.println("most common letter: " + MostCommonLetter());
    }

    int CharCount(){
        int x = 0;
        for(String[] line : document){
            for(String word : line){
                x += word.length();
            }
        }
        return x;
    }

    int WordCount(){
        int x = 0;
        for(String[] line : document){
            x += line.length;
        }
        return x;
    }

    int LineCount(){
        return document.size();
    }

    float WordLen(){
        int i = 0;
        for(String[] line : document){
            for(String word : line){
                i += word.length();
            }
        }
        return (float) i/WordCount();
    }

    //takes each letter as lower case, ignores anything outside a-z
    char MostCommonLetter(){
        int[] letters = new int[26];
        char tmp;
        int index;
        for(String[] line : document){
            for(String word : line){
                for(int i = 0; i < word.length(); i++){
                    tmp = word.toLowerCase().charAt(i);
                    index = tmp - 'a';
                    if(index > 26){continue;}
                    letters[index]++;
                }
            }
        }
        return (char) ('a' +  GetMaxArray(letters));
    }

    // Returns the index of the highest value in an integer array
    int GetMaxArray(int[] a){
        int max = 0;
        int i;
        if(a == null ){ return -1; }
        for(i = 0; i < a.length; i++){
            if(a[max] < a[i] ){ max = i;}
        }
        return max;
    }
    /*
    int WordsInLineStat(String[] line, String str, String regex){
        int x = 0;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        for(String word : line){

        }
        return x;
    }
    */
}
