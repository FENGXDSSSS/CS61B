package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BufferRm implements Buffer{
    public BufferRm() {
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
