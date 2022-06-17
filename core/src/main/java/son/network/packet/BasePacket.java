package son.network.packet;

public class BasePacket {
    public PacketType packetType;

    public BasePacket(PacketType packetType) {
        this.packetType = packetType;
    }
}
