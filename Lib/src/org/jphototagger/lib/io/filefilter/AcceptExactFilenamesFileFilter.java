package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.jphototagger.lib.util.CollectionUtil;

/**
 * @author Elmar Baumann
 */
public final class AcceptExactFilenamesFileFilter implements FileFilter, Serializable {

    private static final long serialVersionUID = 1L;
    private final Collection<String> filenames;
    private boolean ignoreCase = true;

    public AcceptExactFilenamesFileFilter(Collection<? extends String> filenames) {
        if (filenames == null) {
            throw new NullPointerException("filenames == null");
        }

        this.filenames = new ArrayList<String>(filenames);
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

        String filename = file.getName();

        return ignoreCase
                ? CollectionUtil.containsStringIgnoreCase(filenames, filename)
                : filenames.contains(filename);
    }



    public javax.swing.filechooser.FileFilter forFileChooser(String description) {
        return new FileChooserFilter(this, description);
    }
}
