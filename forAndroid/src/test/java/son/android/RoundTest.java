package son.android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.Date;

public class RoundTest {
    @Test
    public void test() {
        try {
            double roundedNow1 = roundedNow();
            Thread.sleep(30000);
            double roundedNow2 = roundedNow();
            assertEquals(roundedNow1, roundedNow2, 0);
        } catch (Exception ex) {}
    }

    private double roundedNow() {
        long now = new Date().getTime();
        long secs = now/1000;
        return Math.floor(secs/60);
    }
}
