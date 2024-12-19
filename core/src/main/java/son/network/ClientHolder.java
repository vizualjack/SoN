package son.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHolder implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final byte[] LOCAL_ADDRESS = {127,0,0,1};
    private final byte[] VIRTUAL_PHONE_ADDRESS = {10,0,2,16};  // cause getting local address sucks at virtual phone in android studio
    DatagramSocket udpEndpoint;
    String msg = "hey i'm a son user";
    Thread thread;
    int port;
    List<byte[]> clients;
    byte[] selfAddress;

    public ClientHolder(int port) {
        this.port = port;
        clients = new ArrayList<byte[]>();
        if(!createUdpEndpoint()) logger.debug("No udp endpoint so i'm useless");
    }

    private byte[] getLocalAddress() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasNetworkChanged() {
        byte[] localAddress = getLocalAddress();
        if(InetAddressHelper.compareAddresses(localAddress, selfAddress)) {
            return false;
        }
        else {
            logger.debug("Network address changed from addr " + InetAddressHelper.toString(selfAddress) + " to " + InetAddressHelper.toString(localAddress));
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
        if(thread == null) return;
        logger.debug("Stopping client holder...");
        udpEndpoint.close();
        udpEndpoint = null;
        try {
            thread.join();
        } catch (InterruptedException e) {
            logger.error("Can't join client holder thread");
            e.printStackTrace();
        }
        thread = null;
        clients.clear();
        logger.debug("Cleared client list");
        logger.debug("Client holder stopped");
    }

    public void start() {
        if(thread != null) return;
        selfAddress = getLocalAddress();
        if(!InetAddressHelper.isLocalAddress(selfAddress)) {
            udpEndpoint.close();
            udpEndpoint = null;
            return;
        }
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        if(udpEndpoint == null) return;
        var bytes = new byte[msg.length()];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        while(udpEndpoint != null) {       
            try {
                udpEndpoint.receive(packet);
                var packetAsStr = new String(packet.getData());
                if(packetAsStr.contentEquals(msg)) {
                    var curAddress = packet.getAddress().getAddress();
                    if(InetAddressHelper.compareAddresses(curAddress, selfAddress) ||
                    InetAddressHelper.compareAddresses(curAddress, LOCAL_ADDRESS) ||
                    InetAddressHelper.compareAddresses(curAddress, VIRTUAL_PHONE_ADDRESS)) continue;
                    if(!addToClients(curAddress)) continue;
                    if(logger.isDebugEnabled()) logger.debug("New SoN User appeared: {}", InetAddressHelper.toString(curAddress));
                    else logger.info("New SoN User appeared");
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
            broadcastAddress[3] = (byte)255;
            logger.debug("Send ping packet to all local network clients");
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
        return thread != null;
    }

    private boolean createUdpEndpoint() {
        try {
            udpEndpoint = new DatagramSocket(port);
            return true;
        } catch (SocketException e) {
            logger.error("Couldn't create DatagramSocket caused by: ", e);
            return false;
        }
    }
}
