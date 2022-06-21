package son.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class Client {
    public Consumer<Socket> onConnected;

    int port;
    

    public Client(int port) {
        this.port = port;
    }

    public void connect() {
        try {
            Socket server = new Socket("192.168.178.30", port);
            onConnected.accept(server);
            server.close();
        } 
        catch(UnknownHostException ex) {
            System.err.println("unknown host, change it");
        }
        catch(IOException ex) {
            ex.fillInStackTrace();
        }
    }
}
