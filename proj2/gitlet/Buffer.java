package gitlet;

import java.io.Serializable;

public interface Buffer extends Serializable {

    void clear();

    void save();

    boolean isEmpty();

    boolean contain(String fileKey);

    void showContain();
}
