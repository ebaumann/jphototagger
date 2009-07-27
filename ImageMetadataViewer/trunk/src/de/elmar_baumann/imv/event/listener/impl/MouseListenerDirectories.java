package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
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
 * @version 2008-09-24
 */
public final class MouseListenerDirectories extends MouseAdapter {

    private final PopupMenuDirectories popupMenu = PopupMenuDirectories.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        TreePath path = TreeUtil.getTreePath(e);
        if (path == null) return;
        if (MouseEventUtil.isPopupTrigger(e)) {
            if (!TreeUtil.isRootItemPosition(e)) {
                Object lastPathComponent = path.getLastPathComponent();
                if (lastPathComponent instanceof DefaultMutableTreeNode) {
                    Object usrOb = ((DefaultMutableTreeNode) lastPathComponent).
                            getUserObject();
                    if (usrOb instanceof File) {
                        File dir = (File) usrOb;
                        popupMenu.setDirectoryName(dir.getAbsolutePath());
                        popupMenu.setTreePath(path);
                    }
                }
            }
            popupMenu.show((JTree) e.getSource(), e.getX(), e.getY());
        }
    }
}
