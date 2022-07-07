package son.linux;

import java.io.File;
import java.io.IOException;

import son.Syncer;

public class App {
    public static void main(String[] args) throws IOException {
        var syncFolderPath = args[0];
        System.out.println("Set syncFolder: " + syncFolderPath);

        var f = new File(syncFolderPath);
        new File(f, "helloImA new fiii1le").createNewFile();
        
        
        // f.mkdir();
        // var syncer = new Syncer(f);

        // while(true) {
        //     try {
        //         Thread.sleep(10000);
        //         System.out.println("Syncing...");
        //         syncer.sync();
        //     } catch (InterruptedException e) {
        //         System.out.println("Thread can't sleep");
        //     }

        // }
        // syncer.startServer();

        // var clientHolder = new ClientHolder(1337);
        // clientHolder.start();
        // System.out.println("clientholder started");
    }
}