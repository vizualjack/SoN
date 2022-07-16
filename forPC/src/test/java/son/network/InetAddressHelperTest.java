package son.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

public class InetAddressHelperTest {
    @Test
    public void test() {
        String address = "192.168.2.255";
        byte[] addr = null;
        try {
            addr = InetAddress.getByName(address).getAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(address.contentEquals(InetAddressHelper.toString(addr)));
    }
}
