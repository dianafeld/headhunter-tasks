import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.channels.FileChannel;

public class Main {

    private static String usage() {
       return "Arguments: log_path";
    }

    private static String toReadableTime(long secondsDelta) {
        return String.format("%h hours, %d min, %d sec",
                secondsDelta / (60 * 60), secondsDelta / 60 % 60, secondsDelta % 60);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Illegal number of arguments");
            System.out.println(usage());
            System.exit(1);
        }

        Path logFile = Paths.get(args[0]);

        if (!Files.exists(logFile)) {
            System.out.println("File not found");
            System.exit(1);
        }

        Map<Integer, Long> totalTimeSpentByUser = null;
        
        try (FileChannelReader logReader = new FileChannelReader(FileChannel.open(logFile, StandardOpenOption.READ))) {
            LogHandler logHandler = new LogHandler(logReader);
            totalTimeSpentByUser = logHandler.getUsersTotalTimeSpent();

        } catch (FileNotFoundException e) {
            System.out.println("Error while opening file: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error while opening/processing file: " + e.getMessage());
            System.exit(1);
        }

        List<Map.Entry<Integer, Long>> usersTime =
                new ArrayList<>(totalTimeSpentByUser.entrySet());
        usersTime.sort(new Comparator<Map.Entry<Integer, Long>>() {
            @Override
            public int compare(Map.Entry<Integer, Long> o1, Map.Entry<Integer, Long> o2) {
                return (int) (long) (o2.getValue() - o1.getValue());
            }
        });

        System.out.println("UserID\tTotal time spent");
        for (Map.Entry<Integer, Long> entry : usersTime) {
            System.out.print(entry.getKey());
            System.out.print("\t\t");
            System.out.println(toReadableTime(entry.getValue()));
        }
    }
}
