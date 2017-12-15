package com.stringcare

import com.efraespada.stringobfuscatorplugin.CredentialUtils
import com.efraespada.stringobfuscatorplugin.FileUtils
import com.efraespada.stringobfuscatorplugin.PrintUtils
import com.efraespada.stringobfuscatorplugin.interfaces.GradleHandlerCallback
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class StringCarePlugin implements Plugin<Project> {

    private Project project;
    private static String key = null;

    Logger logger

    NamedDomainObjectCollection<ReporterExtension> reporterExtensions

    @Override
    void apply(Project project) {
        this.project = project;

        this.project.task('hello') {
            doLast {
                println 'Hello from the GreetingPlugin'
            }
        }

        this.logger = this.project.logger
        this.project.extensions.create("stringobfuscator", StringCareExtension)
        reporterExtensions = this.project.extensions.reporters = project.container(ReporterExtension)
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
                println ":" + module + ":mergeResources:" + variant + ":" + key
                FileUtils.backupStringResources()
                FileUtils.encryptStringResources()
            }

            @Override
            void onMergeResourcesFinish(String module, String variant) {
                FileUtils.restoreStringResources()
            }
        }))
    }
}

class StringCareExtension {
    // Not in use at the moment.
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