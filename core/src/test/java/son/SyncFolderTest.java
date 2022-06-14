package son;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class SyncFolderTest {
    @Test void findFileInSyncFolder() {
        var testFileName = "teeeestFiile";

        var testFolder = createTestFolder();
        createFile(testFolder, testFileName);

        var syncer = new SyncFolder(testFolder);
        var files = syncer.getFiles();
        System.out.println(testFolder.delete());
        deleteFolder(testFolder);

        assertEquals(testFileName, files.get(0));
    }

    @Test void findAllFilesInSyncFolder() {
        var testFiles = Arrays.asList("teeeestFiile", "asdasdww");

        var testFolder = createTestFolder();
        createFiles(testFolder, testFiles);

        var syncer = new SyncFolder(testFolder);
        var files = syncer.getFiles();
        deleteFolder(testFolder); 

        assertTrue(listContentsAreEquals(testFiles, files));
    }

    @Test void findFolderInSyncFolder() {
        var testFolderName = "testFooooolder";

        var testFolder = createTestFolder();
        createFolder(testFolder, testFolderName);

        var syncer = new SyncFolder(testFolder);
        var folders = syncer.getFolders();
        deleteFolder(testFolder);

        assertEquals(testFolderName, folders.get(0));
    }

    @Test void findAllFoldersInSyncFolder() {
        var testFolderNames = Arrays.asList("testFooooolder", "dasdwggrgr");

        var testFolder = createTestFolder();
        createFolders(testFolder, testFolderNames);

        var syncer = new SyncFolder(testFolder);
        var folders = syncer.getFolders();
        deleteFolder(testFolder);

        assertTrue(listContentsAreEquals(testFolderNames, folders));
    }

    @Test void findAllFoldersWithInnerFolderInSyncFolder() {
        var testFolderNames = Arrays.asList("testFooooolder", "testFooooolder\\innerFooolder", "dasdwggrgr", "dasdwggrgr\\innerFooolder");

        var testFolder = createTestFolder();
        createFolders(testFolder, testFolderNames);

        var syncer = new SyncFolder(testFolder);
        var folders = syncer.getFolders();
        deleteFolder(testFolder);

        assertTrue(listContentsAreEquals(testFolderNames, folders));
    }

    @Test void findAllInnerFilesInSyncFolder() {
        var testFolderName = "testFooooolder";
        var testInnerFiles = Arrays.asList(testFolderName + "\\asdasd", testFolderName + "\\adasd");

        var testFolder = createTestFolder();
        createFolder(testFolder, testFolderName);
        createFiles(testFolder, testInnerFiles);

        var syncer = new SyncFolder(testFolder);
        var files = syncer.getFiles();
        deleteFolder(testFolder);

        assertTrue(listContentsAreEquals(testInnerFiles, files));
    }

    

    File createTestFolder() {
        var testFolder = new File("testFolder");
        testFolder.mkdir();
        return testFolder;
    }

    private void deleteFolder(File folder) {
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isDirectory()) deleteFolder(fileInFolder);
            else fileInFolder.delete();
        }
        folder.delete();
    }

    void createFile(File folder, String fileName) {
        try {
            new File(folder, fileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void createFiles(File folder, List<String> fileNames) {
        for (String fileName : fileNames) {
            createFile(folder, fileName);
        }
    }

    void createFolder(File folder, String folderName) {
        new File(folder, folderName).mkdir();
    }

    void createFolders(File folder, List<String> folderNames) {
        for (String folderName : folderNames) {
            createFolder(folder, folderName);
        }
    }

    private boolean listContentsAreEquals(List<String> listOne, List<String> listTwo) {
        if(listOne.size() != listTwo.size()) return false;
        for (String one : listOne) {
            boolean foundInListTwo = false;
            for (String two : listTwo) {
                if(one.equals(two)) {
                    foundInListTwo = true;
                    break;
                }
            }
            if(!foundInListTwo) return false;
        }
        return true;
    }
}
