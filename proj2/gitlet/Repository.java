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
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        if (BLOB_DIR.exists()) {
            BLOB_DIR.mkdir();
        }
        if (COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
        // TODO
    }

    public static void add(String file_name){
        File add_file = join("./" + file_name);
        if (!add_file.exists()){
            System.out.println("File does not exist");
            return;
        }
        // TODO
    }
}
