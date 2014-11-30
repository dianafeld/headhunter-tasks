import java.io.IOException;

public class Main {

    private static int DEFAULT_PORT = 54324;
    private static int MAX_CONNECTIONS = 10;

    private static String usage() {
        return "Arguments: [port]";
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length > 1) {
            System.out.println(usage());
            System.exit(1);
        } else if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }


        NetworkService server = null;
        try {
            server = new NetworkService(port, MAX_CONNECTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        server.run();

    }
}
