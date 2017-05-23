import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Shuhai Li on 3/9/2017.
 */
public class SessionRecordCollection {
    ArrayList<SessionRecord> collection;

    SessionRecordCollection(){
        collection = new ArrayList<SessionRecord>();
    }

    SessionRecordCollection(ArrayList<SessionRecord> collection){
        this.collection = collection;
    }
    public ArrayList<SessionRecord> getCollection(){
        return collection;
    }

    public void add(SessionRecord sr){
        collection.add(sr);
    }



}
