package org.jphototagger.program.event.listener;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@code org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * @author Elmar Baumann
 */
public final class TreeExpandMouseListener extends TreeMouseListener implements ActionListener {

    private final JPopupMenu popupMenu = UiFactory.popupMenu();
    private final JMenuItem itemExpand = UiFactory.menuItem(Bundle.getString(TreeExpandMouseListener.class, "TreeExpandMouseListener.ItemExpand"));
    private final JMenuItem itemCollapse = UiFactory.menuItem(Bundle.getString(TreeExpandMouseListener.class, "TreeExpandMouseListener.ItemCollapse"));

    public TreeExpandMouseListener() {
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
