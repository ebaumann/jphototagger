/*
 * @(#)ControllerKeywords.java    Created on 2009-08-13
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import de.elmar_baumann.lib.componentutil.TreeUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Abstract root class for some controllers in this packages.  Contains some
 * common helper methods.
 *
 * @author  Martin Pohlack
 */
public abstract class ControllerKeywords
        implements ActionListener, KeyListener {
    private final KeywordsPanel panel;

    abstract protected boolean myKey(KeyEvent e);

    abstract protected void localAction(List<DefaultMutableTreeNode> nodes);

    abstract protected boolean canHandleMultipleNodes();

    public ControllerKeywords(KeywordsPanel _panel) {
        panel = _panel;
        listen();
    }

    private void listen() {

        // Listening to singleton popup menu via ActionListenerFactory#
        // listenToPopupMenuKeywords()
        panel.getTree().addKeyListener(this);
    }

    protected KeywordsPanel getHKPanel() {
        return panel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (myKey(e)) {
            List<DefaultMutableTreeNode> selNodes = getSelNodes(e);

            if ((selNodes != null) &&!selNodes.isEmpty()
                    && checkNodeCount(selNodes)) {
                localAction(selNodes);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<DefaultMutableTreeNode> selNodes = getSelNodes(e);

        if ((selNodes != null) &&!selNodes.isEmpty()
                && checkNodeCount(selNodes)) {
            localAction(selNodes);
        }
    }

    protected List<DefaultMutableTreeNode> getSelNodes(ActionEvent e) {
        TreePath[] selPaths = PopupMenuKeywordsTree.INSTANCE.getTreePaths();

        if (selPaths == null) {
            return null;
        }

        List<DefaultMutableTreeNode> selNodes =
            new ArrayList<DefaultMutableTreeNode>();

        for (TreePath selPath : selPaths) {
            Object node = selPath.getLastPathComponent();

            if (node instanceof DefaultMutableTreeNode) {
                selNodes.add((DefaultMutableTreeNode) node);
            }
        }

        return selNodes;
    }

    protected List<DefaultMutableTreeNode> getSelNodes(KeyEvent e) {
        if (e.getComponent() instanceof JTree) {
            JTree tree = (JTree) e.getComponent();

            if (tree.isSelectionEmpty()) {
                return null;
            }

            List<DefaultMutableTreeNode> selNodes =
                new ArrayList<DefaultMutableTreeNode>();

            for (TreePath selPath : tree.getSelectionPaths()) {
                Object node = selPath.getLastPathComponent();

                if (node instanceof DefaultMutableTreeNode) {
                    selNodes.add((DefaultMutableTreeNode) node);
                }
            }

            return selNodes;
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

    protected boolean ensureNoChild(List<DefaultMutableTreeNode> nodes) {
        int size = nodes.size();

        if (size <= 1) {
            return true;
        }

        for (int i = 0; i < size; i++) {
            DefaultMutableTreeNode parent = nodes.get(i);

            for (int j = 0; j < size; j++) {
                if (j != i) {
                    DefaultMutableTreeNode node = nodes.get(j);

                    if (TreeUtil.isAbove(parent, node)) {
                        MessageDisplayer.error(
                            null,
                            "ControllerDeleteKeywords.Tree.Error.IsChild");

                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkNodeCount(Collection<DefaultMutableTreeNode> coll) {
        if (!canHandleMultipleNodes() && (coll.size() > 1)) {
            MessageDisplayer.error(null,
                                   "ControllerKeywords.Error.MultiSelection");

            return false;
        }

        return true;
    }
}
