package son;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public abstract class SyncFile {
    public abstract String getPath();

    public abstract double getLastModified();

    public abstract long getSize();

    public abstract void delete();

    public abstract FileInputStream openInputStream();
    
    public abstract void closeInputStream();

    public abstract FileOutputStream openOutputStream();
    
    public abstract void closeOutputStream();

    public abstract void setLastModified(long lastModified);
}
