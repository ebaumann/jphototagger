package de.elmar_baumann.imv.view;

import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ViewUtil {

    /**
     * Returns the selected directory in the directories tree.
     * 
     * @param  treeDirectories  directories treee
     * @return directory or null if no directory is selected
     */
    public static File getTargetDirectory(JTree treeDirectories) {
        File directory = null;
        TreePath path = treeDirectories.getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof DirectoryTreeModelFile) {
                return (DirectoryTreeModelFile) o;
            }
        }
        return directory;
    }
}
