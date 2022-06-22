package son.linux;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import son.Syncer;
import son.network.Client;
import son.network.ClientHolder;

public class App {
    public static void main(String[] args) {
        var f = new File("testFolder");
        f.mkdir();
        var syncer = new Syncer(f);
        syncer.startServer();

        // var clientHolder = new ClientHolder(1337);
        // clientHolder.start();
        // System.out.println("clientholder started");
    }
}