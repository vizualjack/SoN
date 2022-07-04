package son.linux;

import java.io.File;
import son.Syncer;

public class App {
    public static void main(String[] args) {
        var f = new File("testFolder");
        f.mkdir();
        var syncer = new Syncer(f);

        while(true) {
            try {
                Thread.sleep(60000);
                System.out.println("Syncing...");
                syncer.sync();
            } catch (InterruptedException e) {
                System.out.println("Thread can't sleep");
            }

        }
        // syncer.startServer();

        // var clientHolder = new ClientHolder(1337);
        // clientHolder.start();
        // System.out.println("clientholder started");
    }
}