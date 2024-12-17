package son.android;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

public class LoggerSettings {
    private static final String LOG_PATTERN = "%msg%n";
    private static final Level LOG_LEVEL = BuildConfig.DEBUG ? Level.DEBUG : Level.INFO;

    public static void apply() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        LogcatAppender appender = (LogcatAppender) rootLogger.getAppender("LOGCAT");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        // actual custom settings
        encoder.setPattern(LOG_PATTERN);
        rootLogger.setLevel(LOG_LEVEL);
        //
        encoder.start();
        appender.setEncoder(encoder);
    }
}
