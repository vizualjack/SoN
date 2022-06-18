package son.compare;

import java.util.ArrayList;
import java.util.List;

import son.MetaFile;

public class Comparer {
    public static CompareResult compareFiles(List<MetaFile> thisDeviceFiles, List<MetaFile> otherDeviceFiles) {
        List<MetaFile> sendFiles = new ArrayList<>();
        List<MetaFile> receiveFiles = new ArrayList<>();

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

        return new CompareResult(sendFiles, receiveFiles);
    }
}
