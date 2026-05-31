package gitlet;

import jdk.jshell.execution.Util;

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

    @Override
    public void save() {
        File bufferRmFile = Utils.join(Repository.BUFFER_DIR, "bufferRm");
        Utils.writeObject(bufferRmFile, this);
    }

    public static BufferRm readFromFile() {
        File BufferAddFile = Utils.join(Repository.BUFFER_DIR, "bufferRm");
        return Utils.readObject(BufferAddFile, BufferRm.class);
    }

    private Map<File, byte[]> buffer;
}
