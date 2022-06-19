package son;

import org.junit.jupiter.api.Test;

import son.util.TestHelper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
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

        assertEquals(testFileName, files.get(0).getName());
    }

    @Test void findAllFilesInSyncFolder() {
        var testFileNames = Arrays.asList("teeeestFiile", "asdasdww");

        var testFolder = createTestFolder();
        var testFiles = TestHelper.createFiles(testFolder, testFileNames);

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

        assertEquals(testFolderName, folders.get(0).getName());
    }

    @Test void findAllFoldersInSyncFolder() {
        var testFolderNames = Arrays.asList("testFooooolder", "dasdwggrgr");

        var testFolder = createTestFolder();
        var createdFolders = TestHelper.createFolders(testFolder, testFolderNames);

        var syncer = new SyncFolder(testFolder);
        var folders = syncer.getFolders();
        deleteFolder(testFolder);

        assertTrue(listContentsAreEquals(createdFolders, folders));
    }

    @Test void findAllFoldersWithInnerFolderInSyncFolder() {
        var testFolderNames = Arrays.asList("testFooooolder", "testFooooolder\\innerFooolder", "dasdwggrgr", "dasdwggrgr\\innerFooolder");

        var testFolder = createTestFolder();
        var createdFolders = TestHelper.createFolders(testFolder, testFolderNames);

        var syncer = new SyncFolder(testFolder);
        var folders = syncer.getFolders();
        deleteFolder(testFolder);

        assertTrue(listContentsAreEquals(createdFolders, folders));
    }

    @Test void findAllInnerFilesInSyncFolder() {
        var testFolderName = "testFooooolder";
        var testInnerFiles = Arrays.asList(testFolderName + "\\asdasd", testFolderName + "\\adasd");

        var testFolder = createTestFolder();
        createFolder(testFolder, testFolderName);
        var createdFiles = TestHelper.createFiles(testFolder, testInnerFiles);

        var syncer = new SyncFolder(testFolder);
        var files = syncer.getFiles();
        deleteFolder(testFolder);

        assertTrue(listContentsAreEquals(createdFiles, files));
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

    private boolean listContentsAreEquals(List<File> listOne, List<File> listTwo) {
        if(listOne.size() != listTwo.size()) return false;
        for (File one : listOne) {
            boolean foundInListTwo = false;
            for (File two : listTwo) {
                if(one.getName().equals(two.getName())) {
                    foundInListTwo = true;
                    break;
                }
            }
            if(!foundInListTwo) return false;
        }
        return true;
    }
}
