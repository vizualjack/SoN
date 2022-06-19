package son.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    public static boolean createFiles(File folder, List<String> fileNames) {
        for (String fileName : fileNames) {
            if(createFile(folder, fileName) == null) 
                return false;
        }
        return true;
    }

    public static boolean createFolder(File folder, String folderName) {
        return new File(folder, folderName).mkdir();
    }

    public static boolean createFolders(File folder, List<String> folderNames) {
        for (String folderName : folderNames) {
            if(!createFolder(folder, folderName))
                return false;
        }
        return true;
    }
}
