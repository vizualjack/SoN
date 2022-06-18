package son.network.packet;

import java.io.Serializable;

public class BasePacket implements Serializable {
    public PacketType packetType;

    public BasePacket(PacketType packetType) {
        this.packetType = packetType;
    }
}
