package com.efraespada.stringobfuscatorplugin

import com.efraespada.stringobfuscatorplugin.interfaces.GradleHandlerCallback
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class StringObfuscatorPlugin implements Plugin<Project> {

    private static final float VERSION = 0.3;
    private Project project;
    private static String key = null;

    Logger logger

    NamedDomainObjectCollection<ReporterExtension> reporterExtensions

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
        this.project.stringobfuscator.modules.all{ mod ->
            PrintUtils.print(mod.name)
            PrintUtils.print(mod.accessKey.size() + "")
        }
        this.project.gradle.addBuildListener(new TimingRecorder(this, new GradleHandlerCallback() {
            @Override
            void onDataFound(String module, String variant) {
                PrintUtils.init(module, variant)
                CredentialUtils.init(module, variant, true)
                key = CredentialUtils.getKey()
                FileUtils.init(key, module, variant)
            }

            @Override
            void onMergeResourcesStarts(String module, String variant) {
                PrintUtils.print(variant + ":" + key)
                // PrintUtils.print("size: " + stringFiles.length)
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
        project.extensions.add('stringobfuscator', StringObfuscatorExtension )
        project.stringobfuscator.extensions.modules = project.container(StringObfuscatorConf)
        project.stringobfuscator.modules.all {
            accessKey = ['dev']
        }
    }
}

class StringObfuscatorExtension {

    StringObfuscatorExtension() {

    }

}

class StringObfuscatorConf {
    final String name
    List<String> accessKey

    StringObfuscatorConf(String name) {
        this.name = name
    }

    @Override
    String toString() {
        return name
    }
}

class ReporterExtension {
    final String name
    final Map<String, String> options = [:]

    ReporterExtension(String name) {
        this.name = name
    }

    @Override
    String toString() {
        return name
    }

    def methodMissing(String name, args) {
        // I'm feeling really, really naughty.
        if (args.length == 1) {
            options[name] = args[0].toString()
        } else {
            throw new MissingMethodException(name, this.class, args)
        }
    }
}
