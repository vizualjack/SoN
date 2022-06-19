package son;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import son.util.TestHelper;

public class SyncerTest {
    @Test void easySyncTest() {
        var testFileName = "testFiiillee";
        var testFileContent = "asdasdwoad aw dao wdpawdkosd ";

        var senderFolder = TestHelper.createTestFolder("senderFol");
        var senderTestFile = TestHelper.createFileAndFillWithContent(senderFolder, testFileName, testFileContent);
        assertNotNull(senderTestFile);
        var receiverFolder = TestHelper.createTestFolder("receiverFol");
        /*var syncerWithFiles = */new Syncer(senderFolder);
        var syncerWithNoFiles = new Syncer(receiverFolder);
        syncerWithNoFiles.sync();

        List<File> files = syncerWithNoFiles.syncFolder.getFiles();
        assertEquals(1, files.size());
        var transferedFile = files.get(0);

        assertEquals(testFileName, transferedFile.getName());
        assertEquals(testFileContent, TestHelper.readFromFile(transferedFile));
        
        TestHelper.deleteFolder(senderFolder);
        TestHelper.deleteFolder(receiverFolder);
    }

    @Test void multipleFilesSyncTest() {
        var testFileName = "testFiiillee";
        var testFileContent = "asdasdwoad aw dao wdpawdkosd ";

        var senderFolder = TestHelper.createTestFolder("senderFol");
        var senderTestFile = TestHelper.createFileAndFillWithContent(senderFolder, testFileName, testFileContent);
        assertNotNull(senderTestFile);
        var receiverFolder = TestHelper.createTestFolder("receiverFol");
        /*var syncerWithFiles = */new Syncer(senderFolder);
        var syncerWithNoFiles = new Syncer(receiverFolder);
        syncerWithNoFiles.sync();

        assertTrue(compareSyncFolders(senderFolder, receiverFolder));

        TestHelper.deleteFolder(senderFolder);
        TestHelper.deleteFolder(receiverFolder);
    }

    boolean compareSyncFolders(File folder1, File folder2) {
        return compareSyncFolders(new SyncFolder(folder1), new SyncFolder(folder2));
    }

    boolean compareSyncFolders(SyncFolder syncFolder1, SyncFolder syncFolder2) {
        var syncFolder1Files = syncFolder1.getFiles();
        var syncFolder2Files = syncFolder2.getFiles();
        if(syncFolder1Files.size() != syncFolder2Files.size()) return false;

        for (File file1 : syncFolder1Files) {
            boolean found = false;
            for (File file2 : syncFolder2Files) {
                if(file1.getName() != file2.getName()) continue;
                found = true;
                var file1Content = TestHelper.readFromFile(file1);
                var file2Content = TestHelper.readFromFile(file2);
                if(!file1Content.equals(file2Content)) return false;
            }
            if(!found) return false;
        }
        return true;
    }
}
