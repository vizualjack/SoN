package son.pc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import son.MetaFile;
import son.SyncFile;
import son.SyncFolder;

public class SyncFolderPC extends SyncFolder {
   public File syncFolder;

    public SyncFolderPC(File syncFolder) {
        if(!syncFolder.isDirectory()) throw new RuntimeException("I need a folder/directory");
        this.syncFolder = syncFolder;
    }

    @Override
    public SyncFile createSyncFile(String path) {
        try {
            createFolders(syncFolder, path);
            var newFile = new File(syncFolder, path);
            newFile.createNewFile();
            return new SyncFilePC(syncFolder, newFile);
        } catch (IOException e) {
            System.err.println("Can't create file");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MetaFile> getMetaFiles() {
        var metaFiles = new ArrayList<MetaFile>();
        for(var syncFile : getSyncFiles(syncFolder)) {
            metaFiles.add(new MetaFile(syncFile.getPath(), syncFile.getLastModified(), syncFile.getChecksum()));
        }
        return metaFiles;
    }

    @Override
    public SyncFile getSyncFile(String filePath) {
        File file = new File(syncFolder, filePath);
        if(!file.exists()) return null;
        return new SyncFilePC(syncFolder, file);
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
                files.add(new SyncFilePC(syncFolder, fileInFolder));
            else if (fileInFolder.isDirectory())
                files.addAll(getSyncFiles(fileInFolder));
        }        
        return files;
    }
}
