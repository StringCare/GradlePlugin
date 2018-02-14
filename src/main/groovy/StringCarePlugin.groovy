
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class StringCare implements Plugin<Project> {

    private static final float VERSION = 0.3;
    private Project project;
    private String key;
    private static boolean debug;
    private static Map<String, Config> moduleMap = new HashMap<>();

    def extension = null

    Logger logger

    @Override
    void apply(Project project) {
        System.loadLibrary("malacaton-lib")

        this.project = project
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
            debug = extension.debug != null && extension.debug ? extension.debug : false
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
        this.project.gradle.addBuildListener(new TListener(this, new GradleHandlerCallback() {

            @Override
            boolean debug() {
                return debug
            }

            @Override
            void onDataFound(String module, String variant) {
                // nothing to do here
            }

            @Override
            void onMergeResourcesStarts(String module, String variant) {
                key = CredentialUtils.getKey(module, variant, debug);
                if (!"none".equals(key) && key != null) {
                    if (moduleMap.containsKey(module)) {
                        PrintUtils.print(module, variant + ":" + key)
                        PrintUtils.print(module, "backupStringResources")
                        FileUtils.backupStringResources(module, moduleMap.get(module), debug)
                        PrintUtils.print(module, "encryptStringResources")

                        FileUtils.encryptStringResources(module, moduleMap.get(module), key, debug)
                    } else {
                        Config config = new Config();
                        List<String> stg = new ArrayList<>();
                        stg.add("strings.xml")
                        List<String> src = new ArrayList<>();
                        src.add("src/main")
                        config.setStringFiles(stg)
                        config.setSrcFolders(src)

                        PrintUtils.print(module, variant + ":" + key)
                        PrintUtils.print(module, "backupStringResources")
                        FileUtils.backupStringResources(module, config, debug)
                        PrintUtils.print(module, "encryptStringResources")
                        FileUtils.encryptStringResources(module, config, key, debug)
                    }
                }

            }

            @Override
            void onMergeResourcesFinish(String module, String variant) {
                if (!"none".equals(key)&& key != null) {
                    if (moduleMap.containsKey(module)) {
                        PrintUtils.print(module, "restoreStringResources")
                        FileUtils.restoreStringResources(module, moduleMap.get(module), debug)
                    } else {
                        Config config = new Config();
                        List<String> stg = new ArrayList<>();
                        stg.add("strings.xml")
                        List<String> src = new ArrayList<>();
                        src.add("src/main")
                        config.setStringFiles(stg)
                        config.setSrcFolders(src)

                        PrintUtils.print(module, "restoreStringResources")
                        FileUtils.restoreStringResources(module, config, debug)
                    }
                }
            }
        }))
    }

    private void createExtensions() {
        extension = project.extensions.create('stringcare', Extension )
        project.stringcare.extensions.modules = project.container(Conf)
    }
}

class Extension {

    boolean debug

    Extension() {

    }

}

class Conf {

    final String name
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
