package son.network;

import org.junit.jupiter.api.Test;

public class ClientTest {
    @Test void clientTest() {
        var c = new Client(123);
        c.connect("192.168.2.134"); 
    }
}
