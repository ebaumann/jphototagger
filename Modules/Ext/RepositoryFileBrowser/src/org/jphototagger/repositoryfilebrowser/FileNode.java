package org.jphototagger.repositoryfilebrowser;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.jphototagger.api.nodes.Node;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileNode implements Node {

    private final File file;

    public FileNode(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.file = file;
    }

    @Override
    public Collection<?> getLookupContent() {
        return Collections.singleton(file);
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
