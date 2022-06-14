package son;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

public class FileTransferTest {

    public class Receiver implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket receiverServer = new ServerSocket(1234);                    
                Socket receiverSocket = receiverServer.accept();
                var receiver = new SyncReceiver(receiverSocket);
                receiver.receiveFile(createTestFolder("receiverFol"));
                receiverServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Sender implements Runnable {
        @Override
        public void run() {
            try {
                Socket senderSocket = new Socket("localhost", 1234);
                var sender = new SyncSender(senderSocket);
                var testFile = createFile(createTestFolder("senderFol"), "tesFiiiea");
                var fw = new FileWriter(testFile);
                fw.write("asdasdasdasdfajkshjwafahwejhfjkawehlfkjhlakjdshfljkhaslkjdfasdf");
                fw.flush();
                fw.close();
                sender.sendFile(testFile);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test void test() {       
        var receiver = new Thread(new Receiver());
        var sender = new Thread(new Sender());
        receiver.start();
        sender.start();
    }

    File createTestFolder(String name) {
        var testFolder = new File(name);
        testFolder.mkdir();
        return testFolder;
    }

    File createFile(File folder, String fileName) {
        File f = null;
        try {
            f = new File(folder, fileName);
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
