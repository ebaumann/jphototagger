package org.jphototagger.lib.renderer;

import org.jphototagger.lib.componentutil.LookAndFeelUtil;
import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.lib.resource.JslBundle;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items of {@link TreeModelAllSystemDirectories}. Ddisplays only the
 * directory names instead of their full path.
 *
 * @author Elmar Baumann
 */
public final class TreeCellRendererAllSystemDirectories extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = -1995225344254643215L;
    private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private Icon rootIcon = IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_workspace.png");
    private static final String DISPLAY_NAME_ROOT =
        JslBundle.INSTANCE.getString("TreeCellRendererAllSystemDirectories.DisplayName.Root");
    private int tempSelRow = -1;
    private static final Color TREE_SELECTION_FOREGROUND = LookAndFeelUtil.getUiColor("Tree.selectionForeground");
    private static final Color TREE_SELECTION_BACKGROUND = LookAndFeelUtil.getUiColor("Tree.selectionBackground");
    private static final Color TREE_TEXT_BACKGROUND = LookAndFeelUtil.getUiColor("Tree.textBackground");
    private static final Color TREE_TEXT_FOREGROUND = LookAndFeelUtil.getUiColor("Tree.textForeground");

    public TreeCellRendererAllSystemDirectories() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        if (value == tree.getModel().getRoot()) {
            setIcon(rootIcon);
            setText(DISPLAY_NAME_ROOT);
        } else if (value instanceof DefaultMutableTreeNode) {
            File file = null;
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

            if (userObject instanceof File) {
                file = (File) userObject;
            }

            if ((file != null) && file.exists()) {
                synchronized (fileSystemView) {
                    try {
                        setIcon(fileSystemView.getSystemIcon(file));
                    } catch (Exception ex) {
                        Logger.getLogger(TreeCellRendererAllSystemDirectories.class.getName()).log(Level.WARNING, null,
                                         ex);
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
                name = name.substring(0, name.length() - 2) + ":";
            }
        }

        return name;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }

    private void renderSelectionPopup(int row, boolean selected) {
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = row == tempSelRow;

        setForeground((isTempSelRow || (selected &&!tempSelExists))
                      ? TREE_SELECTION_FOREGROUND
                      : TREE_TEXT_FOREGROUND);
        setBackground((isTempSelRow || (selected &&!tempSelExists))
                      ? TREE_SELECTION_BACKGROUND
                      : TREE_TEXT_BACKGROUND);
    }
}
