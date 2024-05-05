package son.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server implements Runnable {
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
            System.err.println("can't close or join serverthread");
            e.printStackTrace();
        }
        thread = null;
    }

    public void start() {
        if(thread != null) return;
        thread = new Thread(this);
        try {
            server = new ServerSocket(port);
            System.out.println("Sync Server created");
        } catch (IOException e) {
            System.err.println("Can't create serversocket");
            e.printStackTrace();
        }
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("Sync Server started");
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
