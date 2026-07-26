package gitlet;

import java.io.Serializable;
import java.util.Set;

public interface Buffer extends Serializable {

    void clear();

    void save();

    boolean isEmpty();

    boolean contain(String fileKey);

    void showContain();

    Set<String> getSet();
}
