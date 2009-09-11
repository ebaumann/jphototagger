package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
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
        implements ActionListener {

    private final PopupMenuHierarchicalKeywords popup =
            PopupMenuHierarchicalKeywords.INSTANCE;
    private final JMenuItem itemHk = popup.getMenuItemDisplayImages();
    private final JMenuItem itemKw = popup.getMenuItemDisplayImagesKw();

    public ControllerHierarchicalKeywordsDisplayImages() {
        listen();
    }

    private void listen() {
        itemHk.addActionListener(this);
        itemKw.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HierarchicalKeyword keyword = getKeyword();
        if (keyword == null) return;
        Object source = e.getSource();
        if (source == itemHk) {
            showImages(keyword);
        } else if (source == itemKw) {
            showImages(keyword.getKeyword());
        }
    }

    private HierarchicalKeyword getKeyword() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) popup.getTreePath().
                getLastPathComponent();
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            return (HierarchicalKeyword) userObject;
        }
        return null;
    }

    private void showImages(HierarchicalKeyword hierarchicalKeyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        appPanel.getTabbedPaneSelection().setSelectedComponent(
                appPanel.getTabSelectionHierarchicalKeywords());
        HierarchicalKeywordsHelper.selectNode(
                appPanel.getTreeSelHierarchicalKeywords(),
                hierarchicalKeyword);
    }

    private void showImages(String keyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        appPanel.getTabbedPaneSelection().setSelectedComponent(
                appPanel.getTabSelectionKeywords());
        ListUtil.select(appPanel.getListKeywords(), keyword, 0);
    }
}
