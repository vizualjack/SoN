package son.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import son.network.packet.BasePacket;
import son.network.packet.MessagePacket;
import son.network.packet.PacketType;
import son.util.TestHelper;

// tests have to run one after
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

    @Test void sendStringTest() {
        String testMsg = "tetteteete";

        var server = new Server(testPort);
        server.onConnected = s -> {
            var clientEnd = new Endpoint(s);
            clientEnd.sendString(testMsg);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var serverEnd = new Endpoint(s);
            String receiverMsg = serverEnd.readString();
            assertEquals(testMsg, receiverMsg);
        };
        client.connect();
    }

    @Test void sendPacketTest() {
        BasePacket testPacket = new BasePacket(PacketType.NOTMESSAGE);

        var server = new Server(testPort);
        server.onConnected = s -> {
            var clientEnd = new Endpoint(s);
            clientEnd.send(testPacket);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var serverEnd = new Endpoint(s);
            BasePacket receiverPacket = serverEnd.read();
            assertEquals(testPacket.packetType, receiverPacket.packetType);
        };
        client.connect();
    }

    @Test void mixedSendTest() {
        String testMsg = "tetteteete";
        BasePacket testPacket = new BasePacket(PacketType.NOTMESSAGE);

        var server = new Server(testPort);
        server.onConnected = s -> {
            var endpoint = new Endpoint(s);
            endpoint.sendString(testMsg);
            endpoint.send(testPacket);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var endpoint = new Endpoint(s);
            String receiverMsg = endpoint.readString();
            BasePacket receiverPacket = endpoint.read();
            assertEquals(testMsg, receiverMsg);
            assertEquals(testPacket.packetType, receiverPacket.packetType);
        };
        client.connect();
    }

    @Test void messagePacketTest() {
        var testPacket = new MessagePacket("testMesssagge");

        var server = new Server(testPort);
        server.onConnected = s -> {
            var clientEnd = new Endpoint(s);
            clientEnd.send(testPacket);
        };
        server.start();
        
        var client = new Client(testPort);
        client.onConnected = s -> {
            var serverEnd = new Endpoint(s);
            var receiverPacket = (MessagePacket)serverEnd.read();
            assertEquals(testPacket.packetType, receiverPacket.packetType);
            assertEquals(testPacket.getMessage(), receiverPacket.getMessage());
        };
        client.connect();
    }

    @Test void sendFileTest() {
        var senderFolder = TestHelper.createTestFolder("senderFol");
        var receiverFolder = TestHelper.createTestFolder("receiverFol");

        var senderTestFile = TestHelper.createFile(senderFolder, "testFiiile");
        assertNotNull(senderTestFile);
    }
}
