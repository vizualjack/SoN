package son.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class TestHelper {

    public static String readFromFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            var fileReader = new FileReader(file);
            var buffer = new char[16*1024];
            int count;
            while((count = fileReader.read(buffer)) > 0) {
                sb.append(buffer, 0, count);
            }
            fileReader.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    public static File createTestFolder(String folderName) {
        var testFolder = new File(folderName);
        testFolder.mkdir();
        return testFolder;
    }

    public static boolean deleteFolder(File folder) {
        for (var fileInFolder : folder.listFiles()) {
            if(fileInFolder.isDirectory()) deleteFolder(fileInFolder);
            else fileInFolder.delete();
        }
        return folder.delete();
    }

    public static File createFileAndFillWithContent(File folder, String fileName, String content) {
        File file = createFile(folder, fileName);
        if(file != null) {
            if(!writeInFile(file, content)) return null;
        }
        return file;
    }

    public static File createFile(File folder, String fileName) {
        File file = null;
        try {
            file = new File(folder, fileName);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static boolean writeInFile(File file, String content) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
            return true;
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static List<File> createFiles(File folder, List<String> fileNames) {
        List<File> files = new ArrayList<>();
        for (String fileName : fileNames) {
            File createdFile = createFile(folder, fileName);
            if(createdFile == null) return null;
            files.add(createdFile);
        }
        return files;
    }

    public static File createFolder(File folder, String folderName) {
        var createdFolder = new File(folder, folderName);
        if(!createdFolder.mkdir()) return null;
        return createdFolder;
    }

    public static List<File> createFolders(File folder, List<String> folderNames) {
        List<File> folders = new ArrayList<>();
        for (String folderName : folderNames) {
            var createdFolder = createFolder(folder, folderName);
            if(createdFolder == null) return null;
            folders.add(createdFolder);
        }
        return folders;
    }

    public static void createRandomFilesWithContentInFolder(File folder) {
        int numOfFiles = new Random(new GregorianCalendar().getTimeInMillis()).nextInt(10-1) + 1;
        for(int i = 0; i < numOfFiles; i++) {
            createFileAndFillWithContent(folder, randomString(10), randomString(100));
        }
    }

    private static String randomString(int maxLength) {
        Random random = new Random(new GregorianCalendar().getTimeInMillis());
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(maxLength-1) + 1;
        for(int i = 0; i < length; i++) {
            sb.append(random.nextInt(255));
        }
        return sb.toString();
    }
}
