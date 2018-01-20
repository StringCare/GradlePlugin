package com.stringcare;

import java.util.List;

public class Config {

    List<String> stringFiles;
    List<String> srcFolders;

    Config() {
        // nothing to do here
    }

    List<String> getStringFiles() {
        return stringFiles;
    }

    void setStringFiles(List<String> stringFiles) {
        this.stringFiles = stringFiles;
    }

    List<String> getSrcFolders() {
        return srcFolders;
    }

    void setSrcFolders(List<String> srcFolders) {
        this.srcFolders = srcFolders;
    }

}
