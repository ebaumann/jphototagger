package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Reagiert auf Mausaktionen in der Treeview, die die Verzeichnisse darstellt.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public final class MouseListenerDirectories extends MouseAdapter {

    private final PopupMenuDirectories popupMenu =
            PopupMenuDirectories.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        int x = e.getX();
        int y = e.getY();
        TreePath path = tree.getPathForLocation(x, y);
        if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
            boolean isSelectedItem = TreeUtil.isSelectedItemPosition(e);
            boolean isRootItem = TreeUtil.isRootItemPosition(e);
            if (isSelectedItem && !isRootItem) {
                Object o = tree.getSelectionPath().getLastPathComponent();
                Object o2 = path.getLastPathComponent();
                if (o instanceof DefaultMutableTreeNode &&
                        o2 instanceof DefaultMutableTreeNode) {
                    Object userObj1 =
                            ((DefaultMutableTreeNode) o).getUserObject();
                    Object userObj2 = ((DefaultMutableTreeNode) o2).
                            getUserObject();
                    if (userObj1 instanceof File & userObj2 instanceof File) {
                        File selectedDirectory = (File) userObj1;
                        File popupDirectory = (File) userObj2;
                        if (selectedDirectory.equals(popupDirectory)) {
                            popupMenu.setDirectoryName(selectedDirectory.
                                    getAbsolutePath());
                            popupMenu.setTreePath(path);
                            isSelectedItem = true;
                        }
                    }
                }
            }
            popupMenu.setFileItemsEnabled(isSelectedItem);
            popupMenu.show(tree, x, y);
        } else {
            if (path != null) {
                popupMenu.setTreeSelected(true);
                tree.setSelectionPath(path);
                popupMenu.setTreeSelected(false);
            }
        }
    }
}
