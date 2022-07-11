package son;

import java.io.Serializable;

public class MetaFile implements Serializable {
    public String path, checksum;
    public long lastModified;

    public MetaFile(String path, long lastModified, String checksum) {
        this.path = path;
        this.lastModified = lastModified;
        this.checksum = checksum;
    }
}
