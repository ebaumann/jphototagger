package de.elmar_baumann.imagemetadataviewer.event.listener;

import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeSavedSearches;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Beobachtet Mausklicks im JTree mit gespeicherten Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public class TreeSavedSearchesMouseListener extends MouseAdapter {

    private PopupMenuTreeSavedSearches popup = PopupMenuTreeSavedSearches.getInstance();

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof JTree) {
            JTree tree = (JTree) e.getSource();
            int x = e.getX();
            int y = e.getY();
            TreePath path = tree.getPathForLocation(x, y);
            if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
                boolean isSelectedItem = TreeUtil.isSelectedItemPosition(e);
                boolean isRootItem = TreeUtil.isRootItemPosition(e);
                if (isSelectedItem && !isRootItem) {
                    Object node = path.getLastPathComponent();
                    if (node instanceof SavedSearch) {
                        SavedSearch data = (SavedSearch) node;
                        popup.setSavedSearch(data);
                    }
                }
                popup.setEnabledDelete(isSelectedItem && !isRootItem);
                popup.setEnabledEdit(isSelectedItem && !isRootItem);
                popup.setEnabledRename(isSelectedItem && !isRootItem);
                popup.show(tree, x, y);
            } else {
                if (path != null) {
                    tree.setSelectionPath(path);
                }
            }
        }
    }
}
