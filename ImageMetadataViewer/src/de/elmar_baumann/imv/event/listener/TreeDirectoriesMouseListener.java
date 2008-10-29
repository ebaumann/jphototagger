package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Reagiert auf Mausaktionen in der Treeview, die die Verzeichnisse darstellt.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public class TreeDirectoriesMouseListener extends MouseAdapter {

    private PopupMenuTreeDirectories popup = PopupMenuTreeDirectories.getInstance();

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
                if (o instanceof File && o2 instanceof File) {
                    File selectedDirectory = (File) o;
                    File popupDirectory = (File) o2;
                    if (selectedDirectory.equals(popupDirectory)) {
                        popup.setDirectoryName(selectedDirectory.getAbsolutePath());
                        popup.setTreePath(path);
                        isSelectedItem = true;
                    }
                }
            }
            popup.setFileItemsEnabled(isSelectedItem);
            popup.show(tree, x, y);
        } else {
            if (path != null) {
                popup.setTreeSelected(true);
                tree.setSelectionPath(path);
                popup.setTreeSelected(false);
            }
        }
    }
}
