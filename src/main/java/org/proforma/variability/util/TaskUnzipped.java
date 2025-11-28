package org.proforma.variability.util;

import java.nio.file.Path;

import proforma.xml21.TaskType;

public class TaskUnzipped {

    private TaskType task;
    private Path pathToUnzippedContents;
    private Path pathToTaskXml;
    public TaskUnzipped(TaskType task, Path pathToUnzippedContents, Path pathToTaskXml) {
        super();
        this.task = task;
        this.pathToUnzippedContents = pathToUnzippedContents;
        this.pathToTaskXml= pathToTaskXml;
    }
    public TaskType getTask() {
        return task;
    }
    public void setTask(TaskType task) {
        this.task = task;
    }
    public Path getPathToUnzippedContents() {
        return pathToUnzippedContents;
    }
    public void setPathToUnzippedContents(Path pathToUnzippedContents) {
        this.pathToUnzippedContents = pathToUnzippedContents;
    }
    public Path getPathToTaskXml() {
        return pathToTaskXml;
    }
    public void setPathToTaskXml(Path pathToTaskXml) {
        this.pathToTaskXml = pathToTaskXml;
    }
    
    
}
