import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class LogHandler {

    private BufferedReader logReader;

    public LogHandler(BufferedReader logReader) {
        this.logReader = logReader;
    }

    private void handleEvent(HashMap<Integer, Long> totalTimeSpentByUser, long unixTime, int userID, String event) {
        if (event.equals("login")) {
            totalTimeSpentByUser.compute(userID,
                    (key, value) -> value == null ? -unixTime : value - unixTime);
        } else if (event.equals("logout")) {
            totalTimeSpentByUser.compute(userID,
                    (key, value) -> value == null ? unixTime : value + unixTime);
        }
    }

    public HashMap getUsersTotalTimeSpent() throws IOException {
        HashMap<Integer, Long> totalTimeSpentByUser = new HashMap<Integer, Long>();

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
