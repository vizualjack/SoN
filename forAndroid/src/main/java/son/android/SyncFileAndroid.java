package son.android;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import son.SyncFile;

public class SyncFileAndroid extends SyncFile {
    DocumentFile baseFolder, file;
    Context context;
    FileInputStream fileInputStream;
    FileOutputStream fileOutputStream;
    ParcelFileDescriptor inputPfd, outputPfd;

    public SyncFileAndroid(DocumentFile baseFolder, DocumentFile file, Context context) {
        this.baseFolder = baseFolder;
        this.file = file;
        this.context = context;
    }

    @Override
    public String getPath() {
        StringBuffer pathBuilder = new StringBuffer();
        DocumentFile currentFile = null;
        do {
            if(currentFile == null) currentFile = file;
            else currentFile = currentFile.getParentFile();
            pathBuilder.insert(0, "/" + currentFile.getName());
        } while(currentFile.getName() != baseFolder.getName());
        return pathBuilder.substring(1);
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public void delete() {
        try {
            DocumentsContract.deleteDocument(context.getContentResolver(), file.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileInputStream openInputStream() {
        try {
            inputPfd = context.getContentResolver().
                    openFileDescriptor(file.getUri(), "w");
            fileInputStream = new FileInputStream(inputPfd.getFileDescriptor());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }

    @Override
    public void closeInputStream() {
        try {
            fileInputStream.close();
            inputPfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileOutputStream openOutputStream() {
        try {
            outputPfd = context.getContentResolver().
                    openFileDescriptor(file.getUri(), "r");
            fileOutputStream = new FileOutputStream(outputPfd.getFileDescriptor());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOutputStream;
    }

    @Override
    public void closeOutputStream() {
        try {
            fileOutputStream.close();
            outputPfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLastModified(long lastModified) {
        try {
            ContentValues updateValues = new ContentValues();
            updateValues.put(DocumentsContract.Document.COLUMN_LAST_MODIFIED, lastModified);
            Uri docUri = file.getUri();
            context.getContentResolver().update(docUri, updateValues, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
