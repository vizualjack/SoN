package son;

import java.util.List;

public abstract class SyncFolder {
    public abstract SyncFile createSyncFile(String path);

    public abstract double getLastChangeOfFolder();

    public abstract List<MetaFile> getMetaFiles();

    public abstract SyncFile getSyncFile(String filePath);
}