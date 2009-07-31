package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link HierarchicalKeywordsPanel#getMenuItemToggleReal()}
 * and toggles the real property of a keyword.
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-13
 */
public class ControllerToggleRealHierarchicalKeyword
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panel;

    public ControllerToggleRealHierarchicalKeyword(
            HierarchicalKeywordsPanel _panel) {
        panel = _panel;
        listen();
    }

    private void listen() {
        // Listening to singleton popup menu via ActionListenerFactory#
        // listenToPopupMenuHierarchicalKeywords()
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_R)) {
            toggleReal();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleReal();
    }

    private void toggleReal() {
        TreePath path = PopupMenuHierarchicalKeywords.INSTANCE.getTreePath();
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
                TreeModel tm = panel.getTree().getModel();
                if (tm instanceof TreeModelHierarchicalKeywords) {
                    keyword.setReal(!keyword.isReal());
                    ((TreeModelHierarchicalKeywords) tm).changed(
                            (DefaultMutableTreeNode) node, keyword);
                } else {
                    AppLog.logWarning(
                            ControllerToggleRealHierarchicalKeyword.class,
                            Bundle.getString(
                            "ControllerToggleRealHierarchicalKeyword.Error.Model")); // NOI18N
                    }
            } else {
                MessageDisplayer.error(panel.getTree(),
                        "ControllerToggleRealHierarchicalKeyword.Error.Node", // NOI18N
                        node);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
