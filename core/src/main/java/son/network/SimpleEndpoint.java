package son.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SimpleEndpoint {
    DataInputStream receiverStream;
    DataOutputStream senderStream;

    public SimpleEndpoint(Socket socket) {
        try {
            receiverStream = new DataInputStream(socket.getInputStream());
            senderStream = new DataOutputStream(socket.getOutputStream());  
        }
        catch(IOException ex) {
            System.err.println("Can't create endpoint");
        }
    }

    public String read() {
        String message = null;
        try {
            message = receiverStream.readUTF();
        }
        catch(IOException ex) {
            System.err.println("Can't read");   
            ex.printStackTrace();         
        }
        return message;
    }

    public void send(String message) {
        try {
            senderStream.writeUTF(message);
            senderStream.flush();
        }
        catch(IOException ex) {
            System.err.println("Can't send");
        }
    }
}