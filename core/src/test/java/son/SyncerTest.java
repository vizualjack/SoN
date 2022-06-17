package son;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SyncerTest {
    @Test void sendListFilled() {
       List<MetaFile> thisDeviceFiles = new ArrayList<>() {{
           add(new MetaFile("fiiile1"));
           add(new MetaFile("fiiile22"));
       }};        
       List<MetaFile> otherDeviceFiles = new ArrayList<>();

       var syncer = new Syncer(thisDeviceFiles, otherDeviceFiles);
       assertEquals(thisDeviceFiles, syncer.sendFiles);
       assertEquals(otherDeviceFiles, syncer.receiveFiles);
    }

    @Test void receiveListFilled() {
        List<MetaFile> thisDeviceFiles = new ArrayList<>();
        List<MetaFile> otherDeviceFiles = new ArrayList<>() {{
            add(new MetaFile("fiiile1"));
            add(new MetaFile("fiiile22"));
        }};
 
        var syncer = new Syncer(thisDeviceFiles, otherDeviceFiles);
        assertEquals(thisDeviceFiles, syncer.sendFiles);
        assertEquals(otherDeviceFiles, syncer.receiveFiles);
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
 
        var syncer = new Syncer(thisDeviceFiles, otherDeviceFiles);
        assertEquals(emptyList, syncer.sendFiles);
        assertEquals(emptyList, syncer.receiveFiles);
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
 
        var syncer = new Syncer(thisDeviceFiles, otherDeviceFiles);
        assertEquals(expectedSendFiles, syncer.sendFiles);
        assertEquals(expectedReceiveFiles, syncer.receiveFiles);
     }
}
