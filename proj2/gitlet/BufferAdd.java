package gitlet;

import afu.org.checkerframework.checker.igj.qual.I;
import jdk.jshell.execution.Util;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class BufferAdd implements Buffer, Iterable<String> {
    // 所有数据结构统一在构造函数里存一次盘, 只有在init命令里才执行
    public BufferAdd() {
        buffer = new HashMap<>();
        save();
    }

    public void add(String filename, byte[] contents) {
        buffer.put(filename, contents);
    }

    public void remove(String fileKey) {
        buffer.remove(fileKey);
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

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public byte[] getContents(String key) {
        return buffer.get(key);
    }

    @Override
    public boolean contain(String fileKey) {
        return buffer.containsKey(fileKey);
    }

    public static BufferAdd readFromFile() {
        File BufferAddFile = Utils.join(Repository.BUFFER_DIR, "bufferAdd");
        return Utils.readObject(BufferAddFile, BufferAdd.class);
    }

    @Override
    public Iterator<String> iterator() {
        return buffer.keySet().iterator();
    }

    private Map<String, byte[]> buffer;
}
