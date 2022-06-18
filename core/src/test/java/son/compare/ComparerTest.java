package son.compare;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import son.MetaFile;

public class ComparerTest {
    @Test void sendListFilled() {
       List<MetaFile> thisDeviceFiles = new ArrayList<>() {{
           add(new MetaFile("fiiile1"));
           add(new MetaFile("fiiile22"));
       }};        
       List<MetaFile> otherDeviceFiles = new ArrayList<>();

       var compareResult = Comparer.compareFiles(thisDeviceFiles, otherDeviceFiles);
       assertEquals(thisDeviceFiles, compareResult.sendFiles);
       assertEquals(otherDeviceFiles, compareResult.receiveFiles);
    }

    @Test void receiveListFilled() {
        List<MetaFile> thisDeviceFiles = new ArrayList<>();
        List<MetaFile> otherDeviceFiles = new ArrayList<>() {{
            add(new MetaFile("fiiile1"));
            add(new MetaFile("fiiile22"));
        }};
 
        var compareResult = Comparer.compareFiles(thisDeviceFiles, otherDeviceFiles);
        assertEquals(thisDeviceFiles, compareResult.sendFiles);
        assertEquals(otherDeviceFiles, compareResult.receiveFiles);
     }

     @Test void sendAndReceiveListNotFilled() {
        List<MetaFile> thisDeviceFiles = new ArrayList<>() {{
            add(new MetaFile("fiiile1"));
            add(new MetaFile("fiiile22"));
        }};    

        List<MetaFile> otherDeviceFiles = new ArrayList<>() {{
            add(new MetaFile("fiiile1"));
            add(new MetaFile("fiiile22"));
        }};

        List<MetaFile> emptyList = new ArrayList<>();
 
        var compareResult = Comparer.compareFiles(thisDeviceFiles, otherDeviceFiles);
        assertEquals(emptyList, compareResult.sendFiles);
        assertEquals(emptyList, compareResult.receiveFiles);
     }

     @Test void sendAndReceiveListFilled() {
        List<MetaFile> thisDeviceFiles = new ArrayList<>() {{
            add(new MetaFile("fiiile1"));
            add(new MetaFile("fiiile22"));
            add(new MetaFile("letsGooo"));
        }};

        List<MetaFile> otherDeviceFiles = new ArrayList<>() {{
            add(new MetaFile("moooin"));
            add(new MetaFile("fiiile1"));
            add(new MetaFile("fiiile22"));
        }};

        List<MetaFile> expectedSendFiles = new ArrayList<>() {{
            add(thisDeviceFiles.get(2));
        }};

        List<MetaFile> expectedReceiveFiles = new ArrayList<>() {{
            add(otherDeviceFiles.get(0));
        }};
 
        var compareResult = Comparer.compareFiles(thisDeviceFiles, otherDeviceFiles);
        assertEquals(expectedSendFiles, compareResult.sendFiles);
        assertEquals(expectedReceiveFiles, compareResult.receiveFiles);
     }
}
