package son.android;

import org.junit.Test;
import org.slf4j.LoggerFactory;

public class LoggingTest {
    @Test
    public void asdf() {
        System.out.println("SLF4J Provider: " + LoggerFactory.getILoggerFactory().getClass().getName());
    }
}