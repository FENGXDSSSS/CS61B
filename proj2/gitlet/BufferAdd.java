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

    @Override
    public void save() {
        File bufferAddFIle = Utils.join(Repository.BUFFER_DIR, "bufferAdd");
        Utils.writeObject(bufferAddFIle, this);
    }
    private Map<File, byte[]> buffer;
}
