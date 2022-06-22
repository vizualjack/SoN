package son.network.packet;

public class FilePacket extends BasePacket {
    String filePath;
    long size;
    
    public FilePacket(String filePath, long size) {
        super(PacketType.FILE);
        this.filePath = filePath;
        this.size = size;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public long getSize() {
        return size;
    }
}
