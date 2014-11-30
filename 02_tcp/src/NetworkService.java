import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// Example from http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
// nc localhost 54324
public class NetworkService implements Runnable {

    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    private final Socket[] clientSockets;

    public NetworkService(int port, int poolSize)
            throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
        clientSockets = new Socket[poolSize];
    }

    private int getFirstAvailable() {
        for (int i = 0; i < clientSockets.length; ++i) {
            if (clientSockets[i] == null || clientSockets[i].isClosed()) {
                return i;
            }
        }
        throw new RuntimeException("No available sockets (couldn't be thrown)");
    }

    private void sendAll(String text, Socket sender) throws IOException {

        BufferedWriter bw;
        for (int i = 0; i < clientSockets.length; ++i) {
            if (clientSockets[i] != null &&
                    clientSockets[i].isConnected() && !clientSockets[i].equals(sender)) {

                bw = new BufferedWriter(
                        new OutputStreamWriter(clientSockets[i].getOutputStream()));

                bw.write(text + '\n');
                bw.flush();

            }
        }
    }

    @Override
    public void run() { // run the service
        try {
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                clientSockets[getFirstAvailable()] = clientSocket;
                pool.execute(new Handler(clientSocket));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Shutting down...");
            pool.shutdown();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {}
        }
    }

    private class Handler implements Runnable {
        private final Socket clientSocket;

        Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Connected");

            try (final BufferedReader br = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()))) {

                String line;
                while (clientSocket.isConnected() &&  (line = br.readLine()) != null) {
                    sendAll(line, clientSocket);
                }

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }

}

