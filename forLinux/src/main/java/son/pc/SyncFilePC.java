package son.pc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.text.rtf.RTFEditorKit;

import son.SyncFile;

public class SyncFilePC extends SyncFile {

    public File file;

    public SyncFilePC(File file) {
        this.file = file;
    }

    @Override
    public long getLastModified() {
        return (file.lastModified()/1000*1000);
    }

    @Override
    public long getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FileInputStream openInputStream() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void closeInputStream() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FileOutputStream openOutputStream() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void closeOutputStream() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLastModified(long lastModified) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getPath() {
        return file.getPath();
    }
    
}
