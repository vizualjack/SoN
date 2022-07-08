package son.pc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import son.SyncFile;

public class SyncFilePC extends SyncFile {

    public File file;
    FileInputStream inputStream;
    FileOutputStream outputStream;

    public SyncFilePC(File file) {
        this.file = file;
    }

    @Override
    public long getLastModified() {
        return (file.lastModified()/1000*1000);
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public void delete() {
        file.delete();
    }

    @Override
    public FileInputStream openInputStream() {
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("Can't found file");
            e.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public void closeInputStream() {
        try {
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Can't close input stream");
            e.printStackTrace();
        }
    }

    @Override
    public FileOutputStream openOutputStream() {
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("Can't found file");
            e.printStackTrace();
        }
        return outputStream;
    }

    @Override
    public void closeOutputStream() {
        try {
            outputStream.close();
        } catch (IOException e) {
            System.err.println("Can't close output stream");
            e.printStackTrace();
        }
    }

    @Override
    public void setLastModified(long lastModified) {
        file.setLastModified(lastModified);
    }

    @Override
    public String getPath() {
        return file.getPath();
    }
    
}
