package son.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import son.SyncFile;
import son.network.packet.BasePacket;

public class Endpoint {
    private static final Logger logger = LoggerFactory.getLogger(Endpoint.class);

    Socket socket;
    int bufferSize = 16 * 1024;

    public Endpoint(Socket socket) {
        this.socket = socket;
    }

    public boolean receiveSyncFile(SyncFile syncFile, long size) {
        try {
            var socketIn = socket.getInputStream();
            var fileStream = syncFile.openOutputStream();
            int count;
            byte[] buffer = new byte[bufferSize];
            long received = 0L;
            while((count = socketIn.read(buffer)) > 0) {
                fileStream.write(buffer, 0, count);
                received += count;
                if(received >= size) break;
            }
            syncFile.closeOutputStream();
            return true;
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean sendSyncFile(SyncFile syncFile) {
        try {
            var socketOut = socket.getOutputStream();
            var fileStream = syncFile.openInputStream();
            int count;
            byte[] buffer = new byte[bufferSize];
            while((count = fileStream.read(buffer)) > 0) {
                socketOut.write(buffer, 0, count);
            }
            syncFile.closeInputStream();
            return true;
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public BasePacket read() {
        BasePacket packet = null;
        try {
            packet = (BasePacket) new ObjectInputStream(socket.getInputStream()).readObject();
        }
        catch(ClassNotFoundException | IOException ex) {
            logger.error("Can't receive base packet: ", ex);
        }
        return packet;
    }

    public void send(BasePacket packet) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(packet);
        }
        catch(IOException ex) {
            logger.error("Can't send base packet: ", ex);
        }
    }

    public String readString() {
        String message = null;
        try {
            var receiverStream = new DataInputStream(socket.getInputStream());
            message = receiverStream.readUTF();
        }
        catch(IOException ex) {  
            logger.error("Can't receive string packet: ", ex);
        }
        return message;
    }

    public void sendString(String message) {
        try {
            var senderStream = new DataOutputStream(socket.getOutputStream());  
            senderStream.writeUTF(message);
            senderStream.flush();
        }
        catch(IOException ex) {
            logger.error("Can't send string packet: ", ex);
        }
    }
}
