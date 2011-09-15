package org.jphototagger.api.plugin.fileprocessor;

import java.io.File;
import java.util.Collection;
import org.jphototagger.api.plugin.Plugin;

/**
 * A Plugin processing a collection of files.
 * <p>
 * Examples: Transferring images to a web service,
 * converting image files into other file formats.
 *
 * @author Elmar Baumann
 */
public interface FileProcessorPlugin extends Plugin {

    void processFiles(Collection<? extends File> files);
}
