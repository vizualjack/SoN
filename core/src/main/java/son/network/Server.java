package son.network;

public class Server implements Runnable {
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
        // Wait here for clients
    }

}
