package de.elmar_baumann.lib.io;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 * Accepts only directories.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public class DirectoryFilter implements java.io.FileFilter {

    boolean accecptHidden = true;
    FileSystemView fsv = FileSystemView.getFileSystemView();

    /**
     * Constructor.
     * 
     * @param accecptHidden  true if accept hidden directories
     */
    public DirectoryFilter(boolean accecptHidden) {
        this.accecptHidden = accecptHidden;
    }

    @Override
    public boolean accept(File file) {
        boolean isDirectory = file.isDirectory();
        return accecptHidden
            ? isDirectory
            : isDirectory && !fsv.isHiddenFile(file);
    }
}
