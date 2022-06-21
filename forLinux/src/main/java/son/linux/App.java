package son.linux;

import java.io.File;

import son.Syncer;

public class App {
    public static void main(String[] args) {
        var syncer = new Syncer(new File("testFolder"));
        syncer.startServer();
    }
}