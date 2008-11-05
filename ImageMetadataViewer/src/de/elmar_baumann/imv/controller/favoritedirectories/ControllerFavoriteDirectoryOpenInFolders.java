package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
public class ControllerFavoriteDirectoryOpenInFolders extends Controller
    implements ActionListener {

    PopupMenuListFavoriteDirectories popup = PopupMenuListFavoriteDirectories.getInstance();
    AppPanel appPanel = Panels.getInstance().getAppPanel();
    JTabbedPane tabbedPaneSelection = appPanel.getTabbedPaneSelection();
    Component tabTreeDirectories = appPanel.getTabSelectionDirectories();
    JTree treeDirectories = appPanel.getTreeDirectories();
    JList listFavoriteDirectories = appPanel.getListFavoriteDirectories();

    public ControllerFavoriteDirectoryOpenInFolders() {
        popup.addActionListenerOpenInFolders(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl() && listFavoriteDirectories.getSelectedIndex() >= 0) {
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
