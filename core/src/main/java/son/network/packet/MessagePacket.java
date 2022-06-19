package son.network.packet;

public class MessagePacket extends BasePacket {
    String message;

    public MessagePacket(String message) {
        super(PacketType.MESSAGE);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
