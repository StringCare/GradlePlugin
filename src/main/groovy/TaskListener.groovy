
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

class BuilderListener extends BuildAndTaskExecutionListenerAdapter implements TaskExecutionListener {
    private ClockT clock
    private List<Timing> timings = []
    private StringCare plugin
    private GradleHandlerCallback callback

    private static final String TEST = "Test"
    private static final String PRE = "pre"
    private static final String BUILD = "Build"
    private static final String MERGE = "merge"
    private static final String RESOURCES = "Resources"

    BuilderListener(StringCare plugin, GradleHandlerCallback callback) {
        this.plugin = plugin
        this.callback = callback
    }

    @Override
    void beforeExecute(Task task) {
        clock = new ClockT()
        if (task.name.contains(PRE) && task.name.contains(BUILD) && !task.name.equals(PRE + BUILD) && !task.name.contains(TEST)) {
            String module = getName(task)
            if (module != null) {
                callback.onDataFound(module, PrintUtils.uncapitalize(task.name.substring(PRE.length()).substring(0, task.name.substring(PRE.length()).length() - BUILD.length())));
            }
        } else if (task.name.contains(MERGE) && task.name.contains(RESOURCES) && !task.name.contains(TEST)) {
            String module = getName(task)
            if (module != null) {
                if (callback.debug()) {
                    // PrintUtils.print(module, "Module: " + module, true)
                }
                callback.onMergeResourcesStarts(module, PrintUtils.uncapitalize(task.name.substring(MERGE.length()).substring(0, task.name.substring(MERGE.length()).length() - RESOURCES.length())));

            } else {
                PrintUtils.print("not_needed", "🤖 module path not found, report an issue", true)
            }
        }
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if (task.name.contains(MERGE) && task.name.contains(RESOURCES) && !task.name.contains(TEST)) {
            String module = getName(task)
            if (module != null) {
                callback.onMergeResourcesFinish(module, PrintUtils.uncapitalize(task.name.substring(MERGE.length()).substring(0, task.name.substring(MERGE.length()).length() - RESOURCES.length())));
            }
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
        // plugin.reporterExtensions.each { it.run timings; it.onBuildResult result }
    }

    List<String> getTasks() {
        timings*.path
    }

    Timing getTiming(String path) {
        timings.find { it.path == path }
    }

    String getName(Task task) {
        String path = task.project.getPath()
        return path == null || path.length() == 0 ? null : path.split(":")[path.split(":").length - 1]
    }

}
