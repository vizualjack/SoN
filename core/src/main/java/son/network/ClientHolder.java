package son.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClientHolder implements Runnable{
    DatagramSocket udpEndpoint;
    String msg = "hey i'm a son user";
    Thread t;
    int port;
    List<byte[]> clients;
    byte[] selfAddress;

    public ClientHolder(int port) {
        this.port = port;
        clients = new ArrayList<byte[]>();
        if(!createUdpEndpoint()) System.out.println("ClientHolder - No udp endpoint so i'm useless");
    }

    private byte[] getLocalAddress() {
        byte[] address = null;
        try {
            udpEndpoint.connect(InetAddress.getByAddress(new byte[]{1,1,1,1}), 1337);
            address = udpEndpoint.getLocalAddress().getAddress();
            udpEndpoint.disconnect();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public boolean hasNetworkChanged() {
        byte[] localAddress = getLocalAddress();
        if(InetAddressHelper.compareAddresses(localAddress, selfAddress)) {
            return false;
        }
        else {
            System.out.println("Changed from addr " + InetAddressHelper.toString(selfAddress) + " to " + InetAddressHelper.toString(localAddress));
            return true;
        }    
    }

    public boolean addToClients(byte[] addr) {
        for(var clientAddr : clients) {
            if(InetAddressHelper.compareAddresses(clientAddr, addr)) return false;
        }
        clients.add(addr);
        return true;
    }

    public List<byte[]> getClients() {
        return clients;
    }

    public void restart() {
        stop();
        start();
    }

    public void stop() {
        if(t == null) return;
        System.out.println("ClientHolder - Stopping client holder...");
        udpEndpoint.close();
        udpEndpoint = null;
        try {
            t.join();
        } catch (InterruptedException e) {
            System.err.println("ClientHolder - Can't join client holder thread");
            e.printStackTrace();
        }
        t = null;
        clients.clear();
        System.out.println("ClientHolder - Cleared client list");
        System.out.println("ClientHolder - Client holder stopped");
    }

    public void start() {
        selfAddress = getLocalAddress();
        if(!InetAddressHelper.isLocalAddress(selfAddress)) return;
        if(t != null) return;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        if(udpEndpoint == null) return;
        var bytes = new byte[msg.length()];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        while(udpEndpoint != null) {       
            try {
                // System.out.println("wait for packet");
                udpEndpoint.receive(packet);
                // System.out.println("packet received");
                var packetAsStr = new String(packet.getData());
                // System.out.println("packet as string: " + packetAsStr);
                if(packetAsStr.contentEquals(msg)) {
                    var curAddress = packet.getAddress().getAddress();
                    if(InetAddressHelper.compareAddresses(curAddress, selfAddress)) continue;
                    if(addToClients(curAddress)) System.out.println("ClientHolder - New SoN User found: " + InetAddressHelper.toString(curAddress));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pingToClients() {
        if(udpEndpoint == null) return;
        try {
            var bytes = msg.getBytes();
            var broadcastAddress = selfAddress.clone();
//            broadcastAddress[0] = (byte)10;
//            broadcastAddress[1] = (byte)0;
//            broadcastAddress[2] = (byte)2;
            broadcastAddress[3] = (byte)255;
            System.out.println("ClientHolder - Ping to all clients: " + InetAddressHelper.toString(broadcastAddress));
            var inetBroadcastAddress = InetAddress.getByAddress(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetBroadcastAddress, port);
            udpEndpoint.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(byte[] clientAddress) {
        clients.remove(clientAddress);
    }

    public boolean isActive() {
        return t != null;
    }

    private boolean createUdpEndpoint() {
        try {
            udpEndpoint = new DatagramSocket(port);
            return true;
        } catch (SocketException e) {
            System.out.println("ClientHolder - Couldn't create DatagramSocket caused by: ");
            e.printStackTrace();
            return false;
        }
    }
}
