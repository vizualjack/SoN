package son.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Server implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Endpoint.class);

    public Consumer<Socket> onConnected;

    int port;
    Thread thread;
    ServerSocket server;

    public Server(int port) {
        this.port = port;
    }

    public void restart() {
        stop();
        start();
    }

    public void stop() {
        if(thread == null) return;
        try {
            server.close();
            server = null;
            thread.join();
        } catch (InterruptedException | IOException e) {
            logger.error("Can't close or join serverthread: ", e);
        }
        thread = null;
    }

    public void start() {
        if(thread != null) return;
        thread = new Thread(this);
        try {
            server = new ServerSocket(port);
            logger.debug("Created");
        } catch (IOException e) {
            logger.error("Can't create ServerSocket", e);
        }
        thread.start();
    }

    @Override
    public void run() {
        logger.info("Started");
        while(server != null) {
            try {
                Socket client = server.accept(); 
                onConnected.accept(client);
                client.close();
            }
            catch(IOException ex) {
                ex.fillInStackTrace();
            }
        }
    }

}
