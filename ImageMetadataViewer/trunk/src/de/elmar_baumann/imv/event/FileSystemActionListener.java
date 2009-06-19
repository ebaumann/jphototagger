package de.elmar_baumann.imv.event;

import java.io.File;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/20
 */
public interface FileSystemActionListener {
    
    
    /**
     * Called if an file system was performed.
     * 
     * @param action  action
     * @param src     source file
     * @param target  target file
     */
    public void actionPerformed(FileSystemEvent action, File src, File target);
    
    /**
     * Called if an file system action failed.
     * 
     * @param action  action
     * @param error   error
     * @param src     source file
     * @param target  target file
     */
    public void actionFailed(FileSystemEvent action, FileSystemError error, File src, File target);

}
