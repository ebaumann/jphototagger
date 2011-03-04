package org.jphototagger.program.event.listener;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface FileSystemListener {
    void fileCopied(File source, File target);

    void fileDeleted(File file);

    void fileMoved(File source, File target);

    void fileRenamed(File oldFile, File newFile);
}
