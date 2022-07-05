package son.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

public class ClientTest {
    @Test void clientTest() throws UnknownHostException {
        var c = new Client(123);
        c.connect(InetAddress.getByName("192.168.2.134").getAddress()); 
    }
}
