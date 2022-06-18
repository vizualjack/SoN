package son.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ObjectInputStream;

import org.junit.jupiter.api.Test;

import son.network.packet.BasePacket;
import son.network.packet.PacketType;

public class NetworkTest {
    int testPort = 1234;

    boolean serverConnected = false,
            clientConnected = false;

    @Test void simpleConnectionTest() {
        var server = new Server(testPort);
        server.onConnected = socket -> {serverConnected = true;};
        server.start();

        var client = new Client(testPort);
        client.onConnected = socket -> {clientConnected = true;};
        client.connect();

        assertTrue(serverConnected);
        assertTrue(clientConnected);
    }

    @Test void simpleEndpointTest() {
        String testMsg = "tetteteete";

        var server = new Server(testPort);
        server.onConnected = s -> {
            var clientEnd = new SimpleEndpoint(s);
            clientEnd.send(testMsg);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var serverEnd = new SimpleEndpoint(s);
            String receiverMsg = serverEnd.read();
            assertEquals(testMsg, receiverMsg);
        };
        client.connect();
    }

    @Test void packetEndpointTest() {
        BasePacket testPacket = new BasePacket(PacketType.NOTMESSAGE);

        var server = new Server(testPort);
        server.onConnected = s -> {
            var clientEnd = new PacketEndpoint(s);
            clientEnd.send(testPacket);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var serverEnd = new PacketEndpoint(s);
            BasePacket receiverPacket = serverEnd.read();
            assertEquals(testPacket.packetType, receiverPacket.packetType);
        };
        client.connect();
    }

    @Test void mixedEndpointTest() {
        String testMsg = "tetteteete";
        BasePacket testPacket = new BasePacket(PacketType.NOTMESSAGE);

        var server = new Server(testPort);
        server.onConnected = s -> {
            var simple = new SimpleEndpoint(s);
            simple.send(testMsg);
            var packet = new PacketEndpoint(s);
            packet.send(testPacket);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var simple = new SimpleEndpoint(s);
            String receiverMsg = simple.read();
            var packet = new PacketEndpoint(s);
            BasePacket receiverPacket = packet.read();
            assertEquals(testMsg, receiverMsg);
            assertEquals(testPacket.packetType, receiverPacket.packetType);
        };
        client.connect();
    }
}
