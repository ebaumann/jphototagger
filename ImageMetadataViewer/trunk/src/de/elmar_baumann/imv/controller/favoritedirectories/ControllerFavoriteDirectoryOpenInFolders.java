package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Opens the favorite directory in the folder panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/05
 */
public final class ControllerFavoriteDirectoryOpenInFolders implements ActionListener {

    private final PopupMenuListFavoriteDirectories popupMenu = PopupMenuListFavoriteDirectories.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JTabbedPane tabbedPaneSelection = appPanel.getTabbedPaneSelection();
    private final Component tabTreeDirectories = appPanel.getTabSelectionDirectories();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final JList listFavoriteDirectories = appPanel.getListFavoriteDirectories();

    public ControllerFavoriteDirectoryOpenInFolders() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerOpenInFolders(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (listFavoriteDirectories.getSelectedIndex() >= 0) {
            selectDirectory();
        }
    }

    private void selectDirectory() {
        FavoriteDirectory favorite =
            (FavoriteDirectory) listFavoriteDirectories.getSelectedValue();
        TreePath path = TreeUtil.getTreePath(
            new File(favorite.getDirectoryName()), treeDirectories.getModel());
        listFavoriteDirectories.clearSelection();
        tabbedPaneSelection.setSelectedComponent(tabTreeDirectories);
        TreeUtil.expandPathCascade(treeDirectories, path);
        treeDirectories.setSelectionPath(path);
    }
}
