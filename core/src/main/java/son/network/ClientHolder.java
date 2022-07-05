package son.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.graph.Network;

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
        try {
            getLocalAddress();
            udpEndpoint = new DatagramSocket(port);
        } catch (SocketException | UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getLocalAddress() throws SocketException, UnknownHostException {
        try(DatagramSocket s=new DatagramSocket()) {
            s.connect(InetAddress.getByAddress(new byte[]{1,1,1,1}), 1337);
            selfAddress = s.getLocalAddress().getAddress();
        }
    }

    public void addToClients(byte[] addr) {
        for(var clientAddr : clients) {
            if(InetAddressHelper.compareAddresses(clientAddr, addr)) return;
        }
        clients.add(addr);
    }

    public List<byte[]> getClients() {
        return clients;
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
                    var curAddress = packet.getAddress().getAddress();
                    if(!InetAddressHelper.compareAddresses(curAddress, selfAddress)) {
                        System.out.println("SoN User found:" + curAddress);
                        addToClients(curAddress);
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
            var broadcastAddress = selfAddress.clone();
            broadcastAddress[3] = (byte)255;
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
}
