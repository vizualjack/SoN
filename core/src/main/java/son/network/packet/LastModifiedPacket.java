package son.network.packet;

public class LastModifiedPacket extends BasePacket {
    long lastModified;

    public LastModifiedPacket(long lastModified) {
        super(PacketType.LAST_MODIFIED);
        this.lastModified = lastModified;
    }

    public long getLastModified() {
        return lastModified;
    }
}
