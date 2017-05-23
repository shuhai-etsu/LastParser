import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shuhai Li on 4/23/2017.
 */
public class SessionRecordIncomplete extends SessionRecord{

    public Object parseRecord(String inputString){

        Pattern patternSignature = Pattern.compile("still logged in"); //	system crash characteristics
        Matcher matcherSignature = patternSignature.matcher(inputString);

        Pattern patternOverall = Pattern.compile("\\w+"+"\\s+"+
                terminalPatternString + "\\s+" +
                dataTimePatternString +
                "\\s+"+"still logged in"+"\\s+.+");
        Matcher matcherOverall =patternOverall.matcher(inputString);

        Pattern patternDateTime = Pattern.compile(dataTimePatternString);
        Matcher matcherDateTime = patternDateTime.matcher(inputString);

        if (matcherSignature.find()){
            SessionRecordIncomplete sr = new SessionRecordIncomplete();
            if (matcherOverall.find()){
                String[] strings = inputString.split("\\s+");
                sr.userName = strings[0];
                sr.terminal = strings[1];
                if (matcherDateTime.find()){
                    sr.startDateTime = parseLocalDateTime(matcherDateTime.group());
                    if (sr.startDateTime == null) {
                        return inputString;
                    }
                }
                sr.host = strings[strings.length-1];
                return sr;
            }else{
                return inputString;
            }

        }else{
            return nextChain.parseRecord(inputString);
        }
    }

    public String toXML(){
        String output =null;
        output = "<incomplete>"+"\n" +
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

                    "\t<remote-terminal>" + host + "</remote-terminal>\n" +
                "</incomplete>\n";
        return output;
    }

}