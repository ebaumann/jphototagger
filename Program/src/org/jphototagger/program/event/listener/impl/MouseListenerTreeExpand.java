package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.resource.JptBundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerTreeExpand extends MouseListenerTree implements ActionListener {
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JMenuItem itemExpand =
        new JMenuItem(JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem itemCollapse =
        new JMenuItem(JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemCollapse"));

    public MouseListenerTreeExpand() {
        popupMenu.add(itemExpand);
        popupMenu.add(itemCollapse);
        listenExpandAllSubItems(itemExpand, true);
        listenCollapseAllSubItems(itemCollapse, true);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (path == null) {
            throw new NullPointerException("path == null");
        }

        popupMenu.show(tree, x, y);
    }
}
