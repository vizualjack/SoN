package son.android;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
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
        String lastSegment = file.getUri().getLastPathSegment();
        String[] pathParts = lastSegment.split(baseFolder.getName());
        String path = pathParts[pathParts.length-1];
        return path.substring(1).replace("/", "\\");
    }

    @Override
    public long getLastModified() {
        return file.lastModified()/1000*1000;
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
                    openFileDescriptor(file.getUri(), "w");
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
        // dont work for android
       try {
//           file.getUri()
           System.out.println("Set last modified ANDROID");
//           ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(file.getUri(), "rw");
//           if (pfd == null) {
//               System.out.println("No ParcelFileDescriptor");
//               return;
//           }
//           File f = new File(file.getUri().getPath());
//           System.out.println("last modified prev: " + f.lastModified());
//           if(f.setLastModified(lastModified)) System.out.println("Last modified set!");
//           System.out.println("last modified after: " + f.lastModified());
//           pfd.close();  // Close the ParcelFileDescriptor to release resources
//           ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(file.getUri(), "rw");
//           pfd.getFileDescriptor()
//           ContentValues updateValues = new ContentValues();
//           updateValues.put(DocumentsContract.Document.COLUMN_LAST_MODIFIED, lastModified);
//           context.getContentResolver().insert(file.getUri(), updateValues);
//           System.out.println("Changes: " + changes);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}
