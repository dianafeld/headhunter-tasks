import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class FileChannelReader implements AutoCloseable {

    private final static int BYTE_BUFFER_CAPACITY = 128;
    private final static int LINE_BUFFER_CAPACITY = 1024; // max line length
    
    private final FileChannel channel;
    
    private ByteBuffer byteBuffer;
    private ByteBuffer lineBuffer;
    
    public FileChannelReader(FileChannel channel) {
        this.channel = channel;
        byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);
        byteBuffer.flip();
        lineBuffer = ByteBuffer.allocate(LINE_BUFFER_CAPACITY);
    }
    
    private String getLineFromBuffer(ByteBuffer lineBuffer) {

        while (byteBuffer.hasRemaining()) {

            byte b;
            if ((b = byteBuffer.get()) == (byte) '\n') {
                byteBuffer.compact();
                byteBuffer.flip();

                return new String(lineBuffer.array(), 0, lineBuffer.position());
            }
            lineBuffer.put(b);
        }
        byteBuffer.clear();
        
        return null;
    }

    public String readLine() throws IOException {

        lineBuffer.clear();

        String line = getLineFromBuffer(lineBuffer);
        if (line != null) {
            return line;
        }
        
        while (channel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            line = getLineFromBuffer(lineBuffer);
            if (line != null) {
                return line;
            }
        }

        if (lineBuffer.position() != 0) {
            byteBuffer.limit(0);
            return new String(lineBuffer.array(), 0, lineBuffer.position());
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
