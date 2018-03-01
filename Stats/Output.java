import java.util.*;
class Output{
    private Stats s = new Stats();

    public static void main(String... args){
        if(args.length < 1){ throw new Error("1 Argument missing: expected file path of text document");}
        Output o = new Output();
        o.run(args[0]);
    }

    private void run(String filePath){
        s.Initialise(filePath);
        System.out.println("Word Count: " + s.WordCount());
        System.out.println("Line Count: " + s.LineCount());
        System.out.printf("Avg letters per word: %.1f\n", s.AvgWordLen());
        System.out.println("Most common letter: " + s.MostCommonString(s.GetCharactersUsed(false),false, false));

        //Some examples of other statistics and tools that are available from the library
        /*
        System.out.println("\n... Extras ...\n");
        System.out.println("Unique words used: " + s.UniqueStrings(s.MapFromRegex(s.GetDocument(),"\\w+"),false).size());
        System.out.println("Contents of the 2nd line: " + s.GetStringFromLine(1,2));

        TreeMap<Integer, String> map = s.MapFromRegex(s.GetDocument(),"\\b[Tt].*?\\b");
        System.out.println("Number of words beginning with 't': " + map.size() );

        List<String> uniqueTwords = s.UniqueStrings(map,false);
        System.out.println("Of those, how many are unique?':" + uniqueTwords.size());

        TreeMap<String, Integer> histogram = s.StringsCount(s.GetDocument(),uniqueTwords,false,true);
        System.out.println("A list of those words and their count in the document:");
        for(Map.Entry<String,Integer> entry : histogram.entrySet()){
            System.out.print("   '" + entry.getKey() + "'");
            System.out.println(" : " + entry.getValue());
        }
        */
    }
}
