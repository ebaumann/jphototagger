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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import org.jphototagger.lib.componentutil.TreeUtil;

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

    abstract protected boolean myKey(KeyEvent evt);

    abstract protected void localAction(List<DefaultMutableTreeNode> nodes);

    abstract protected boolean canHandleMultipleNodes();

    public ControllerKeywords(KeywordsPanel _panel) {
        if (_panel == null) {
            throw new NullPointerException("_panel == null");
        }

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
    public void keyPressed(KeyEvent evt) {
        if (myKey(evt)) {
            List<DefaultMutableTreeNode> selNodes = getSelNodes(evt);

            if ((selNodes != null) &&!selNodes.isEmpty()
                    && checkNodeCount(selNodes)) {
                localAction(selNodes);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        List<DefaultMutableTreeNode> selNodes = getSelNodes(evt);

        if ((selNodes != null) &&!selNodes.isEmpty()
                && checkNodeCount(selNodes)) {
            localAction(selNodes);
        }
    }

    protected List<DefaultMutableTreeNode> getSelNodes(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

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

    protected List<DefaultMutableTreeNode> getSelNodes(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (evt.getComponent() instanceof JTree) {
            JTree tree = (JTree) evt.getComponent();

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
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }

    protected boolean ensureNoChild(List<DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

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
