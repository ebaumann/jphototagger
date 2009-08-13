package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Abstract root class for some controllers in this packages.  Contains some
 * common helper methods.
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-08-13
 */
public abstract class ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {
    private final HierarchicalKeywordsPanel panel;

    public ControllerHierarchicalKeywords(HierarchicalKeywordsPanel _panel) {
        panel = _panel;
        listen();
    }

    private void listen() {
        // Listening to singleton popup menu via ActionListenerFactory#
        // listenToPopupMenuHierarchicalKeywords()
        panel.getTree().addKeyListener(this);
    }

    protected HierarchicalKeywordsPanel getHKPanel() {
        return panel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (myKey(e)) {
            DefaultMutableTreeNode node = getSourceNode(e);
            if (node != null) {
                localAction(node);
            }
        }
    }

    abstract protected boolean myKey(KeyEvent e);

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode node = getSourceNode(e);
        if (node != null) {
            localAction(node);
        }
    }

    abstract protected void localAction(DefaultMutableTreeNode node);

    protected DefaultMutableTreeNode getSourceNode(ActionEvent e) {
        TreePath path = PopupMenuHierarchicalKeywords.INSTANCE.getTreePath();
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) node;
        }
        return null;
    }

    protected DefaultMutableTreeNode getSourceNode(KeyEvent e) {
        if (e.getComponent() instanceof JTree)
        {
            JTree tree = (JTree)e.getComponent();
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                return (DefaultMutableTreeNode) node;
            }
        }
        return null;
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
