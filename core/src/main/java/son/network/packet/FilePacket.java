package son.network.packet;

public class FilePacket extends BasePacket {
    String filePath;
    
    public FilePacket(String filePath) {
        super(PacketType.FILE);
        this.filePath = filePath;
    }
    
    public String getFilePath() {
        return filePath;
    }
}
