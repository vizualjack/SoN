package son.android;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import son.SyncFile;

public class SyncFileAndroid extends SyncFile {
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

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
        String fullFilePath = file.getUri().getLastPathSegment();
        String fullBaseFolderPath =  baseFolder.getUri().getLastPathSegment();
        String realPath = fullFilePath.replace(fullBaseFolderPath + "/", "");
        return realPath.replace("/", "\\");
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public void delete() {
        try {
            if(!file.isDirectory() || (file.isDirectory() && file.listFiles().length <= 0)) {
                DocumentsContract.deleteDocument(context.getContentResolver(), file.getUri());
                new SyncFileAndroid(baseFolder, file.getParentFile(), context).delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileInputStream openInputStream() {
        try {
            inputPfd = context.getContentResolver().
                    openFileDescriptor(file.getUri(), "r");
            fileInputStream = new FileInputStream(inputPfd.getFileDescriptor());
        } catch (FileNotFoundException e) {
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
                    openFileDescriptor(file.getUri(), "w");
            fileOutputStream = new FileOutputStream(outputPfd.getFileDescriptor());
        } catch (FileNotFoundException e) {
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
    public long getLastModified() {
        return file.lastModified()/1000*1000;
    }

    @Override
    public void setLastModified(long lastModified) {
        // doesn't work for android
    }
}
