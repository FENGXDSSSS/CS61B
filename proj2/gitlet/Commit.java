package gitlet;

// TODO: any imports you need here
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

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
    public Commit(String message, String author, String last, Map<File, String> stackedBlob) {
        // 消息
        this.message = message;
        // 时间戳
        Instant Now = Instant.now();
        this.timestemp = Now.getEpochSecond();
        // last提交
        this.last = last;
        // Blob追踪映射
        this.stackedBlob = stackedBlob;
        // 映射
        this.author = author;
        // hash计算
        this.sha1 = Utils.sha1(this);
    }
    /** The message of this Commit. */

    public String getSha1() {
        return sha1;
    }

    public Map<File, String> getStackedBlob() {
        return stackedBlob;
    }

    private String message;
    private long timestemp;
    private Map<File, String> stackedBlob;
    private String last;
    private String author;
    private String sha1;
    /* TODO: fill in the rest of this class. */
}
