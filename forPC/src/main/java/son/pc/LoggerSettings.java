package son.pc;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;

public class LoggerSettings {
    private static final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%level] %logger{36} - %msg%n";

    private static Level logLevel = Level.INFO;

    public static void setLogLevel(Level newLogLevel) {
        logLevel = newLogLevel;
    }

    public static void apply() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        ConsoleAppender appender = (ConsoleAppender) rootLogger.getAppender("console");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        // actual custom settings
        encoder.setPattern(LOG_PATTERN);
        rootLogger.setLevel(logLevel);
        // 
        encoder.start();
        appender.setEncoder(encoder);
    }
}
