import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shuhai Li on 4/23/2017.
 */
public class SessionRecordShutDown extends SessionRecord{

    public Object parseRecord(String inputString){


        Pattern patternSignature = Pattern.compile("shutdown system down"); //	system crash characteristics
        Matcher matcherSignature = patternSignature.matcher(inputString);

        Pattern patternOverall = Pattern.compile("shutdown system down" + "\\s+" +
                dataTimePatternString + "\\s-\\s" +
                dataTimePatternString + "\\s+" +
                durationPatternString +
                "\\s+.+");
        Matcher matcherOverall =patternOverall.matcher(inputString);

        Pattern patternDateTime = Pattern.compile(dataTimePatternString); //date pattern
        Matcher matcherDateTime = patternDateTime.matcher(inputString);

        if (matcherSignature.find()){
            if (matcherOverall.find()){
                SessionRecordShutDown sr = new SessionRecordShutDown();
                String[] strings = inputString.split("\\s+");
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

                sr.host = strings[strings.length-1];
                return sr;
            }else{
                return inputString;
            }
        }else {
            return nextChain.parseRecord(inputString);
        }
    }

    public String toXML(){
        String output =null;
        output = "<shut-down>"+"\n" +

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
                    "\t<remote-terminal>" + host + "</remote-terminal>\n" +

                "</shut-down>\n";
        return output;
    }

}
