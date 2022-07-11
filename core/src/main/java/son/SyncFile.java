package son;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class SyncFile {
    private final int CHECKSUM_ACCURACY = 10;

    public String getChecksum() {
        try {
            StringBuilder sb = new StringBuilder();
            long size = getSize();
            long bytesOffset = size / CHECKSUM_ACCURACY;
            if(bytesOffset == 0) bytesOffset = 1;
            var in = openInputStream();
            byte[] buffer = new byte[1];
            for(long curOffset = 0; curOffset < size; curOffset += bytesOffset) {
                in.read(buffer);
                sb.append(buffer[0]);
            }
            closeInputStream();
            sb.append(size);
            return sb.toString();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;       
    }

    public abstract String getPath();

    public abstract long getLastModified();

    public abstract long getSize();

    public abstract void delete();

    public abstract FileInputStream openInputStream();
    
    public abstract void closeInputStream();

    public abstract FileOutputStream openOutputStream();
    
    public abstract void closeOutputStream();

    public abstract void setLastModified(long lastModified);
}
