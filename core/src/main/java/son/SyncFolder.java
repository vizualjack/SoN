package son;

import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class SyncFolder {
    protected final String SYNC_FILE_NAME = ".lastSync";

    public long getLastSyncTime() {
        var syncFile = getSyncFile(SYNC_FILE_NAME);
        if(syncFile == null) return 0;
        try {
            DataInputStream dis = new DataInputStream(syncFile.openInputStream());
            var lastSyncTime = dis.readLong();
            syncFile.closeInputStream();
            return lastSyncTime;
        } catch(Exception e) {e.printStackTrace();}
        return 0;
    }

    public void setLastSyncTime(long lastSyncTime) {
        var syncFile = getSyncFile(SYNC_FILE_NAME);
        if(syncFile == null) createSyncFile(SYNC_FILE_NAME);
        if (syncFile == null) {
            System.out.println("No last sync file, i skip");
            return;
        }
        try {
            DataOutputStream dos = new DataOutputStream(syncFile.openOutputStream());
            dos.writeLong(lastSyncTime);
            syncFile.closeOutputStream();
        } catch(Exception e) {e.printStackTrace();}
    }

    public long getLastChangeOfFolder() {
        long latestFileChange = 0;
        for(MetaFile metaFile : getMetaFiles()) {
            if(metaFile.lastModified > latestFileChange)
                latestFileChange = metaFile.lastModified;
        }
        return latestFileChange;
    }

    public abstract List<MetaFile> getMetaFiles();

    public abstract SyncFile getSyncFile(String filePath);

    public abstract SyncFile createSyncFile(String path);
}