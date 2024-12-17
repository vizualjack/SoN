package son.pc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import son.Syncer;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        if (debugArgExists(args)) LoggerSettings.setLogLevel(Level.DEBUG);
        LoggerSettings.apply();
        File f = null;
        if(args.length >= 1) {
            f = new File(getDirectoryArg(args));
            logger.info("Start with folder syncFolder: {}", f.getPath());
        }
        else {
            logger.error("No path set. Usage: java -jar path\\to\\son.jar your\\directory");
            return;
        }
        new Syncer(new SyncFolderPC(f)).syncLoop();
    }

    private static boolean debugArgExists(String[] args) {
        return Arrays.stream(args).anyMatch(s -> s.equals("-d") || s.equals("--debug"));
    }

    private static String getDirectoryArg(String[] args) {
        return Arrays.stream(args)
               .filter(s -> !s.equals("-d") && !s.equals("--debug"))
               .findFirst()
               .orElse(null);
    }
}