package gitlet;

import java.io.File;
import java.io.Serializable;

public interface Buffer extends Serializable {
    public void add(File filename, byte[] contents);

    public void clear();

    public void save();
}
