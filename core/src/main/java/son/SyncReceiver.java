package son;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SyncReceiver {
    Socket socket;

    public SyncReceiver(Socket socket) {
        this.socket = socket;
    }

    public void receiveFile(File folder) {
        try {
            var socketIn = socket.getInputStream();
            DataInputStream dis = new DataInputStream(socketIn);
            String name = (String)dis.readUTF();
            System.out.println("Name received!");
            var fileStream = new FileOutputStream(new File(folder, name));
            var buffer = new byte[16 * 1024];            
            int count;
            while((count = socketIn.read(buffer)) > 0) {
                fileStream.write(buffer, 0, count);
                System.out.println("Wrote in filestream!");
            }
            fileStream.close();
            socketIn.close();
            socket.close();
            System.out.println("File received");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            System.out.println("Can't send");
        }
    }
}
