package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import de.elmar_baumann.lib.resource.Bundle;
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

/**
 * Renders items of {@link TreeModelAllSystemDirectories}. Ddisplays only the
 * directory names instead of their full path.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-07-23
 */
public final class TreeCellRendererAllSystemDirectories extends DefaultTreeCellRenderer {

    private final FileSystemView fileSystemView = FileSystemView.
            getFileSystemView();
    private Icon rootIcon = IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_workspace.png"); // NOI18N
    private static final String DISPLAY_NAME_ROOT =
            Bundle.getString(
            "TreeCellRendererAllSystemDirectories.DisplayName.Root"); // NOI18N
    private int popupHighLightRow = -1;
    private Color foregroundColorHighlightPopup;
    private Color backgroundColorHighlightPopup;

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                row, hasFocus);

        if (value == tree.getModel().getRoot()) {
            setIcon(rootIcon);
            setText(DISPLAY_NAME_ROOT);
        } else if (value instanceof DefaultMutableTreeNode) {
            File file = null;
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof File) {
                file = (File) userObject;
            }
            if (file != null && file.exists()) {
                synchronized (fileSystemView) {
                    try {
                        setIcon(fileSystemView.getSystemIcon(file));
                    } catch (Exception ex) {
                        Logger.getLogger(TreeCellRendererAllSystemDirectories.class.
                                getName()).log(Level.WARNING, null, ex);
                    }
                }
            }
            setText(getDirectoryName(file));
        }
        renderHighlightPopup(row);
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

    public void setHighlightColorsForPopup(Color foreground, Color background) {
        foregroundColorHighlightPopup = foreground;
        backgroundColorHighlightPopup = background;
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }

    private void renderHighlightPopup(int row) {
        if (row == popupHighLightRow && foregroundColorHighlightPopup != null &&
                backgroundColorHighlightPopup != null) {
            System.out.println("row: " + row + " hlr: " + popupHighLightRow); // NOI18N
            setOpaque(true);
            setForeground(foregroundColorHighlightPopup);
            setBackground(backgroundColorHighlightPopup);
        } else {
            setOpaque(false);
        }
    }
}
