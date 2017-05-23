/**
 * Created by Shuhai Li on 4/23/2017.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class MyFileWriter {

    String outputFileName;            //output file name

    MyFileWriter(String outputFileName){
        this.outputFileName = outputFileName;
    }

    //write session records in XML format to console or output file
    public void writeSessionRecord(SessionRecordCollection sessionRecordCollection, LocalDateTime startDateTime, LocalDateTime endDateTime){
        FileWriter fw = null;
        BufferedWriter output = null;

        String dateTimeString;
        dateTimeString = "<last output>\n" +
                "\t<start-log>" +"\n" +
                    "\t\t<date>" +"\n" +
                        "\t\t\t<year>"+startDateTime.getYear()+"</year>" +"<month>"+startDateTime.getMonthValue()+"</month>" +"<day>"+startDateTime.getDayOfMonth()+"</day>" + "<weekday>" + startDateTime.getDayOfWeek().toString().toLowerCase() + "</weekday>" + "\n"+
                    "\t\t</date>" +"\n" +
                    "\t\t<time>" + "\n" +
                        "\t\t\t<hr>"+startDateTime.getHour() +"</hr>" +"<min>"+startDateTime.getMinute() +"</min>" +"<sec>"+startDateTime.getSecond() +"</sec>" + "\n" +
                    "\t\t</time>" +"\n" +
                "\t</start-log>"+"\n" +

                "\t<end-log>\n" +
                    "\t\t<date>" +"\n" +
                        "\t\t\t<year>"+endDateTime.getYear()+"</year>" +"<month>"+endDateTime.getMonthValue()+"</month>" +"<day>"+endDateTime.getDayOfMonth()+"</day>" + "<weekday>" +endDateTime.getDayOfWeek().toString().toLowerCase() + "</weekday>" + "\n"+
                    "\t\t</date>" +"\n" +
                    "\t\t<time>" + "\n" +
                        "\t\t\t<hr>"+endDateTime.getHour() +"</hr>" +"<min>"+endDateTime.getMinute() +"</min>" +"<sec>"+endDateTime.getSecond() +"</sec>" + "\n" +
                    "\t\t</time>" +"\n" +
                "\t</end-log>\n";


        if (outputFileName.equals("stdout")){
            System.out.println(dateTimeString);
            System.out.println("<sessions>\n +" +
                    "\t<session>\n");
            for(SessionRecord sr:sessionRecordCollection.getCollection())
            {
                System.out.print(sr.toXML());
            }
            System.out.println("\t</session>\n" +
                    "<sessions>\n" +
                    "</last-output");
        }else {
            try
            {
                fw = new FileWriter(outputFileName);   //initiate FileWriter object with specified output file
                output = new BufferedWriter(fw);      //initiate BufferWriter object

                output.write(dateTimeString);
                output.write("<sessions>\n" +
                        "\t<session>\n");

                for(SessionRecord sr:sessionRecordCollection.getCollection())
                {
                    output.write(sr.toXML());
                }
                output.write("\t</session>\n" +
                        "<sessions>\n" +
                        "</last-output>");

                output.close();                                //close the file
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.err.println("Cannot write to the file");
            }
        }
    }

    //write unparsable records to console or suspense file
    public void writeUnparsableRecord(ArrayList<String> arrayList){
        FileWriter fw = null;
        BufferedWriter output = null;
        if (outputFileName.equals("stderr")){
            for (String string:arrayList){
                System.err.println(string);
            }
        }else{
            try
            {
                fw = new FileWriter(outputFileName);   //initiate FileWriter object with specified output file
                output = new BufferedWriter(fw);      //initiate BufferWriter object

                for(String string:arrayList)
                {
                    output.write(string+"\n");
                }
                output.close();                                //close the file
                System.err.println("Finished writing unparsable records to " + outputFileName);
            }
            catch(Exception e)
            {
                System.err.println("Can not write to " + outputFileName);
            }
        }
    }

    //write error messages to console or error file
    public void writeErrorMessage(String message){
        FileWriter fw = null;
        BufferedWriter output = null;
        if (outputFileName.equals("stderr")){
            System.err.print(message);
        }else {
            try
            {
                fw = new FileWriter(outputFileName);   //initiate FileWriter object with specified output file
                output = new BufferedWriter(fw);      //initiate BufferWriter object
                //System.out.println("Writing error to an error file");
                output.write(message);
                output.close();                                //close the file
                //System.out.println("Finished writing error message to"+outputFileName);
            }
            catch(Exception e)
            {
                System.err.println("Cannot write to the file");
            }
        }
    }
}
