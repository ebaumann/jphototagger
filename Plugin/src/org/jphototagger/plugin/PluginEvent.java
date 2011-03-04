package org.jphototagger.plugin;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmar Baumann
 */
public class PluginEvent {
    public enum Type {

        /**
         * The plugin action has been started
         */
        STARTED,

        /**
         * The plugin action has been finished successfully
         */
        FINISHED_SUCCESS,

        /**
         * The plugin action has been finished with errors
         */
        FINISHED_ERRORS,
    }

    private final Type type;
    private final List<File> processedFiles = new ArrayList<File>();
    private final List<File> changedFiles = new ArrayList<File>();

    public PluginEvent(Type type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        this.type = type;
    }

    public List<File> getChangedFiles() {
        return new ArrayList<File>(changedFiles);
    }

    public void setChangedFiles(List<File> changedFiles) {
        if (changedFiles == null) {
            throw new NullPointerException("changedFiles == null");
        }

        this.changedFiles.clear();
        this.changedFiles.addAll(changedFiles);
    }

    public boolean filesChanged() {
        return changedFiles.size() > 0;
    }

    public void setProcessedFiles(List<File> processedFiles) {
        if (processedFiles == null) {
            throw new NullPointerException("processedFiles == null");
        }

        this.processedFiles.clear();
        this.processedFiles.addAll(processedFiles);
    }

    public List<File> getProcessedFiles() {
        return new ArrayList<File>(processedFiles);
    }

    public Type getType() {
        return type;
    }

    public boolean isStarted() {
        return type.equals(Type.STARTED);
    }

    public boolean isFinishedSuccessfully() {
        return type.equals(Type.FINISHED_SUCCESS);
    }

    public boolean isFinishedWithErrors() {
        return type.equals(Type.FINISHED_ERRORS);
    }

    public boolean isFinished() {
        return isFinishedSuccessfully() || isFinishedWithErrors();
    }
}
