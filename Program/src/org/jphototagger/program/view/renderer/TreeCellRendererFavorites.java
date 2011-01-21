package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.data.Timeline;

import java.awt.Component;

import java.io.File;

import javax.swing.filechooser.FileSystemView;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for {@link org.jphototagger.program.data.Timeline} nodes.
 *
 * @author Elmar Baumann
 */
public final class TreeCellRendererFavorites extends DefaultTreeCellRenderer {
    private static final long    serialVersionUID = 4280765256503091379L;
    private final FileSystemView fileSystemView   =
        FileSystemView.getFileSystemView();
    private int tempSelRow = -1;

    public TreeCellRendererFavorites() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                                           row, hasFocus);

        DefaultMutableTreeNode node       = (DefaultMutableTreeNode) value;
        Object                 userObject = node.getUserObject();

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
                synchronized (fileSystemView) {
                    try {
                        setIcon(fileSystemView.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLogger.logSevere(TreeCellRendererFavorites.class,
                                            ex);
                    }
                }
            }
        }

        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow  = row == tempSelRow;

        setForeground((isTempSelRow || (selected &&!tempSelExists))
                      ? AppLookAndFeel.getTreeSelectionForeground()
                      : AppLookAndFeel.getTreeTextForeground());
        setBackground((isTempSelRow || (selected &&!tempSelExists))
                      ? AppLookAndFeel.getTreeSelectionBackground()
                      : AppLookAndFeel.getTreeTextBackground());
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
}
