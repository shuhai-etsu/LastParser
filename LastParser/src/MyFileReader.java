import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyFileReader {

    String inputFileName;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    LocalDateTime inputStartDateTime = null;
    LocalDateTime inputEndDateTime = null;
    MyFileWriter fw;
    SessionRecordComplete sr;

    //constructor
    MyFileReader(String inputFileName,MyFileWriter fw){
        this.inputFileName = inputFileName;
        this.fw = fw;
        sr = new SessionRecordComplete();
    }

    //get the string array read from the input file
    public ArrayList<String> getStringArrayList(){
        return stringArrayList;
    }

    //Read content from the input file
    public void readFile() throws Exception {
        Scanner input = null;
        String newLine = null;

        if (inputFileName.equals("stdin")){
            input = new Scanner(System.in);
            System.out.println("terminate your input with # at new line!");
            while (input.hasNextLine()){
                newLine = input.nextLine();
                if(newLine.equals("#")) break;
                stringArrayList.add(newLine);
            }
            input.close();
            isFileEmpty();
        }else{
            try
            {
                input = new Scanner(new File(inputFileName));
                while (input.hasNextLine())
                {
                    newLine = input.nextLine();
                    stringArrayList.add(newLine);
                }
                input.close();                  //close input file
                isFileEmpty();                  //check if the input file is empty
            }
            catch (FileNotFoundException e)
            {
                fw.writeErrorMessage("Input file not found!\n");
                System.exit(2);
            }
        }

    }

    //check to see if the file is empty
    private void isFileEmpty(){
        if (stringArrayList.isEmpty()){
            fw.writeErrorMessage("The input file is empty!\n");
            System.exit(2);
        } else{
            validateWTMPTrailer();
        }
    }

    //check if the input file contains wtmp trailer
    private void validateWTMPTrailer(){
        int size =stringArrayList.size();
        if (!stringArrayList.get(size-1).contains("wtmp begins")){
            fw.writeErrorMessage("Input file not having contain wtmp trailer\n");
            System.exit(2);
        }
    }

    //get the creation DateTime of input file
    public LocalDateTime getCreationDateTime(){
        if (inputFileName.equals("stdin")){
            inputEndDateTime = LocalDateTime.now();
        }else{
            Path path = Paths.get(inputFileName);
            BasicFileAttributes attr;
            FileTime fileTime =null;
            try {
                attr = Files.readAttributes(path, BasicFileAttributes.class);
                fileTime = attr.creationTime();
            } catch (IOException e) {
                System.out.println("can not get creation time");
            }
            long i= fileTime.to(TimeUnit.SECONDS);
            inputEndDateTime = LocalDateTime.ofEpochSecond(i,0, ZoneOffset.ofHours(0));
        }
        return inputEndDateTime;
    }

    //get the trailer DateTime at the end of the input file
    public LocalDateTime getTrailerDateTime(){
        if (inputFileName.equals("stdin")){
            inputStartDateTime = LocalDateTime.now();
        } else{

            int size =stringArrayList.size();
            Pattern pattern = Pattern.compile("(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s{1,2}\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}\\s\\d{4}");
            String str = stringArrayList.get(size-1);
            Matcher matcher = pattern.matcher(str);

            if (matcher.find()){
                try {
                    inputStartDateTime  = sr.parseLocalDateTime(matcher.group());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return inputStartDateTime;
    }
}
