package com.efraespada.stringobfuscatorplugin.reporters

import com.efraespada.stringobfuscatorplugin.Timing
import org.gradle.BuildResult
import org.gradle.api.logging.Logger

abstract class AbstractStringObfuscatorReporter {
    Map<String, String> options
    Logger logger

    AbstractStringObfuscatorReporter(Map<String, String> options, Logger logger) {
        this.options = options
        this.logger = logger
    }

    abstract run(List<Timing> timings)

    String getOption(String name, String defaultVal) {
        options[name] == null ? defaultVal : options[name]
    }

    void onBuildResult(BuildResult result) {}
}
