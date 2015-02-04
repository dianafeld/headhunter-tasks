import java.io.IOException;
import java.util.*;

public class LogHandler {

    private final FileChannelReader logReader;

    public LogHandler(FileChannelReader logReader) {
        this.logReader = logReader;
    }

    private void handleEvent(Map<Integer, Long> totalTimeSpentByUser, long unixTime, int userID, String event) {
        if (event.equals("login")) {
            totalTimeSpentByUser.compute(userID,
                    (key, value) -> value == null ? -unixTime : value - unixTime);
        } else if (event.equals("logout")) {
            totalTimeSpentByUser.compute(userID,
                    (key, value) -> value == null ? unixTime : value + unixTime);
        }
    }
    
    
    public Map getUsersTotalTimeSpent() throws IOException {
        Map<Integer, Long> totalTimeSpentByUser = new HashMap<>();

        String line;
        String[] tokens;
        long unixTime;
        int userID;
        String event;
        while ((line = logReader.readLine()) != null) {

            tokens = line.split(", ");
            unixTime = Long.valueOf(tokens[0]);
            userID = Integer.valueOf(tokens[1]);
            event = tokens[2];

            handleEvent(totalTimeSpentByUser, unixTime, userID, event);
        }

        return totalTimeSpentByUser;
    }    
}
