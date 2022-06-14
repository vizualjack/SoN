package son;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class SyncSender {
    Socket socket;

    public SyncSender(Socket socket) {
        this.socket = socket;
    }

    public void sendFile(File file) {
        try {
            var socketOut = socket.getOutputStream();
            DataOutputStream dout = new DataOutputStream(socketOut);  
            dout.writeUTF(file.getName());
            dout.flush();
            System.out.println("Name sent!");
            var fileStream = new FileInputStream(file);
            var buffer = new byte[16 * 1024];
            int count;
            while((count = fileStream.read(buffer)) > 0) {
                socketOut.write(buffer, 0, count);
                System.out.println("Wrote to socket!");
            }
            fileStream.close();
            socketOut.close();
            socket.close();
            System.out.println("File send");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            System.out.println("Can't send");
        }
    }
}
