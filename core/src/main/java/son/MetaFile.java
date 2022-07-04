package son;

import java.io.File;

public class MetaFile {
    public String path;
    public long lastModified;

    public MetaFile(String path, long lastModified) {
        this.path = path;
    }
}
