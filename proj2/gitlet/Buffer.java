package gitlet;

import java.io.File;
import java.io.Serializable;

public interface Buffer extends Serializable {

    public void clear();

    public void save();

    public boolean isEmpty();

    public boolean contain(String fileKey);

    public void showContain();
}
