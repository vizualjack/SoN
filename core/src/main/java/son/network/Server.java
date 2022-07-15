package son.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server implements Runnable {
    public Consumer<Socket> onConnected;

    int port;
    Thread thread;

    public Server(int port) {
        this.port = port;
    }

    public void restart() {
        stop();
        start();
    }

    public void stop() {
        if(thread == null) return;
        thread.interrupt();
        thread = null;
    }

    public void start() {
        if(thread != null) return;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true) {
            try {
                ServerSocket server = new ServerSocket(port);
                Socket client = server.accept(); 
                onConnected.accept(client);
                client.close();
                server.close();
            }
            catch(IOException ex) {
                ex.fillInStackTrace();
            }
        }
    }

}
