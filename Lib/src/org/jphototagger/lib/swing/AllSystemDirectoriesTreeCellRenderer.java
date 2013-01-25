package org.jphototagger.lib.swing;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jphototagger.lib.swing.util.LookAndFeelUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Renders items of {@code AllSystemDirectoriesTreeModel}. Ddisplays only the
 * directory names instead of their full path.
 *
 * @author Elmar Baumann
 */
public final class AllSystemDirectoriesTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AllSystemDirectoriesTreeCellRenderer.class.getName());
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final Icon ROOT_ICON = IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_workspace.png");
    private static final String DISPLAY_NAME_ROOT = Bundle.getString(AllSystemDirectoriesTreeCellRenderer.class, "AllSystemDirectoriesTreeCellRenderer.DisplayName.Root");
    private static final Color TREE_SELECTION_FOREGROUND = LookAndFeelUtil.getUiColor("Tree.selectionForeground");
    private static final Color TREE_SELECTION_BACKGROUND = LookAndFeelUtil.getUiColor("Tree.selectionBackground");
    private static final Color TREE_TEXT_BACKGROUND = LookAndFeelUtil.getUiColor("Tree.textBackground");
    private static final Color TREE_TEXT_FOREGROUND = LookAndFeelUtil.getUiColor("Tree.textForeground");
    private int tempSelRow = -1;

    public AllSystemDirectoriesTreeCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
        if (value == tree.getModel().getRoot()) {
            setIcon(ROOT_ICON);
            setText(DISPLAY_NAME_ROOT);
        } else if (value instanceof DefaultMutableTreeNode) {
            File file = null;
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof File) {
                file = (File) userObject;
            }
            if ((file != null) && file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Throwable t) {
                        Logger.getLogger(AllSystemDirectoriesTreeCellRenderer.class.getName()).log(Level.WARNING, null, t);
                    }
                }
                setText(getDirectoryName(file));
            }
        }
        renderSelectionPopup(row, selected);
        return this;
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

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }

    private void renderSelectionPopup(int row, boolean selected) {
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = row == tempSelRow;

        setForeground((isTempSelRow || (selected && !tempSelExists))
                ? TREE_SELECTION_FOREGROUND
                : TREE_TEXT_FOREGROUND);
        setBackground((isTempSelRow || (selected && !tempSelExists))
                ? TREE_SELECTION_BACKGROUND
                : TREE_TEXT_BACKGROUND);
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
}
