package com.efraespada.stringobfuscatorplugin

import com.efraespada.stringobfuscatorplugin.interfaces.GradleHandlerCallback
import com.efraespada.stringobfuscatorplugin.util.Clock
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class Timing {
    long ms
    String path
    boolean success
    boolean didWork
    boolean skipped

    Timing(long ms, String path, boolean success, boolean didWork, boolean skipped) {
        this.ms = ms
        this.path = path
        this.success = success
        this.didWork = didWork
        this.skipped = skipped
    }
}

class TimingRecorder extends BuildAndTaskExecutionListenerAdapter implements TaskExecutionListener {
    private Clock clock
    private List<Timing> timings = []
    private StringObfuscatorPlugin plugin
    private GradleHandlerCallback callback

    TimingRecorder(StringObfuscatorPlugin plugin, GradleHandlerCallback callback) {
        this.plugin = plugin
        this.callback = callback
    }

    @Override
    void beforeExecute(Task task) {
        clock = new Clock()
        String test = "Test";
        String pre = "pre";
        String build = "Build";
        String merge = "merge";
        String resources = "Resources";
        if (task.name.contains(pre) && task.name.contains(build) && !task.name.equals(pre + build) && !task.name.contains(test)) {
            callback.onDataFound(task.project.name, PrintUtils.uncapitalize(task.name.substring(pre.length()).substring(0, task.name.substring(pre.length()).length() - build.length())));
        } else if (task.name.contains(merge) && task.name.contains(resources) && !task.name.contains(test)) {
            callback.onMergeResources(task.project.name, PrintUtils.uncapitalize(task.name.substring(merge.length()).substring(0, task.name.substring(merge.length()).length() - resources.length())));
        }
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        timings << new Timing(
                clock.getTimeInMs(),
                task.getPath(),
                taskState.getFailure() == null,
                taskState.getDidWork(),
                taskState.getSkipped()
        )
    }

    @Override
    void buildFinished(BuildResult result) {
        plugin.reporterExtensions.each { it.run timings; it.onBuildResult result }
    }

    List<String> getTasks() {
        timings*.path
    }

    Timing getTiming(String path) {
        timings.find { it.path == path }
    }
}
