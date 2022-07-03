package son.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkClientHolder implements ClientHolder, Runnable{
    DatagramSocket udpEndpoint;
    String msg = "hey i'm a son user";
    Thread t;
    int port;
    String foundClient;
    String selfAddress;

    public NetworkClientHolder(int port) {
        this.port = port;
        try {
            selfAddress = InetAddress.getLocalHost().getHostAddress();
            udpEndpoint = new DatagramSocket(port);
        } catch (SocketException | UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        if(t != null) return;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        if(udpEndpoint == null) return;
        sendPacketToAll();
        var bytes = new byte[msg.length()];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        while(true) {       
            try {
                System.out.println("wait for packet");
                udpEndpoint.receive(packet);
                System.out.println("packet received");
                if(new String(packet.getData()).contentEquals(msg)) {
                    var curAddress = packet.getAddress().getHostAddress();
                    if(!curAddress.contentEquals(selfAddress)) {
                        System.out.println("SoN User found:" + curAddress);
                        foundClient = curAddress;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void sendPacketToAll() {
        try {
            var bytes = msg.getBytes();
            var broadcastAddress = InetAddress.getLocalHost().getAddress();
            broadcastAddress[3] = (byte)255;
            var inetBroadcastAddress = InetAddress.getByAddress(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetBroadcastAddress, port);
            udpEndpoint.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClient() {
        return foundClient;
    }
}
