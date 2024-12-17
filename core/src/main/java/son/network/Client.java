package son.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Consumer<Socket> onConnected;
    public boolean timedOut = false;

    int port;    


    public Client(int port) {
        this.port = port;
    }

    public void connect(String address) {
        try {
            connect(InetAddress.getByName(address).getAddress());
        } catch (UnknownHostException e) {
            logger.error("{}", e);
        }
    }

    public void connect(byte[] address) {
        try {
            Socket server = new Socket(InetAddress.getByAddress(address), port);
            onConnected.accept(server);
            server.close();
        }
        catch(UnknownHostException ex) {
            logger.error("Unknown host, change it or connect it");
        }
        catch(ConnectException ex) {
            timedOut = ex.getMessage().contains("Connection timed out");
        }
        catch(IOException ex) {
            logger.error("Exception: ", ex);
        }
    }
}
