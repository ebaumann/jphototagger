package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item
 * {@link PopupMenuHierarchicalKeywords#getMenuItemDisplayImages()} and on
 * action displays images with the selected keyword.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerHierarchicalKeywordsDisplayImages
        extends ControllerHierarchicalKeywords
        implements ActionListener {

    public ControllerHierarchicalKeywordsDisplayImages(
            HierarchicalKeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return false;
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            showImages((HierarchicalKeyword) userObject);
        }
    }

    private void showImages(HierarchicalKeyword hierarchicalKeyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        appPanel.getTabbedPaneSelection().setSelectedComponent(
                appPanel.getTabSelectionHierarchicalKeywords());
        HierarchicalKeywordsHelper.selectNode(
                appPanel.getTreeSelHierarchicalKeywords(),
                hierarchicalKeyword);
    }
}
