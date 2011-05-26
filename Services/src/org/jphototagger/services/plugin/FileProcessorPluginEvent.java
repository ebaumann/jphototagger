package org.jphototagger.services.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Elmar Baumann
 */
public class FileProcessorPluginEvent {

    public enum Type {

        PROCESSING_STARTED,
        PROCESSING_FINISHED_SUCCESS,
        PROCESSING_FINISHED_ERRORS;

        public boolean isFinished() {
            return this.equals(PROCESSING_FINISHED_SUCCESS) || this.equals(PROCESSING_FINISHED_ERRORS);
        }
    }
    private final Type type;
    private final Collection<File> processedFiles = new ArrayList<File>();
    private final Collection<File> changedFiles = new ArrayList<File>();

    public FileProcessorPluginEvent(Type type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        this.type = type;
    }

    public Collection<? extends File> getChangedFiles() {
        return Collections.unmodifiableCollection(changedFiles);
    }

    public void setChangedFiles(Collection<? extends File> changedFiles) {
        if (changedFiles == null) {
            throw new NullPointerException("changedFiles == null");
        }

        this.changedFiles.clear();
        this.changedFiles.addAll(changedFiles);
    }

    public boolean filesChanged() {
        return changedFiles.size() > 0;
    }

    public void setProcessedFiles(Collection<? extends File> processedFiles) {
        if (processedFiles == null) {
            throw new NullPointerException("processedFiles == null");
        }

        this.processedFiles.clear();
        this.processedFiles.addAll(processedFiles);
    }

    public Collection<? extends File> getProcessedFiles() {
        return Collections.unmodifiableCollection(processedFiles);
    }

    public Type getType() {
        return type;
    }

    public boolean isStarted() {
        return type.equals(Type.PROCESSING_STARTED);
    }

    public boolean isFinishedSuccessfully() {
        return type.equals(Type.PROCESSING_FINISHED_SUCCESS);
    }

    public boolean isFinishedWithErrors() {
        return type.equals(Type.PROCESSING_FINISHED_ERRORS);
    }

    public boolean isFinished() {
        return isFinishedSuccessfully() || isFinishedWithErrors();
    }
}
