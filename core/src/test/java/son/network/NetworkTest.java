package son.network;

import java.net.Socket;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class NetworkTest {

    boolean serverConnected = false,
            clientConnected = false;

    @Test void test() {
        var server = new Server(1234);
        server.onConnected = socket -> {serverConnected = true; return true;};

        var client = new Client(1234);
        client.onConnected = socket -> {clientConnected = true; return true;};
    }
}
