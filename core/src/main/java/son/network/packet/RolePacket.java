package son.network.packet;

public class RolePacket extends BasePacket {
    public enum Role {SENDER, RECEIVER}

    Role role;

    public RolePacket(Role role) {
        super(PacketType.ROLE);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
    
}
