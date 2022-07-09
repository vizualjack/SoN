package son.network.packet;

public class LastModifiedPacket extends BasePacket {
    double lastModified;

    public LastModifiedPacket(double lastModified) {
        super(PacketType.LAST_MODIFIED);
        this.lastModified = lastModified;
    }

    public double getLastModified() {
        return lastModified;
    }
}
