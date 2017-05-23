/**
 * Created by Shuhai Li on 4/23/2017.
 */
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Duration;
import java.time.LocalDateTime;

public class SessionRecordCrash extends  SessionRecord{

    public Object parseRecord(String inputString){

        Pattern patternOverall = Pattern.compile("\\w+"+"\\s+"+
                terminalPatternString + "\\s+" +
                dataTimePatternString + "\\s-\\s" +"down" +
                "\\s+" +
                durationPatternString +
                "(\\s+(:0|:0.0)?)?$");
        Matcher matcherOverall =patternOverall.matcher(inputString);

        Pattern patternDateTime = Pattern.compile(dataTimePatternString); //date pattern
        Matcher matcherDateTime = patternDateTime.matcher(inputString);

        if (matcherOverall.find()){
            SessionRecordCrash sr = new SessionRecordCrash();
            String[] strings = inputString.split("\\s+");
            sr.userName = strings[0];
            sr.terminal = strings[1];
            if(matcherDateTime.find()){
                sr.startDateTime = parseLocalDateTime(matcherDateTime.group());
                if (sr.startDateTime == null){
                    return inputString;
                }
            }
            sr.duration =  parseDuration(inputString);
            return sr;
        }else{
            return nextChain.parseRecord(inputString);
        }
    }

    public String toXML(){
        String output =null;
        output = "<crash>"+"\n" +
                    "\t<user>" + userName + "</user>" +"\n" +
                    "\t<terminal>" +terminal + "</terminal>"+"\n" +

                    "\t<start-session>" +"\n" +
                        "\t\t<date>" +"\n" +
                            "\t\t\t<year>"+startDateTime.getYear()+"</year>" +"<month>"+startDateTime.getMonthValue()+"</month>" +"<day>"+startDateTime.getDayOfMonth()+"</day>" + "<weekday>" + startDateTime.getDayOfWeek().toString().toLowerCase() + "</weekday>" + "\n"+
                        "\t\t</date>" +"\n" +
                        "\t\t<time>" + "\n" +
                            "\t\t\t<hr>"+startDateTime.getHour() +"</hr>" +"<min>"+startDateTime.getMinute() +"</min>" +"<sec>"+startDateTime.getSecond() +"</sec>" + "\n" +
                        "\t\t</time>" +"\n" +
                    "\t</start-session>"+"\n" +

                    "\t" + durationToXML(duration) +"\n" +
                "</crash>\n";
        return output;
    }

}
