package gitlet;


import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.Serializable;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    // 由于提交一旦建立就无后续更改，所以构造之初就可序列化
    // stackedBlob1 在init命令中为null
    public Commit(String message, String author,
                  long time, String last, Map<String, String> stackedBlob1) {
        // 消息
        this.messages = message;
        // 时间戳
        this.timestemp = time;

        // last提交
        this.last = last;

        // Blob追踪映射

        if (stackedBlob1 != null) {
            this.stackedBlob = new TreeMap<>();
            this.stackedBlob.putAll(stackedBlob1);
        }
        // 映射
        this.author = author;
        // hash计算
        byte[] stackStr = Utils.serialize(stackedBlob);
        this.sha1 = Utils.sha1(message, author, String.valueOf(timestemp), last, stackStr);

    }
    /** The message of this Commit. */

    // 获取sha1值
    public String getSha1() {
        return sha1;
    }

    // 获取上一次提交
    public String getLast() {
        return last;
    }

    // 获取追踪目录
    public Map<String, String> getStackedBlob() {
        if (stackedBlob == null) {
            return new HashMap<String, String>();
        } else {
            return stackedBlob;
        }
    }

    public void save() {
        File commitFile = Utils.join(Repository.COMMIT_DIR, getSha1());
        Utils.writeObject(commitFile, this);
    }

    // 是否包含指定文件key
    public boolean containStacked(String fileKey) {
        if (stackedBlob == null) {
            return false;
        } else {
            return stackedBlob.containsKey(fileKey);
        }
    }

    // 读文件
    public static Commit readFromFile(String sha1OfCommit) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, sha1OfCommit);
        if (!commitFile.exists()) {
            return null;
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    // 获取当前时间戳
    private String getDate() {
        Date date = new Date(timestemp);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return formatter.format(date);
    }

    // 消息message是否与指定消息相同
    public boolean isEqualsMessage(String findMessage) {
        return findMessage.equals(this.messages);
    }

    // 文件是否被跟踪
    public boolean fileIsStacked(String fileName) {
        return stackedBlob.containsKey(fileName);
    }

    // 获得跟指定的追踪文件内容
    public byte[] getStackedFileContents(String fileName) {
        String blobID = stackedBlob.get(fileName);
        Blob thisBlob = Blob.readFromFile(blobID);
        return thisBlob.getContents();
    }

    private String getStackedFileSha1(String fileName) {
        return stackedBlob.get(fileName);
    }

    public boolean isWithStackedSame(File file) {
        byte[] contents = Utils.readContents(file);
        String sha1ByAddFile = Utils.sha1(contents);
        String sha1ByFile = getStackedFileSha1(file.getName());
        return sha1ByFile.equals(sha1ByAddFile);
    }

    // 追踪列表写入文件
    public void writeFilesFromStacked() {
        for (String fileName : stackedBlob.keySet()) {
            File file = Utils.join(Repository.CWD, fileName);
            byte[] contents = getStackedFileContents(fileName);
            Utils.writeContents(file, (Object) contents);
        }
    }

    @Override
    public String toString() {
        String message = "===\n";
        message += "commit " + this.getSha1() + "\n";
        message += "Date: " + this.getDate() + "\n";
        message += this.messages + "\n";
        return message;
    }

    private String messages;
    private long timestemp;
    // 文件名/has
    private TreeMap<String, String> stackedBlob;
    private String last;
    private String author;
    private String sha1;
}
