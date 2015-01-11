import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

class Sender implements Runnable {
    
    private final Set<SocketChannel> clientChannels;
    
    public Sender(Set<SocketChannel> clientChannels) {
        this.clientChannels = clientChannels;
    }
    
    @Override
    public void run() {
        while (true) {
            
        }
    }

    public void sendAll(ByteBuffer message, SocketChannel clientChannel) throws IOException {
        
        for (SocketChannel channel: clientChannels) {
            if (!channel.isConnected()) {
                clientChannels.remove(channel);
            } else if (!channel.equals(clientChannel)) {
                channel.write(message);
                message.flip();
            }
        }
    }
}
