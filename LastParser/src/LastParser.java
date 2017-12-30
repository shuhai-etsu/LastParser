/**
 * Created by Shuhai Li on 5/1/2017.
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
/*meaningless comments*/
public class LastParser {

    public static void main(String[] args){

        //process command arguments using command line class
        CommandLine cl = new CommandLine(args);
        String inputFileName = cl.getInputFile();
        String errorFileName = cl.getErrorFile();
        String suspenseFileName = cl.getSuspenseFile();
        String markupFilename = cl.getMarkupFile();

        //Read log file from last unix command
        MyFileWriter fw = new MyFileWriter(errorFileName);
        MyFileReader fr = new MyFileReader(inputFileName, fw);
        try {
            fr.readFile();
        } catch (Exception e) {
            fw.writeErrorMessage("something wrong while reading input file");
        }
        ArrayList<String> stringArrayList = fr.getStringArrayList();
        LocalDateTime inputStartDateTime = fr.getTrailerDateTime();
        if (inputStartDateTime == null) {
            fw.writeErrorMessage("the datetime in the trailer is not parsable");
            System.exit(2);
        }
        LocalDateTime inputEndDateTime=fr.getCreationDateTime();

        //Use Chain of Responsibility pattern to parse the session records
        SessionRecordShutDown sr1 = new SessionRecordShutDown();
        SessionRecordReboot sr2 = new SessionRecordReboot();
        SessionRecordRunlevelChange sr3 = new SessionRecordRunlevelChange();
        SessionRecordIncomplete sr4 = new SessionRecordIncomplete();
        SessionRecordCrash sr5 = new SessionRecordCrash();
        SessionRecordCrashKnownTerminal sr6 = new SessionRecordCrashKnownTerminal();
        SessionRecordComplete sr7 = new SessionRecordComplete();
        SessionRecordCompleteUnknownTerminal sr8 = new SessionRecordCompleteUnknownTerminal();
        sr1.setNextChain(sr2);
        sr2.setNextChain(sr3);
        sr3.setNextChain(sr4);
        sr4.setNextChain(sr5);
        sr5.setNextChain(sr6);
        sr6.setNextChain(sr7);
        sr7.setNextChain(sr8);

        Object obj;    //returned object from parsing
        SessionRecord sr;
        SessionRecordCollection sessionRecordCollection = new SessionRecordCollection(); //session record collection
        ArrayList<String> unparsableRecords = new ArrayList<>();   //list of unparsable records

        int i = 0;
        while (!stringArrayList.isEmpty() && i<2){
            stringArrayList.remove(stringArrayList.size()-1);//remove the last two rows from input( empty row and wtmp trailer row)
            i++;
        }

        if (!stringArrayList.isEmpty()){
            for (String str: stringArrayList){
                obj = sr1.parseRecord(str);    //pass the input string to the first SessionRecord object for parsing
                //System.out.println((obj instanceof String));
                if (obj instanceof String){
                    unparsableRecords.add(str);
                }else{
                    //System.out.println(str);
                    sr = (SessionRecord) obj;
                    sessionRecordCollection.add(sr);
                }
            }
        }

        //write parsed Session records in XML format to markup file
        if (!sessionRecordCollection.getCollection().isEmpty()){
            MyFileWriter fw2 = new MyFileWriter(markupFilename);
            fw2.writeSessionRecord(sessionRecordCollection,inputStartDateTime,inputEndDateTime);
        }

        //write unparsable records to suspense file
        if (!unparsableRecords.isEmpty()){
            fw.writeErrorMessage("Some records are unparsable. See " + suspenseFileName + " for the list\n");
            MyFileWriter fw3 = new MyFileWriter(suspenseFileName);
            fw3.writeUnparsableRecord(unparsableRecords);
            System.exit(1);
        }
        System.exit(0);
    }
}