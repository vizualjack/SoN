package son;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SyncEstablisherTest {
    @Test void test() {
        var waiter = new SyncEstablisher();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            assertTrue(false); // should not be here
        }
        var wannaSync = new SyncEstablisher();
        waiter.stop();

        assertNull(wannaSync.lastMsg);
        assertNotNull(waiter.lastMsg);
    }
}
