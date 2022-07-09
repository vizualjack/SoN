package son;

import java.io.Serializable;

public class MetaFile implements Serializable {
    public String path;
    public double lastModified;

    public MetaFile(String path, double lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }
}
