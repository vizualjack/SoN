package son;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Syncer {
    public File syncFolder;

    public Syncer(File syncFolder) {
        if(!syncFolder.isDirectory()) throw new RuntimeException("I need a folder/directory");
        this.syncFolder = syncFolder;
    }

    public List<String> getFolders() {
        return getFolders(syncFolder);
    }

    public List<String> getFiles() {
        return getFiles(syncFolder);
    }

    private List<String> getFolders(File folder) {
        var paths = new ArrayList<String>();
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isDirectory()) {
                paths.add(relativePathFromSyncFolder(fileInFolder));
                paths.addAll(getFolders(fileInFolder));
            }
        }        
        return paths;
    }

    private List<String> getFiles(File folder) {
        var paths = new ArrayList<String>();
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isFile())
                paths.add(relativePathFromSyncFolder(fileInFolder));
            else if (fileInFolder.isDirectory())
                paths.addAll(getFiles(fileInFolder));
        }        
        return paths;
    }

    private String relativePathFromSyncFolder(File file) {
        var path = file.toPath().toString();
        return path.replace(syncFolder.getName() + "\\", "");
    }
}