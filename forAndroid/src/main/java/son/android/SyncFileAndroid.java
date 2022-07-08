package son.android;

import android.content.Context;

import androidx.documentfile.provider.DocumentFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import son.SyncFile;

public class SyncFileAndroid extends SyncFile {
    DocumentFile documentFile;
    Context context;

    public SyncFileAndroid(DocumentFile documentFile, Context context) {
        this.documentFile = documentFile;
        this.context = context;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public void delete() {

    }

    @Override
    public FileInputStream openInputStream() {
        return null;
    }

    @Override
    public void closeInputStream() {

    }

    @Override
    public FileOutputStream openOutputStream() {
        return null;
    }

    @Override
    public void closeOutputStream() {

    }

    @Override
    public void setLastModified(long lastModified) {

    }
}
