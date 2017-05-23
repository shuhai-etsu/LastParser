import java.time.format.DateTimeFormatter;

/**
 * Created by Shuhai Li on 3/9/2017.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Duration;
import java.time.LocalDateTime;
public abstract class SessionRecord {
    String userName;
    String terminal;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    Duration duration;
    String host;
    SessionRecord nextChain;
    static final String dataTimePatternString = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s{1,2}\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}\\s+\\d{4}";
    static final String durationPatternString = "\\((\\d{1,3})?\\+?(\\d{2}):(\\d{2})\\)";
    static final String terminalPatternString = "(pts/|tty)\\d{1,3}";
    SessionRecord(){
        userName = null;
        terminal = null;
        startDateTime = null;
        endDateTime = null;
        duration = null;
        host = null;
    }

    SessionRecord(String userName,String terminal, LocalDateTime startDateTime,LocalDateTime endDateTime, Duration duration, String host){
        this.userName = userName;
        this.terminal = terminal;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.host = host;
    }

    public void setNextChain(SessionRecord sr){
        this.nextChain = sr;
    } //for chain of responsibility pattern
    public abstract Object parseRecord(String input);  //parse record string to Session Record object
    public abstract String toXML();       //represent Session record in XML format

    //this function parses the datetime string into a LocalDateTime object
    public LocalDateTime parseLocalDateTime(String inputString){
        LocalDateTime dateTime = null;

        //handle cases like Thu Oct 9 09:43:23 2016 where dayOfMonth has one digit
        Pattern patternDateTime1 = Pattern.compile("(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s{1,2}\\d\\s\\d{2}:\\d{2}:\\d{2}\\s\\d{4}"); //date pattern
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEE MMM  d HH:mm:ss yyyy");
        Matcher matcherDateTime1 = patternDateTime1.matcher(inputString);

        //handle cases like Thu Oct 19 09:43:23 2016 where dayOfMonth has two digit
        Pattern patternDateTime2 = Pattern.compile("(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\s(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s{1,2}\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s\\d{4}"); //date pattern
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy");
        Matcher matcherDateTime2 = patternDateTime2.matcher(inputString);

        if (matcherDateTime1.find()){
            try {
                dateTime = LocalDateTime.parse(matcherDateTime1.group(),formatter1);
            } catch (Exception e) {
                return null;
            }
        }else if(matcherDateTime2.find()){
            try {
                dateTime = LocalDateTime.parse(matcherDateTime2.group(),formatter2);
            } catch (Exception e) {
                return null;
            }
        }
        return dateTime;
    }

    //this function parse duration field in session record
    //It returns a Duration object from Java library
    public Duration parseDuration(String inputString){
        Duration duration = null;
        int day;
        int hr;
        int min;
        Pattern patternDuration = Pattern.compile(durationPatternString);
        Matcher matcherDuration = patternDuration.matcher(inputString);
        String dayMatch;
        while (matcherDuration.find()){
            dayMatch = matcherDuration.group(1);
            if (dayMatch == null|| dayMatch.isEmpty())
                day =0;
            else
                day = Integer.parseInt(dayMatch);

            hr = Integer.parseInt(matcherDuration.group(2));
            min = Integer.parseInt(matcherDuration.group(3));
            duration = Duration.ofSeconds(day*24*60*60 + hr*60*60 + min*60);
        }
        return duration;
    }

    //this function represents Duration object to XML format
    public String durationToXML(Duration duration){
        String output;
        long day;
        long hr;
        long min;
        day = duration.toDays();
        hr = duration.minusDays(day).toHours();
        min = duration.minusDays(day).minusHours(hr).toMinutes();
        output = "<duration>"+"<day>" + day +"</day>" +"<hr>" + hr +"</hr>" +"<mn>" + min +"</mn>" +"</duration>";
        return output;
    }

}
