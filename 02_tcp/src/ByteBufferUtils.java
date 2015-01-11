import java.nio.ByteBuffer;

public class ByteBufferUtils {

    public static ByteBuffer clone(ByteBuffer original) { // http://stackoverflow.com/a/4074089
        int start = original.position();
        
        ByteBuffer clone = ByteBuffer.allocate(original.capacity());
        original.rewind();
        clone.put(original);
        original.rewind();
        clone.flip();
        clone.limit(original.limit());
        
        original.position(start);
        return clone;
    }
    
}
