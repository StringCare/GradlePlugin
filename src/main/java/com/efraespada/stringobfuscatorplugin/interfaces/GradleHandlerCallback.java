package com.efraespada.stringobfuscatorplugin.interfaces;

public interface GradleHandlerCallback {

    void onDataFound(String module, String variant);

    void onMergeResources(String module, String variant);

}
