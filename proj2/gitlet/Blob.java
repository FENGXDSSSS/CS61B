//Binary Large Object（中文通常翻译为：二进制大型对象）
package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class Blob implements Serializable {
    // 同Commit一旦建立后续无需更改，构造之初即可序列化
    public Blob(String fileName, byte[] contents) {
        this.fileName = fileName;
        this.contents = Arrays.copyOf(contents, contents.length);
        this.sha1 = Utils.sha1(this.contents);
    }

    public String getSha1() {
        return sha1;
    }

    public String getName() {
        return fileName;
    }

    public byte[] getContents() {
        return contents;
    }

    public void save() {
        File blobFile = Utils.join(Repository.BLOB_DIR, getSha1());
        Utils.writeObject(blobFile, this);
    }

    public static Blob readFromFile(String sha1OfBlob) {
        File BlobFile = Utils.join(Repository.BLOB_DIR, sha1OfBlob);
        return Utils.readObject(BlobFile, Blob.class);
    }

    private String fileName;
    private byte[] contents;
    private String sha1;
}
