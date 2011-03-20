package org.jphototagger.lib.io.filefilter;


import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 *
 * @author Elmar Baumann
 */
public final class AcceptExactFilenameNameFileFilter implements FileFilter, Serializable {
    private static final long serialVersionUID = 1241542302327284769L;
    private final String filename;
    private boolean ignoreCase = true;

    public AcceptExactFilenameNameFileFilter(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        this.filename = filename;
    }

    /**
     *
     * @param ignore Default: true
     */
    public void setIgnoreCase(boolean ignore) {
        this.ignoreCase = ignore;
    }

    @Override
    public boolean accept(File file) {
        if (!file.isFile()) {
            return false;
        }

        String name = file.getName();

        return ignoreCase
                ? name.equalsIgnoreCase(filename)
                : name.equals(filename);
    }

    public javax.swing.filechooser.FileFilter forFileChooser(String description) {
        return new FileChooserFilter(this, description);
    }
}
