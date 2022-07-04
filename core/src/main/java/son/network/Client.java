package son.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class Client {
    public Consumer<Socket> onConnected;
    public boolean timedOut = false;

    int port;    


    public Client(int port) {
        this.port = port;
    }

    public void connect(String address) {
        try {
            Socket server = new Socket(address, port);
            onConnected.accept(server);
            server.close();
        }
        catch(UnknownHostException ex) {
            System.err.println("unknown host, change it");
        }
        catch(ConnectException ex) {
            timedOut = ex.getMessage().contains("Connection timed out");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
