package org.jphototagger.api.plugin.fileprocessor;

import java.io.File;
import java.util.Collection;
import org.jphototagger.api.plugin.Plugin;

/**
 * A Plugin processing a collection of files.
 *
 * @author Elmar Baumann
 */
public interface FileProcessorPlugin extends Plugin {

    void processFiles(Collection<? extends File> files);
}
