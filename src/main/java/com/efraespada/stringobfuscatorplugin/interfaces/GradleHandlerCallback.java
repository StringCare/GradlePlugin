package com.efraespada.stringobfuscatorplugin.interfaces;

public interface GradleHandlerCallback {

    void onDataFound(String module, String variant);

    void onMergeResourcesStarts(String module, String variant);

    void onMergeResourcesFinish(String module, String variant);

}
