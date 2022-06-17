package son.compare;

import java.util.List;

import son.MetaFile;

public class CompareResult {
    public List<MetaFile> sendFiles, receiveFiles;

    public CompareResult(List<MetaFile> sendFiles, List<MetaFile> receiveFiles) {
        this.sendFiles = sendFiles;
        this.receiveFiles = receiveFiles;
    }
}
