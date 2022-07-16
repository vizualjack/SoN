package son.pc;

import java.io.File;
import java.io.IOException;

import son.Syncer;

public class App {
    public static void main(String[] args) throws IOException {
        File f = null;
        if(args.length >= 1) {
            f = new File(args[0]);
            System.out.println("Set syncFolder: " + f.getPath());
        }
        else {
            // System.out.println("Create test folder...");
            // f = new File("testFolder");
            // f.mkdir();
            System.err.println("No path set");
            return;
        }
  
        var syncer = new Syncer(new SyncFolderPC(f));
        while(true) {
            try {
                Thread.sleep(60000);
                System.out.println("Syncing...");
                syncer.sync();
            } catch (InterruptedException e) {
                System.out.println("Thread can't sleep");
            }
        }
    }
}