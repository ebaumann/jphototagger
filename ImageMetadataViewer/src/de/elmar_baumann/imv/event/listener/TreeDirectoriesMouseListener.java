package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                if (o instanceof DirectoryTreeModelFile && o2 instanceof DirectoryTreeModelFile) {
                    DirectoryTreeModelFile selectedDirectory = (DirectoryTreeModelFile) o;
                    DirectoryTreeModelFile popupDirectory = (DirectoryTreeModelFile) o2;
                    if (selectedDirectory.equals(popupDirectory)) {
                        popup.setDirectoryName(selectedDirectory.getAbsolutePath());
                        isSelectedItem = true;
                    }
                }
            }
            popup.setEnabledAddToFavoriteDirectories(isSelectedItem);
            popup.show(tree, x, y);
        } else {
            if (path != null) {
                tree.setSelectionPath(path);
            }
        }
    }
}
