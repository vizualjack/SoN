package son.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Function;

public class Client {
    public Function<Socket,Boolean> onConnected;

    int port;


    public Client(int port) {
        this.port = port;
    }

    public void connect() {
        try {
            Socket server = new Socket("localhost", port);
            onConnected.apply(server);
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
