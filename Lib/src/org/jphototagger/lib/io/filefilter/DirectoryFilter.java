package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

/**
 * Accepts only directories.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class DirectoryFilter implements java.io.FileFilter, Serializable {

    /**
     * Instance which accepts hidden files.
     */
    public static final DirectoryFilter ACCEPT_HIDDEN_FILES = new DirectoryFilter(Option.ACCEPT_HIDDEN_FILES);

    /**
     * Instance which rejects hidden files.
     */
    public static final DirectoryFilter NO_OPTIONS = new DirectoryFilter(Option.NO_OPTION);
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final long serialVersionUID = 5618213820669639267L;
    private final List<Option> options;

    public enum Option {

        /**
         * Accepting hidden files (Default: Do not accept hidden files)
         */
        ACCEPT_HIDDEN_FILES, NO_OPTION
        ;
    }

    /**
     * Constructor.
     *
     * @param options  options
     */
    public DirectoryFilter(Option... options) {
        if (options == null) {
            throw new NullPointerException("options == null");
        }

        this.options = Arrays.asList(options);
    }

    @Override
    public boolean accept(File file) {
        boolean isDirectory = file.isDirectory();

        return options.contains(Option.ACCEPT_HIDDEN_FILES)
               ? isDirectory
               : isDirectory &&!FILE_SYSTEM_VIEW.isHiddenFile(file);
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @param  description  description
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser(String description) {
        return new FileChooserFilter(this, description);
    }
}
