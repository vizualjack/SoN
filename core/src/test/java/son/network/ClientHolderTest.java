package son.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

public class ClientHolderTest {
    @Test void test() throws InterruptedException, UnknownHostException {
        var ch1 = new ClientHolder(1337);
        // var ch2 = new ClientHolder(1337);
        var myAddress = InetAddress.getLocalHost().getHostAddress();
        ch1.start();
        assertNull(ch1.foundClient);
        Thread.sleep(100);
        assertNotNull(ch1.foundClient);
        assertEquals(myAddress, ch1.getClient());
    }    
}
