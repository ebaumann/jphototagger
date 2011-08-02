package org.jphototagger.program.event.listener.impl;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.util.Bundle;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerTreeExpand extends MouseListenerTree implements ActionListener {
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JMenuItem itemExpand = new JMenuItem(Bundle.getString(MouseListenerTreeExpand.class, "MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem itemCollapse = new JMenuItem(Bundle.getString(MouseListenerTreeExpand.class, "MouseListenerTreeExpand.ItemCollapse"));

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
