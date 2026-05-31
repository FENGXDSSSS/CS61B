package gitlet;

import jdk.jshell.execution.Util;

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

    public static BufferAdd readFromFile() {
        File BufferAddFile = Utils.join(Repository.BUFFER_DIR, "bufferAdd");
        return Utils.readObject(BufferAddFile, BufferAdd.class);
    }
    private Map<File, byte[]> buffer;
}
