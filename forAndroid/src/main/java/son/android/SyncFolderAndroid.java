package son.android;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.util.List;

import son.MetaFile;
import son.SyncFile;
import son.SyncFolder;

public class SyncFolderAndroid extends SyncFolder {
    Uri folderUri;
    Context context;
    DocumentFile folder;

    public SyncFolderAndroid(Uri folderUri, Context context) {
        this.folderUri = folderUri;
        this.context = context;
        folder = DocumentFile.fromTreeUri(context, folderUri);
    }

    @Override
    public SyncFile createSyncFile(String path) {
//        DocumentFile newFile = folder.createFile("", "letssgooo");
//        folder.createDirectory()
        String[] pathParts = path.split("\\");


        DocumentFile documentFile = null;
        for(int i = 0; i < pathParts.length; i++) {
            String pathPart = pathParts[i];
            if(i < pathParts.length - 1) {
                documentFile = folder.findFile(pathPart);
                if(documentFile == null)
                    documentFile = documentFile.createDirectory(pathPart);
            }
            else {
                documentFile = documentFile.createFile("", pathPart);
            }
        }
        return new SyncFileAndroid(documentFile, context);
    }

    @Override
    public long getLastChangeOfFolder() {
        return 0;
    }

    @Override
    public List<MetaFile> getMetaFiles() {
        return null;
    }

    @Override
    public SyncFile getSyncFile(String filePath) {
        return null;
    }
}
