package de.elmar_baumann.lib.io;

import java.io.File;
import java.util.Set;
import javax.swing.filechooser.FileSystemView;

/**
 * Accepts only directories.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public final class DirectoryFilter implements java.io.FileFilter {

    private static FileSystemView fsv = FileSystemView.getFileSystemView();
    private final Set<Option> options;

    public enum Option {

        ACCEPT_HIDDEN_FILES,
        REJECT_HIDDEN_FILES,
    }

    /**
     * Constructor.
     * 
     * @param accecptHidden  true if accept hidden directories
     */
    public DirectoryFilter(Set<Option> options) {
        this.options = options;
    }

    @Override
    public boolean accept(File file) {
        boolean isDirectory = file.isDirectory();
        return options.contains(Option.ACCEPT_HIDDEN_FILES)
            ? isDirectory
            : isDirectory && !fsv.isHiddenFile(file);
    }
}
