package son.pc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import son.SyncFile;

public class SyncFilePC extends SyncFile {
    private static final Logger logger = LoggerFactory.getLogger(SyncFilePC.class);

    public File baseFolder, file;
    FileInputStream inputStream;
    FileOutputStream outputStream;

    public SyncFilePC(File baseFolder, File file) {
        this.baseFolder = baseFolder;
        this.file = file;
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
        if(!file.isDirectory() || (file.isDirectory() && file.listFiles().length <= 0)) {
            file.delete();
            new SyncFilePC(baseFolder, file.getParentFile()).delete();
        }
    }

    @Override
    public FileInputStream openInputStream() {
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("Can't found file");
            e.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public void closeInputStream() {
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.error("Can't close input stream");
            e.printStackTrace();
        }
    }

    @Override
    public FileOutputStream openOutputStream() {
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("Can't found file");
            e.printStackTrace();
        }
        return outputStream;
    }

    @Override
    public void closeOutputStream() {
        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error("Can't close output stream");
            e.printStackTrace();
        }
    }

    @Override
    public void setLastModified(long lastModified) {
        logger.debug("Set last modified {} to file {}", lastModified, file.getName());
        file.setLastModified(lastModified);
    }

    @Override
    public String getPath() {
        var path = file.getPath();
        return path.replace(baseFolder.getPath() + "\\", "");
    }
    
}
