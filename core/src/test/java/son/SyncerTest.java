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
}
