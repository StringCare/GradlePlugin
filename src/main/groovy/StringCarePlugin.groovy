
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class StringCare implements Plugin<Project> {

    private static final float VERSION = 0.3;
    private Project project;
    private static String key = null;
    private static Map<String, Config> moduleMap = new HashMap<>();

    Logger logger

    @Override
    void apply(Project project) {
        this.project = project;

        createExtensions()

        this.project.task('stop') {
            doLast {
                String cmd = "";
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    cmd = "gradlew.bat";
                } else {
                    cmd = "gradlew";
                    Runtime.getRuntime().exec("chmod +x ./" + cmd);
                }

                Runtime.getRuntime().exec("./" + cmd + " --stop");
                Runtime.getRuntime().exec("./" + cmd + " clean");
            }
        }

        this.logger = this.project.logger
        this.project.afterEvaluate {
            this.project.stringcare.modules.all { mod ->
                Config config = new Config()
                if (mod.stringFiles != null && mod.srcFolders != null) {
                    config.setStringFiles(mod.stringFiles)
                    config.setSrcFolders(mod.srcFolders)
                    moduleMap.put(mod.name, config)
                } else if (mod.srcFolders != null) {
                    List<String> stg = new ArrayList<>();
                    stg.add("strings.xml")
                    config.setStringFiles(stg)
                    config.setSrcFolders(mod.srcFolders)
                    moduleMap.put(mod.name, config)
                } else if (mod.stringFiles != null) {
                    List<String> src = new ArrayList<>();
                    src.add("src/main")
                    config.setStringFiles(mod.stringFiles)
                    config.setSrcFolders(src)
                    moduleMap.put(mod.name, config)
                }
            }
        }
        this.project.gradle.addBuildListener(new TimingRecorder(this, new GradleHandlerCallback() {
            @Override
            void onDataFound(String module, String variant) {
                PrintUtils.init(module, variant)
                CredentialUtils.init(module, variant, true)
                key = CredentialUtils.getKey()
                if (moduleMap.containsKey(module)) {
                    FileUtils.init(key, module, variant, moduleMap.get(module))
                } else {
                    Config config = new Config();
                    List<String> stg = new ArrayList<>();
                    stg.add("strings.xml")
                    List<String> src = new ArrayList<>();
                    src.add("src/main")
                    config.setStringFiles(stg)
                    config.setSrcFolders(src)
                    FileUtils.init(key, module, variant, config)
                }
            }

            @Override
            void onMergeResourcesStarts(String module, String variant) {
                PrintUtils.print(variant + ":" + key)
                PrintUtils.print("backupStringResources")
                FileUtils.backupStringResources()
                PrintUtils.print("encryptStringResources")
                FileUtils.encryptStringResources()
            }

            @Override
            void onMergeResourcesFinish(String module, String variant) {
                PrintUtils.print("restoreStringResources")
                FileUtils.restoreStringResources()
            }
        }))
    }

    private void createExtensions() {
        project.extensions.create('stringcare', Extension )
        project.stringcare.extensions.modules = project.container(Conf)
    }
}

class Extension {

    Extension() {

    }

}

class Conf {

    final String name
    String lala
    List<String> stringFiles
    List<String> srcFolders

    Conf(String name) {
        this.name = name
    }

    @Override
    String toString() {
        return name
    }

}
