package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.data.Timeline;
import java.awt.Component;
import java.io.File;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for {@link de.elmar_baumann.imv.data.Timeline} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeCellRendererFavorites extends DefaultTreeCellRenderer {

    private final FileSystemView fileSystemView =
            FileSystemView.getFileSystemView();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                row, hasFocus);

        assert value instanceof DefaultMutableTreeNode;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        render(userObject);
        return this;
    }

    private void render(Object userObject) {
        File file = null;
        if (userObject instanceof FavoriteDirectory) {
            FavoriteDirectory favoriteDirectory =
                    (FavoriteDirectory) userObject;
            file = favoriteDirectory.getDirectory();
            setText(favoriteDirectory.getFavoriteName());
        } else if (userObject instanceof File) {
            file = (File) userObject;
            setText(file != null
                    ? getDirectoryName(file)
                    : ""); // NOI18N
        }
        if (file != null) {
            if (file.exists()) {
                synchronized (fileSystemView) {
                    try {
                        setIcon(fileSystemView.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLog.logSevere(TreeCellRendererFavorites.class, ex);
                    }
                }
            }
        }
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
