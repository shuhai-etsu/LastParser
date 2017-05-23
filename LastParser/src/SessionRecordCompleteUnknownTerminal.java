import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shuhai Li on 4/23/2017.
 */
public class SessionRecordCompleteUnknownTerminal extends SessionRecord {

    public Object parseRecord(String inputString){
//hilltw   pts/8        Wed Mar 23 20:21:24 2016 - Wed Mar 23 20:23:40 2016  (00:02)
//kilpatrickc pts/59       Thu Oct 27 09:07:47 2016 - Thu Oct 27 09:08:44 2016  (00:00)

        Pattern patternOverall = Pattern.compile("\\w+" + "\\s+" +
                terminalPatternString + "\\s+" +
                dataTimePatternString + "\\s-\\s" +
                dataTimePatternString + "\\s+" +
                durationPatternString +"\\z");
        Matcher matcherOverall = patternOverall.matcher(inputString);

        Pattern patternDateTime = Pattern.compile(dataTimePatternString); //date pattern
        Matcher matcherDateTime = patternDateTime.matcher(inputString);

        if (matcherOverall.find()){
            SessionRecordCompleteUnknownTerminal sr = new SessionRecordCompleteUnknownTerminal();
            String[] strings = inputString.split("\\s+");
            sr.userName = strings[0];
            sr.terminal = strings[1];
            //System.out.println(userName);
            //System.out.println(terminal);
            int i = 0;
            while(matcherDateTime.find()){
                if (i==0){
                    sr.startDateTime = parseLocalDateTime(matcherDateTime.group());
                    if (sr.startDateTime == null){
                        return inputString;
                    }
                }
                if (i==1){
                    sr.endDateTime = parseLocalDateTime(matcherDateTime.group());
                    if (sr.endDateTime == null){
                        return inputString;
                    }
                }
                i++;
            }
            sr.duration =  parseDuration(inputString);

            if (sr.startDateTime.isAfter(sr.endDateTime)){
                return inputString;
            }
            if (Math.abs(Duration.between(sr.startDateTime,sr.endDateTime).toMinutes()-sr.duration.toMinutes())>1){
                return inputString;
            }

            return sr;
        }else{
            return inputString;
        }
    }

    public String toXML(){
        String output;
        output = "<complete-unknown-terminal>"+"\n" +
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

					"\t<end-session>\n" +
                        "\t\t<date>" +"\n" +
                            "\t\t\t<year>"+endDateTime.getYear()+"</year>" +"<month>"+endDateTime.getMonthValue()+"</month>" +"<day>"+endDateTime.getDayOfMonth()+"</day>" + "<weekday>" +endDateTime.getDayOfWeek().toString().toLowerCase() + "</weekday>" + "\n"+
                        "\t\t</date>" +"\n" +
                        "\t\t<time>" + "\n" +
                            "\t\t\t<hr>"+endDateTime.getHour() +"</hr>" +"<min>"+endDateTime.getMinute() +"</min>" +"<sec>"+endDateTime.getSecond() +"</sec>" + "\n" +
                        "\t\t</time>" +"\n" +
                    "\t</end-session>\n"+

                    "\t" + durationToXML(duration) +"\n" +
                "</complete-unknown-terminal>\n";
        return output;
    }

}
