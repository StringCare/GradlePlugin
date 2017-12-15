package com.stringcare

import com.efraespada.stringobfuscatorplugin.PrintUtils
import com.efraespada.stringobfuscatorplugin.interfaces.GradleHandlerCallback
import com.stringcare.util.Clock
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
    private StringCarePlugin plugin
    private GradleHandlerCallback callback

    private static final String TEST = "Test";
    private static final String PRE = "pre";
    private static final String BUILD = "Build";
    private static final String MERGE = "merge";
    private static final String RESOURCES = "Resources";

    TimingRecorder(StringCarePlugin plugin, GradleHandlerCallback callback) {
        this.plugin = plugin
        this.callback = callback
    }

    @Override
    void beforeExecute(Task task) {
        clock = new Clock()
        if (task.name.contains(PRE) && task.name.contains(BUILD) && !task.name.equals(PRE + BUILD) && !task.name.contains(TEST)) {
            callback.onDataFound(task.project.name, PrintUtils.uncapitalize(task.name.substring(PRE.length()).substring(0, task.name.substring(PRE.length()).length() - BUILD.length())));
        } else if (task.name.contains(MERGE) && task.name.contains(RESOURCES) && !task.name.contains(TEST)) {
            callback.onMergeResourcesStarts(task.project.name, PrintUtils.uncapitalize(task.name.substring(MERGE.length()).substring(0, task.name.substring(MERGE.length()).length() - RESOURCES.length())));
        }
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if (task.name.contains(MERGE) && task.name.contains(RESOURCES) && !task.name.contains(TEST)) {
            callback.onMergeResourcesFinish(task.project.name, PrintUtils.uncapitalize(task.name.substring(MERGE.length()).substring(0, task.name.substring(MERGE.length()).length() - RESOURCES.length())));
        }
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
