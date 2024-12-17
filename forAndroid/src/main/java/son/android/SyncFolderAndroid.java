package son.android;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import son.MetaFile;
import son.SyncFile;
import son.SyncFolder;

public class SyncFolderAndroid extends SyncFolder {
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

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
                DocumentFile searchInnerFile = file.findFile(pathPart);
                if(searchInnerFile == null)
                    file = file.createDirectory(pathPart);
                else
                    file = searchInnerFile;
            }
            else {
                file = file.createFile("", pathPart);
            }
        }
        return new SyncFileAndroid(syncFolder, file, context);
    }

    @Override
    public List<MetaFile> getMetaFiles() {
        List<MetaFile> metaFiles = new ArrayList<>();
        for(SyncFile syncFile : getSyncFiles(syncFolder)) {
            metaFiles.add(new MetaFile(syncFile.getPath(), syncFile.getLastModified(), syncFile.getChecksum()));
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
            if(fileInFolder.isFile() && fileInFolder.getName() != SYNC_FILE_NAME)
                files.add(new SyncFileAndroid(syncFolder, fileInFolder, context));
            else if (fileInFolder.isDirectory())
                files.addAll(getSyncFiles(fileInFolder));
        }
        return files;
    }
}
