package son.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import son.network.packet.BasePacket;

public class Endpoint {
    Socket socket;
    int bufferSize = 16 * 1024;
    byte[] buffer = new byte[bufferSize];

    public Endpoint(Socket socket) {
        this.socket = socket;
    }

    public boolean receiveFile(String filePath) {
        try {
            var socketIn = socket.getInputStream();
            var fileStream = new FileOutputStream(new File(filePath));
            int count;
            while((count = socketIn.read(buffer)) > 0) {
                fileStream.write(buffer, 0, count);
            }
            return true;
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean sendFile(File file) {
        try {
            var socketOut = socket.getOutputStream();
            var fileStream = new FileInputStream(file);
            int count;
            while((count = fileStream.read(buffer)) > 0) {
                socketOut.write(buffer, 0, count);
            }
            fileStream.close();
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

    public String readString() {
        String message = null;
        try {
            var receiverStream = new DataInputStream(socket.getInputStream());
            message = receiverStream.readUTF();
        }
        catch(IOException ex) {  
            ex.printStackTrace();         
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
            ex.printStackTrace();         
        }
    }
}
