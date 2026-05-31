package gitlet;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class BufferAdd implements Buffer{
    public BufferAdd() {
        buffer = new HashMap<>();
    }
    @Override
    public void add(File filename, byte[] contents) {
        buffer.put(filename, contents);
    }
    @Override
    public void clear() {
        buffer.clear();
    }
    private Map<File, byte[]> buffer;
}
