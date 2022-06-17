package son;

import java.util.ArrayList;
import java.util.List;

public class Syncer {
    public List<MetaFile> sendFiles = new ArrayList<>();
    public List<MetaFile> receiveFiles = new ArrayList<>();

    public Syncer(List<MetaFile> thisDeviceFiles, List<MetaFile> otherDeviceFiles) {
        compare(thisDeviceFiles, otherDeviceFiles);
    }

    private void compare(List<MetaFile> thisDeviceFiles, List<MetaFile> otherDeviceFiles) {
        for (var thisDeviceFile : thisDeviceFiles) {
            var found = false;
            for (var otherDeviceFile : otherDeviceFiles) {
                if(thisDeviceFile.path.equals(otherDeviceFile.path)) {
                    found = true;
                    break;
                }
            }
            if(!found) sendFiles.add(thisDeviceFile);
        }

        for (var otherDeviceFile : otherDeviceFiles) {
            var found = false;
            for (var thisDeviceFile : thisDeviceFiles) {
                if(otherDeviceFile.path.equals(thisDeviceFile.path)) {
                    found = true;
                    break;
                }
            }
            if(!found) receiveFiles.add(otherDeviceFile);
        }
    }
}
