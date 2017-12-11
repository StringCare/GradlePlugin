package com.efraespada.stringobfuscatorplugin;

public interface GradleHandlerCallback {

    void onDataFound(String module, String variant);

    void onMergeResourcesStarts(String module, String variant);

    void onMergeResourcesFinish(String module, String variant);

}
