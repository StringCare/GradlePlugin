import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class StringCare implements Plugin<Project> {

    private Project project
    private String key
    private static String main_module
    private static boolean debug
    public static String WRAPPER = "gradlew"
    private static Map<String, Config> moduleMap = new HashMap<>()

    def extension = null

    Logger logger

    @Override
    void apply(Project project) {
        this.project = project
        createExtensions()

        FileUtils.defineProjectPath(this.project.file(WRAPPER).absolutePath.replace(WRAPPER, ""))

        this.logger = this.project.logger
        this.project.afterEvaluate { setupConfig() }
        this.project.gradle.addBuildListener(new BuilderListener(this, new GradleHandlerCallback() {

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
                backupAndObfuscate(module, variant)
            }

            @Override
            void onMergeResourcesFinish(String module, String variant) {
                restoreResources(module)
            }
        }))
    }

    private void createExtensions() {
        extension = project.extensions.create('stringcare', Extension)
        project.stringcare.extensions.modules = project.container(Conf)
    }

    private void setupConfig() {
        debug = extension.debug != null && extension.debug ? extension.debug : false
        main_module = extension.main_module != null ? extension.main_module : "app"
        this.project.stringcare.modules.all { mod ->
            Config config = new Config()
            if (mod.stringFiles != null && mod.srcFolders != null) {
                config.setStringFiles(mod.stringFiles)
                config.setSrcFolders(mod.srcFolders)
                moduleMap.put(mod.name, config)
            } else if (mod.srcFolders != null) {
                List<String> stg = new ArrayList<>()
                stg.add("strings.xml")
                config.setStringFiles(stg)
                config.setSrcFolders(mod.srcFolders)
                moduleMap.put(mod.name, config)
            } else if (mod.stringFiles != null) {
                List<String> src = new ArrayList<>();
                src.add("src" + File.separator + "main")
                config.setStringFiles(mod.stringFiles)
                config.setSrcFolders(src)
                moduleMap.put(mod.name, config)
            }
        }
    }

    private void backupAndObfuscate(String module, String variant) {
        key = CredentialUtils.getKey(module, variant, debug);
        if ("none" != key && key != null) {
            if (moduleMap.containsKey(module)) {
                PrintUtils.print(module, variant + ":" + key)
                PrintUtils.print(module, "backupStringResources")
                FileUtils.backupStringResources(module, moduleMap.get(module), debug)
                PrintUtils.print(module, "encryptStringResources")

                try {
                    FileUtils.encryptStringResources(main_module, module, moduleMap.get(module), key, debug)
                } catch (Exception e) {
                    e.printStackTrace()
                }
            } else {
                Config config = new Config();
                List<String> stg = new ArrayList<>();
                stg.add("strings.xml")
                List<String> src = new ArrayList<>();
                src.add("src" + File.separator + "main")
                config.setStringFiles(stg)
                config.setSrcFolders(src)

                PrintUtils.print(module, variant + ":" + key)
                PrintUtils.print(module, "backupStringResources")
                FileUtils.backupStringResources(module, config, debug)
                PrintUtils.print(module, "encryptStringResources")
                try {
                    FileUtils.encryptStringResources(main_module, module, config, key, debug)
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
    }

    private void restoreResources(String module) {
        if (!"none".equals(key) && key != null) {
            if (moduleMap.containsKey(module)) {
                PrintUtils.print(module, "restoreStringResources")
                FileUtils.restoreStringResources(module, moduleMap.get(module), debug)
            } else {
                Config config = new Config()
                List<String> stg = new ArrayList<>()
                stg.add("strings.xml")
                List<String> src = new ArrayList<>()
                src.add("src" + File.separator + "main")
                config.setStringFiles(stg)
                config.setSrcFolders(src)

                PrintUtils.print(module, "restoreStringResources")
                FileUtils.restoreStringResources(module, config, debug)
            }
        }
    }
}

class Extension {
    boolean debug
    String main_module
    Extension() {
        // nothing to do here
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
