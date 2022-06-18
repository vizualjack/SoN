package son.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
}
