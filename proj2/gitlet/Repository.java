package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // 对象目录
    public static final File OBJECT_DIR = join(GITLET_DIR, "object");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    // 缓存目录
    public static final File BUFFER_DIR = join(GITLET_DIR, "buffer");
    // 分支目录
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (!GITLET_DIR.mkdir()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        if (!OBJECT_DIR.mkdir()) {
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
        }
        BUFFER_DIR.mkdir();
        BRANCH_DIR.mkdir();
    }
}
