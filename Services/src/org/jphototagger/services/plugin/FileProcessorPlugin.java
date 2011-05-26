package org.jphototagger.services.plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * A Plugin processing in the UI selected files. JPhotoTagger presents the
 * capability of this plugin to the user if files are selected and calls
 * {@link #processFiles(List)}. Examples are transferring images to a web service
 * or converting image files into other file formats.
 *
 * @author Elmar Baumann
 */
public interface FileProcessorPlugin extends Plugin {

    void processFiles(Collection<? extends File> files);
    void addFileProcessorPluginListener(FileProcessorPluginListener listener);
    void removeFileProcessorPluginListener(FileProcessorPluginListener listener);
}
