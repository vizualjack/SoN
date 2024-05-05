package son;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SyncEstablisher implements Runnable {
    private int syncPort = 1337;
    private boolean stopped = false;
    private String startSyncMessage = "hiwad83urodfösasd-fasdf4üä0rit4w3+t+#w43ti90ß";

    Thread thread;
    public String lastMsg;

    public SyncEstablisher() {
        start();
    }

    public void start() {
        if(thread != null) return;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        stopped = true;
        thread.interrupt();
    }

    @Override
    public void run() {
        tryToSyncWithAnother();
        waitForSync();
    }

    private void tryToSyncWithAnother() {
        try {
            Socket socket = new Socket("localhost", syncPort);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());  
            dout.writeUTF(startSyncMessage);
            dout.flush();
            dout.close();  
            socket.close();  
        } catch (UnknownHostException e) {
            System.err.println("Unknown host!");
        } catch (IOException e) {
            System.out.println("Nobody there to sync with...");
        }
    }

    private void waitForSync() {
        try {
            ServerSocket ss = new ServerSocket(syncPort);
            while(!stopped) {
                try {
                    Socket s = ss.accept();
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    String str = (String)dis.readUTF();  
                    if(startSyncMessage.equals(str)) {
                        lastMsg = str; 
                    }
                } catch (IOException ex) {
                    System.out.println("i guess its a timeout so i start new...");
                }        
            }
            ss.close();
        } catch (IOException ex) {
            System.out.println("can't start the serverSocket");
        }      
    }
}
