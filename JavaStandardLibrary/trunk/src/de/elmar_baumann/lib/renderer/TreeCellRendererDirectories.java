package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text of trees displaying a 
 * {@link de.elmar_baumann.lib.model.TreeModelDirectories }.
 * Uses {@link javax.swing.filechooser.FileSystemView#getSystemIcon(java.io.File)}
 * and displays only the directory names instead of their full path.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public final class TreeCellRendererDirectories extends DefaultTreeCellRenderer {

    private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private Icon rootIcon = IconUtil.getImageIcon("/de/elmar_baumann/lib/resource/icon_workspace.png"); // NOI18N

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        if (value == tree.getModel().getRoot()) {
            setIcon(rootIcon);
            setText(Bundle.getString("DirectoryTreeModel.Root.Text"));
        } else if (value instanceof File) {
            File file = (File) value;
            try {
                synchronized (fileSystemView) {
                    setIcon(fileSystemView.getSystemIcon(file));
                    setText(getDirectoryName(file));
                }
            } catch (Exception ex) {
                Logger.getLogger(TreeCellRendererDirectories.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        return this;
    }

    private String getDirectoryName(File file) {
        String name = file.getName();

        // Windows drive letters
        if (name.isEmpty()) {
            name = file.getAbsolutePath();
            if (name.endsWith("\\")) { // NOI18N
                name = name.substring(0, name.length() - 2) + ":"; // NOI18N
            }
        }
        return name;
    }
}
