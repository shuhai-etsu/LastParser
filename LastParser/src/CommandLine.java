/**
 * Created by Shuhai Li on 4/22/2017.
 */
import java.util.*;

public class CommandLine {
    String inputFile;
    String errorFile;
    String suspenseFile;
    String markupFile;

    //set default file names
    CommandLine(){
        inputFile = "stdin";
        errorFile = "stderr";
        suspenseFile = "stderr";
        markupFile = "stdout";
    }
    //This constructor inspects command arguments and set file names
    CommandLine(String[] args){
        ArrayList<String> args2 = new ArrayList<String> (Arrays.asList(args));

        //get file name for input data
        if (args2.contains("-i")){
            int i = args2.indexOf("-i");
            if (args.length >(i+1) && (!args2.get(i+1).equals("-e") && !args2.get(i+1).equals("-m") && !args2.get(i+1).equals("-s") ) )
                inputFile = args2.get(i+1);
            else {
                System.err.println("Input file not specified after -i flag!");
                System.exit(2);
            }
        }else{
            inputFile = "stdin";
        }

        //get file name for parsed records
        if (args2.contains("-m")){
            int i = args2.indexOf("-m");
            if (args.length >(i+1) && (!args2.get(i+1).equals("-e") && !args2.get(i+1).equals("-i") && !args2.get(i+1).equals("-s") ) )
                markupFile = args2.get(i+1);
            else {
                System.err.println("Markup file not specified after -m flag!");
                System.exit(2);
            }
        }else{
            markupFile = "stdout";
        }

        // /get file name for unparsable records
        if (args2.contains("-s")){
            int i = args2.indexOf("-s");
            if (args.length >(i+1) && (!args2.get(i+1).equals("-e") && !args2.get(i+1).equals("-i") && !args2.get(i+1).equals("-m") ) )
                suspenseFile = args2.get(i+1);
            else {
                System.err.println("Suspense file not specified after -s flag!");
                System.exit(2);
            }
        }else{
            suspenseFile = "stderr";
        }

        //get file name for error messages
        if (args2.contains("-e")){
            int i = args2.indexOf("-e");
            if (args.length >(i+1) && (!args2.get(i+1).equals("-i") && !args2.get(i+1).equals("-m") && !args2.get(i+1).equals("-s") ) )
                errorFile = args2.get(i+1);
            else {
                System.err.println("Error file not specified after -e flag!");
                System.exit(2);
            }
        }else{
            errorFile = "stderr";
        }
    }

    public String getInputFile(){
        return inputFile;
    }

    public String getErrorFile(){
        return errorFile;
    }

    public String getSuspenseFile(){
        return suspenseFile;
    }

    public String getMarkupFile(){
        return markupFile;
    }
}
