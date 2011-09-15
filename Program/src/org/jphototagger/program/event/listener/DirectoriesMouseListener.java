package org.jphototagger.program.event.listener;

import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.view.popupmenus.DirectoriesPopupMenu;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * @author Elmar Baumann
 */
public final class DirectoriesMouseListener extends TreeMouseListener {

    private final DirectoriesPopupMenu popupMenu = DirectoriesPopupMenu.INSTANCE;

    public DirectoriesMouseListener() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);

        TreePath path = TreeUtil.getTreePath(evt);

        if (path == null) {
            return;
        }

        if (MouseEventUtil.isPopupTrigger(evt)) {
            if (!TreeUtil.isRootItemPosition(evt)) {
                Object lastPathComponent = path.getLastPathComponent();

                if (lastPathComponent instanceof DefaultMutableTreeNode) {
                    Object usrOb = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();

                    if (usrOb instanceof File) {
                        File dir = (File) usrOb;

                        popupMenu.setDirectory(dir);
                        popupMenu.setTreePath(path);
                    }
                }
            }

            popupMenu.show((JTree) evt.getSource(), evt.getX(), evt.getY());
        }
    }
}
