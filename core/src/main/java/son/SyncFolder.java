package son;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SyncFolder {
    public File folder;

    public SyncFolder(File syncFolder) {
        if(!syncFolder.isDirectory()) throw new RuntimeException("I need a folder/directory");
        this.folder = syncFolder;
    }

    public List<File> getFolders() {
        return getFolders(folder);
    }

    public List<File> getFiles() {
        return getFiles(folder);
    }

    public long getLastChangeOfFolder() {
        return folder.lastModified();
    }

    private List<File> getFolders(File folder) {
        var folders = new ArrayList<File>();
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isDirectory()) {
                folders.add(fileInFolder);
                folders.addAll(getFolders(fileInFolder));
            }
        }        
        return folders;
    }

    private List<File> getFiles(File folder) {
        var files = new ArrayList<File>();
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isFile())
                files.add(fileInFolder);
            else if (fileInFolder.isDirectory())
                files.addAll(getFiles(fileInFolder));
        }        
        return files;
    }

    private String relativePathFromSyncFolder(File file) {
        var path = file.toPath().toString();
        var pathParts = path.split(folder.getName());
        return pathParts[pathParts.length-1].substring(1);
    }

    public List<MetaFile> getMetaFiles() {
        var metaFiles = new ArrayList<MetaFile>();
        for(var file : getFiles()) {
            metaFiles.add(new MetaFile(relativePathFromSyncFolder(file), file.lastModified()));
        }
        return metaFiles;
    }

    public File getFileForRelativePath(String filePath) {
        return new File(folder, filePath);
    }
}