package son;

import java.util.List;

public abstract class SyncFolder {
    public abstract SyncFile createSyncFile(String path);

    public double getLastChangeOfFolder() {
        double latestFileChange = 0;
        for(MetaFile metaFile : getMetaFiles()) {
            if(metaFile.lastModified > latestFileChange)
                latestFileChange = metaFile.lastModified;
        }
        return latestFileChange;
    }

    public abstract List<MetaFile> getMetaFiles();

    public abstract SyncFile getSyncFile(String filePath);
}