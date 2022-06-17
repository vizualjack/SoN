package son.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

public class Server implements Runnable {
    public Function<Socket,Boolean> onConnected;

    int port;
    Thread thread;

    public Server(int port) {
        this.port = port;
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
                onConnected.apply(client);
                server.close();
            }
            catch(IOException ex) {
                ex.fillInStackTrace();
            }
        }
    }

}
