package son.android;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.List;

import son.MetaFile;
import son.SyncFile;
import son.SyncFolder;

public class SyncFolderAndroid extends SyncFolder {
    Uri folderUri;
    Context context;
    DocumentFile syncFolder;

    public SyncFolderAndroid(Uri folderUri, Context context) {
        this.folderUri = folderUri;
        this.context = context;
        syncFolder = DocumentFile.fromTreeUri(context, folderUri);
    }

    @Override
    public SyncFile createSyncFile(String path) {
        String[] pathParts = path.split("\\\\");

        DocumentFile file = syncFolder;
        for(int i = 0; i < pathParts.length; i++) {
            String pathPart = pathParts[i];
            if(i < pathParts.length - 1) {
                file = file.findFile(pathPart);
                if(file == null)
                    file = file.createDirectory(pathPart);
            }
            else {
                file = file.createFile("", pathPart);
            }
        }
        return new SyncFileAndroid(syncFolder, file, context);
    }

    @Override
    public long getLastChangeOfFolder() {
        long latestFileChange = 0;
        for(MetaFile metaFile : getMetaFiles()) {
            if(metaFile.lastModified > latestFileChange)
                latestFileChange = metaFile.lastModified;
        }
        return latestFileChange;
    }

    @Override
    public List<MetaFile> getMetaFiles() {
        List<MetaFile> metaFiles = new ArrayList<>();
        for(SyncFile syncFile : getSyncFiles(syncFolder)) {
            metaFiles.add(new MetaFile(syncFile.getPath(), syncFile.getLastModified()));
        }
        return metaFiles;
    }

    @Override
    public SyncFile getSyncFile(String path) {
        String[] pathParts = path.split("\\\\");
        DocumentFile file = syncFolder;
        for(int i = 0; i < pathParts.length; i++) {
            String pathPart = pathParts[i];
            file = file.findFile(pathPart);
            if(file == null) break;
            if( (i < pathParts.length-1 && file.isFile()) ||
                (i == pathPart.length()-1 && file.isDirectory())) {
                file = null;
                break;
            }
        }
        return file == null ? null : new SyncFileAndroid(syncFolder, file, context);
    }

    private List<SyncFile> getSyncFiles(DocumentFile folder) {
        List<SyncFile> files = new ArrayList<>();
        for (DocumentFile fileInFolder : folder.listFiles()) {
            if(fileInFolder.isFile())
                files.add(new SyncFileAndroid(syncFolder, fileInFolder, context));
            else if (fileInFolder.isDirectory())
                files.addAll(getSyncFiles(fileInFolder));
        }
        return files;
    }
}
