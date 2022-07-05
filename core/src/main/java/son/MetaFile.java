package son;

import java.io.Serializable;

public class MetaFile implements Serializable {
    public String path;
    public long lastModified;

    public MetaFile(String path, long lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }
}
