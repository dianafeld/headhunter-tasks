import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


// nc localhost 54324
public class NetworkService implements Runnable {
    
    private final static int READ_BUFFER_CAPACITY = 1024;

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    
    private final Sender sender;
    private final Thread senderThread;
    
    private final ByteBuffer readBuffer;
    private final Set<SocketChannel> clientChannels;

    public NetworkService(int port) throws IOException {

        selector = Selector.open();
        
        serverChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        serverChannel.socket().bind(address);
        serverChannel.configureBlocking(false);

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        readBuffer = ByteBuffer.allocate(READ_BUFFER_CAPACITY);

        clientChannels = Collections.newSetFromMap(new ConcurrentHashMap<SocketChannel, Boolean>());

        sender = new Sender(clientChannels);
        
        senderThread = new Thread(sender);
        senderThread.start();
    }
    
    
    private void processKey(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        } else if (key.isAcceptable()) {
            accept(key);
        } else if (key.isReadable()) {
            read(key);
        }
    }
    
    
    private void processSelectedKeys() throws IOException {
        Iterator iter = selector.selectedKeys().iterator();
        while (iter.hasNext()) {
            SelectionKey key = (SelectionKey) iter.next();
            iter.remove();
            processKey(key);
        }
    }
    
    
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        
        System.out.println("Client connected!");

        clientChannel.register(selector, SelectionKey.OP_READ);
        clientChannels.add(clientChannel);
    }
    
    private void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        readBuffer.clear();

        int readSize;
        try {
            readSize = clientChannel.read(readBuffer);
        } catch (IOException e) {
            System.out.println("The connection was broken on the other side: ");
            System.out.println(e.getMessage());
            key.cancel();
            clientChannel.close();
            return;
        }

        if (readSize == -1) {
            System.out.println("Client disconnected");
            key.channel().close();
            key.cancel();
            return;
        } else {
            readBuffer.flip();
            sender.sendAll(ByteBufferUtils.clone(readBuffer), clientChannel);
        }

    }
    
    @Override
    public void run() {
        try {
            
            while (true) {
                selector.select();
                processSelectedKeys();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Shutting down...");
            try {
                serverChannel.close();
                selector.close();
                senderThread.interrupt();
            } catch (IOException e) {}
        }
    }
}

