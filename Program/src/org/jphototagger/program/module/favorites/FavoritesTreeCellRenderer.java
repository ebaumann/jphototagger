package org.jphototagger.program.module.favorites;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * Renders items and text for {@code org.jphototagger.program.data.Timeline} nodes.
 *
 * @author Elmar Baumann
 */
public final class FavoritesTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FavoritesTreeCellRenderer.class.getName());
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private int tempSelRow = -1;

    public FavoritesTreeCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        render(userObject, row);
        return this;
    }

    private void render(Object userObject, int row) {
        File file = null;
        if (userObject instanceof Favorite) {
            Favorite favoriteDirectory = (Favorite) userObject;
            file = favoriteDirectory.getDirectory();
            setText(favoriteDirectory.getName());
        } else if (userObject instanceof File) {
            file = (File) userObject;
            setText(getDirectoryName(file));
        }
        if (file != null) {
            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = row == tempSelRow;
        setForeground((isTempSelRow || (selected && !tempSelExists))
                ? AppLookAndFeel.getTreeSelectionForeground()
                : AppLookAndFeel.getTreeForeground());
        setBackground((isTempSelRow || (selected && !tempSelExists))
                ? AppLookAndFeel.getTreeSelectionBackground()
                : AppLookAndFeel.getTreeBackground());
    }

    private String getDirectoryName(File file) {
        String name = file.getName();
        // Windows drive letters
        if (name.isEmpty()) {
            name = file.getAbsolutePath();
            if (name.endsWith("\\")) {
                return name.substring(0, name.length() - 2) + ":";
            }
        }
        return getDisplayname(file);
    }

    private String getDisplayname(File file) {
        synchronized(FILE_SYSTEM_VIEW) {
            try {
                return FILE_SYSTEM_VIEW.getSystemDisplayName(file);
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, null, t);
                return file.getName();
            }
        }
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
