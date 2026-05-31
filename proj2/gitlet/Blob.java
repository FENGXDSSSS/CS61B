package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    public Blob(File fileName, byte[] contents) {
        this.fileName = fileName;
        this.contents = contents;
        this.sha1 = Utils.sha1(contents);
    }

    public String getSha1() {
        return sha1;
    }

    public File getName() {
        return fileName;
    }

    public void save() {
        File blobFile = Utils.join(Repository.BLOB_DIR, getSha1());
        Utils.writeObject(blobFile, this);
    }
    private File fileName;
    private byte[] contents;
    private String sha1;
}
