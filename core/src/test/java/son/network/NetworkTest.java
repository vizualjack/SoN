package son.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

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
        client.connect("localhost");

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
        client.connect("localhost");
    }

    @Test void sendPacketTest() {
        BasePacket testPacket = new BasePacket(PacketType.MESSAGE);

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
        client.connect("localhost");
    }

    @Test void mixedSendTest() {
        String testMsg = "tetteteete";
        BasePacket testPacket = new BasePacket(PacketType.MESSAGE);

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
        client.connect("localhost");
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
        client.connect("localhost");
    }

    @Test void sendFileTest() {
        var senderFolder = TestHelper.createTestFolder("senderFol");
        var receiverFolder = TestHelper.createTestFolder("receiverFol");

        var senderTestFile = TestHelper.createFileAndFillWithContent(senderFolder, "testFiiile", "this is just a test");
        assertNotNull(senderTestFile);

        var server = new Server(testPort);
        server.onConnected = s -> {
            var endpoint = new Endpoint(s);
            endpoint.sendFile(senderTestFile);
        };
        server.start();

        var client = new Client(testPort);
        client.onConnected = s -> {
            var endpoint = new Endpoint(s);
            var receivedFile = new File(receiverFolder, senderTestFile.getName());
            assertTrue(endpoint.receiveFile(receivedFile, receivedFile.length()));
            assertEquals(TestHelper.readFromFile(receivedFile), TestHelper.readFromFile(senderTestFile));
        };
        client.connect("localhost");

        TestHelper.deleteFolder(senderFolder);
        TestHelper.deleteFolder(receiverFolder);
    }


    @Test void sendTwoFilesTest() {
        var senderFolder = TestHelper.createTestFolder("senderFol");
        var receiverFolder = TestHelper.createTestFolder("receiverFol");

        var senderTestFile = TestHelper.createFileAndFillWithContent(senderFolder, "testFiiile", "this is just a test");
        var senderTestFile2 = TestHelper.createFileAndFillWithContent(senderFolder, "testFi22iile", "t222his is just a test");
        assertNotNull(senderTestFile);
        assertNotNull(senderTestFile2);

        var server = new Server(testPort);
        server.onConnected = s -> {
            var endpoint = new Endpoint(s);
            endpoint.sendFile(senderTestFile);
            endpoint.sendFile(senderTestFile2);
        };
        server.start();

        var client = new Client(testPort);
        client.onConnected = s -> {
            var endpoint = new Endpoint(s);
            var receivedFile = new File(receiverFolder, senderTestFile.getName());
            assertTrue(endpoint.receiveFile(receivedFile, receivedFile.length()));

            var receivedFile2 = new File(receiverFolder, senderTestFile2.getName());
            assertTrue(endpoint.receiveFile(receivedFile2, receivedFile2.length()));


            assertEquals(TestHelper.readFromFile(senderTestFile), TestHelper.readFromFile(receivedFile));
            assertEquals(TestHelper.readFromFile(senderTestFile2), TestHelper.readFromFile(receivedFile2));
        };
        client.connect("localhost");

        TestHelper.deleteFolder(senderFolder);
        TestHelper.deleteFolder(receiverFolder);
    }
}