package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class UnStacked implements Serializable {

    // 构造函数
    public UnStacked() {
        unStackedFiles = new TreeSet<>();
    }

    // 添加
    public void addUnStacked(String fileName) {
        File fileDir = Utils.join(Repository.CWD, fileName);
        if (!fileDir.exists()) {
            return;
        }
        unStackedFiles.add(fileName);
    }

    // 删除
    public void rmUnstacked(String fileName) {
        unStackedFiles.remove(fileName);
    }

    // 从暂存区中获取文件到为追踪文件集合
    public void getFileFromBuffer(Buffer buffer) {
        if (buffer.isEmpty()) {
            return;
        }
        unStackedFiles.addAll(buffer.getSet());
    }

    // 依据暂存区数据删除指定被追踪文件
    public void rmUnStackedOfBufferAdd(Buffer buffer) {
        if (buffer.isEmpty()) {
            return;
        }

    }

    // 存盘
    public void save() {
        File unStackedFile = Utils.join(Repository.UNSTACKED_DIR, "unstacked");
        Utils.writeObject(unStackedFile, this);
    }

    // 获取字符串信息
    @Override
    public String toString() {
        StringBuilder unStackedStr = new StringBuilder("=== Untracked Files ===\n");
        for (String fileName : unStackedFiles) {
            unStackedStr.append(fileName + "\n");
        }
        unStackedStr.append("\n");
        return unStackedStr.toString();
    }

    // 打印集合状态
    public void showContain() {
        System.out.println(toString());
    }

    // 读取
    public static UnStacked readFromFile() {
        File unStackedFile = Utils.join(Repository.UNSTACKED_DIR, "unstacked");
        return Utils.readObject(unStackedFile, UnStacked.class);
    }

    // 数据结构实体
    private Set<String> unStackedFiles;
}
