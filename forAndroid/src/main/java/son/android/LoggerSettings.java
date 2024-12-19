package son.android;

import android.content.Context;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.helpers.NOPAppender;

public class LoggerSettings {
    private static final String LOG_PATTERN_LOGCAT = "%msg%n";
    private static final Level LOG_LEVEL = BuildConfig.DEBUG ? Level.DEBUG : Level.INFO;
    private static final String LOG_PATTERN_FILE = "%d{yyyy-MM-dd HH:mm:ss} [%level] %logger{36} - %msg%n";
    private static final String LOG_FILE_NAME = "logs.txt";
    private static final String LOG_FILE_APPENDER_NAME = "FILE";

    public static void apply() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        LogcatAppender appender = (LogcatAppender) rootLogger.getAppender("LOGCAT");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        // actual custom settings
        encoder.setPattern(LOG_PATTERN_LOGCAT);
        rootLogger.setLevel(LOG_LEVEL);
        //
        encoder.start();
        appender.setEncoder(encoder);
    }

    public static void activateFileLogging(Context context) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if(rootLogger.getAppender(LOG_FILE_APPENDER_NAME) != null) return;
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(LOG_PATTERN_FILE);
        encoder.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName(LOG_FILE_APPENDER_NAME);
        File logFile = getLogFile(context);
        try {
            logFile.createNewFile();
        } catch (IOException ex) {
            return;
        }
        fileAppender.setFile(logFile.getAbsolutePath());
        fileAppender.setEncoder(encoder);
        fileAppender.setAppend(true);
        fileAppender.start();
        rootLogger.addAppender(fileAppender);
    }

    public static void deactivateFileLogging(Context context) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAppender(LOG_FILE_APPENDER_NAME);
        getLogFile(context).delete();
    }

    public static File getLogFile(Context context) {
        return new File(context.getExternalFilesDir(""), LOG_FILE_NAME);
    }
}
