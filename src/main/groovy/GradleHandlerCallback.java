
public interface GradleHandlerCallback {

    boolean debug();

    void onDataFound(String module, String variant);

    void onMergeResourcesStarts(String module, String variant);

    void onMergeResourcesFinish(String module, String variant);

}
