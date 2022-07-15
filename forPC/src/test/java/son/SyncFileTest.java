package son;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import son.pc.SyncFilePC;

public class SyncFileTest {
    @Test
    public void test() {
        File testFile1 = createTestFile("teeest1");
        SyncFile syncFile1 = new SyncFilePC(null, testFile1);
        assertNotNull(testFile1);
        File testFile2 = createTestFile("teeest22");
        SyncFile syncFile2 = new SyncFilePC(null, testFile2);
        assertNotNull(testFile2);
        String checksum1 = syncFile1.getChecksum();
        String checksum2 = syncFile2.getChecksum();
        testFile1.delete();
        testFile2.delete();
        assertEquals(checksum1, checksum2);
    }

    private File createTestFile(String name) {
        try {
            File testFile = new File(name);
            testFile.createNewFile();
            var out = new FileOutputStream(testFile);
            out.write("This is just a test teeext".getBytes());
            out.close();
            return testFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

