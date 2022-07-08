package son.pc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import son.MetaFile;
import son.SyncFile;
import son.SyncFolder;

public class SyncFolderPC extends SyncFolder {
   public File folder;

    public SyncFolderPC(File syncFolder) {
        if(!syncFolder.isDirectory()) throw new RuntimeException("I need a folder/directory");
        this.folder = syncFolder;
    }

    @Override
    public SyncFile createSyncFile(String path) {
        try {
            createFolders(folder, path);
            var newFile = new File(folder, path);
            newFile.createNewFile();
            return new SyncFilePC(newFile);
        } catch (IOException e) {
            System.err.println("Can't create file");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getLastChangeOfFolder() {
        long latestFileChange = 0;
        for(var syncFile : getSyncFiles(folder)) {
            if(syncFile.getLastModified() > latestFileChange)
                latestFileChange = syncFile.getLastModified();
        }
        return latestFileChange;
    }

    @Override
    public List<MetaFile> getMetaFiles() {
        var metaFiles = new ArrayList<MetaFile>();
        for(var syncFile : getSyncFiles(folder)) {
            metaFiles.add(new MetaFile(relativePathFromSyncFolder(syncFile), syncFile.getLastModified()));
        }
        return metaFiles;
    }

    @Override
    public SyncFile getSyncFile(String filePath) {
        return new SyncFilePC(new File(folder, filePath));
    }

    private String relativePathFromSyncFolder(SyncFile syncFile) {
        var path = syncFile.getPath();
        var pathParts = path.split(folder.getName());
        return pathParts[pathParts.length-1].substring(1);
    }

    private void createFolders(File baseFolder, String filePath) {
        var lastSlashIndex = filePath.lastIndexOf("\\");
        if(lastSlashIndex == -1) return;
        var foldersPath = filePath.substring(0, lastSlashIndex);
        new File(baseFolder, foldersPath).mkdirs();
    }
    
    private List<SyncFile> getSyncFiles(File folder) {
        var files = new ArrayList<SyncFile>();
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isFile())
                files.add(new SyncFilePC(fileInFolder));
            else if (fileInFolder.isDirectory())
                files.addAll(getSyncFiles(fileInFolder));
        }        
        return files;
    }
}
