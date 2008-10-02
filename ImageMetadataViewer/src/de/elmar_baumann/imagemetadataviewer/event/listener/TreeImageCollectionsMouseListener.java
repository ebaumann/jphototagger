package de.elmar_baumann.imagemetadataviewer.event.listener;

import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeImageCollections;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Reagiert auf Mausklicks im <code>JTree</code> mit Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/08
 */
public class TreeImageCollectionsMouseListener extends MouseAdapter {

    private PopupMenuTreeImageCollections popup = PopupMenuTreeImageCollections.getInstance();

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
                    String itemText = path.getLastPathComponent().toString();
                    popup.setImageCollectionName(itemText);
                }
                popup.setEnabledDelete(isSelectedItem && !isRootItem);
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
