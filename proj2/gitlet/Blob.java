package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    // 同Commit一旦建立后续无需更改，构造之初即可序列化
    public Blob(File fileName, byte[] contents) {
        this.fileName = fileName;
        this.contents = contents;
        this.sha1 = Utils.sha1(contents);

        save();
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

    public static Blob readFromFile(String sha1OfBlob) {
        File BlobFile = Utils.join(Repository.BLOB_DIR, sha1OfBlob);
        return Utils.readObject(BlobFile, Blob.class);
    }

    private File fileName;
    private byte[] contents;
    private String sha1;
}
