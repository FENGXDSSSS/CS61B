package gitlet;

import java.io.File;
import java.util.*;

public class BufferRm implements Buffer, Iterable<String>{
    // 所有数据结构统一在构造函数里存一次盘, 只有在init命令里才执行
    public BufferRm() {
        buffer = new HashSet<>();
        save();
    }

    public void add(String filename) {
        buffer.add(filename);
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

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public boolean contain(String fileKey) {
        return buffer.contains(fileKey);
    }

    @Override
    public void showContain() {
        if (this.isEmpty()) {
            return;
        } else {
            System.out.print(this.toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder("=== Staged Files ===\n");
        List<String> keySet = new ArrayList<>(buffer);
        // 字母排序
        Collections.sort(keySet);
        for (String fileName : keySet) {
            message.append(fileName).append("\n");
        }

        return message.toString();
    }

    public static BufferRm readFromFile() {
        File BufferAddFile = Utils.join(Repository.BUFFER_DIR, "bufferRm");
        return Utils.readObject(BufferAddFile, BufferRm.class);
    }

    @Override
    public Iterator<String> iterator() {
        return buffer.iterator();
    }

    private Set<String> buffer;
}
