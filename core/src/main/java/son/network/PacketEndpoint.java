package son.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import son.network.packet.BasePacket;

public class PacketEndpoint {
    Socket socket;

    public PacketEndpoint(Socket socket) {
        this.socket = socket;
    }

    public BasePacket read() {
        BasePacket packet = null;
        try {
            packet = (BasePacket) new ObjectInputStream(socket.getInputStream()).readObject();
        }
        catch(ClassNotFoundException ex) {
            System.err.println("class not found");   
            ex.printStackTrace();  
        }
        catch(IOException ex) {
            System.err.println("Can't read");   
            ex.printStackTrace();         
        }   
        return packet;
    }

    public void send(BasePacket packet) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(packet);
        }
        catch(IOException ex) {
            System.err.println("Can't send");   
            ex.printStackTrace();         
        }
    }
}
