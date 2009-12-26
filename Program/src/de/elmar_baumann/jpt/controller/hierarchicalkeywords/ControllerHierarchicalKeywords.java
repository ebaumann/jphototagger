/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
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
        if (e.getComponent() instanceof JTree) {
            JTree tree = (JTree) e.getComponent();
            if (!checkSingleSelection(tree)) return null;
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

    private boolean checkSingleSelection(JTree tree) {
        if (tree.getSelectionCount() != 1) {
            MessageDisplayer.error(
                    null,
                    "ControllerHierarchicalKeywords.Error.MultiSelection");
            return false;
        }
        return true;
    }
}
