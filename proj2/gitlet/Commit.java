package gitlet;

// TODO: any imports you need here
import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.*;
import java.time.Instant;
import java.io.File;
import java.io.Serializable;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    // 由于提交一旦建立就无后续更改，所以构造之初就可序列化
    public Commit(String message, String author, long time, String last, Map<String, String> stackedBlob) {
        // 消息
        this.message = message;
        // 时间戳
        this.timestemp = time;
        // last提交
        this.last = last;
        // Blob追踪映射
        this.stackedBlob = new HashMap<>();
        this.stackedBlob.putAll(stackedBlob);
        // 映射
        this.author = author;
        // hash计算
        this.sha1 = Utils.sha1(message, author, timestemp, last, new TreeMap<> (stackedBlob));

    }
    /** The message of this Commit. */

    public String getSha1() {
        return sha1;
    }

    public String getLast() {
        return last;
    }

    public Map<String, String> getStackedBlob() {
        return stackedBlob;
    }

    public void save() {
        File commitFile = Utils.join(Repository.COMMIT_DIR, getSha1());
        Utils.writeObject(commitFile, this);
    }

    public boolean containStacked(String fileKey) {
        return stackedBlob.containsKey(fileKey);
    }

    public static Commit readFromFile(String sha1OfCommit) {
        File CommitFile = Utils.join(Repository.COMMIT_DIR, sha1OfCommit);
        return Utils.readObject(CommitFile, Commit.class);
    }

    private String getDate() {
        Date date = new Date(timestemp);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return formatter.format(date);
    }

    public boolean isEqualsMessage(String findMessage) {
        return findMessage.equals(this.message);
    }

    @Override
    public String toString() {
        String message = "===\n";
        message += "commit " + this.getSha1() + "\n";
        message += "Date: " + this.getDate() + "\n";
        message += this.message + "\n";
        return message;
    }

    private String message;
    private long timestemp;
    private Map<String, String> stackedBlob;
    private String last;
    private String author;
    private String sha1;
    /* TODO: fill in the rest of this class. */
}
