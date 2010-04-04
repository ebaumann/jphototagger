/*
 * @(#)TreeSelectionContent.java    Created on 2010-04-04
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

package org.jphototagger.lib.componentutil;

import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.Content;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Contains whatever is selected in a {@link JTree}.
 * <p>
 * Handles temporary mouse selections (left click on a not selected tree item),
 * if the tree has a popup menu
 * ({@link javax.swing.JComponent#getComponentPopupMenu()}).
 * <p>
 * The content are arrays of {@link TreePath} instances or null.
 *
 * @author Elmar Baumann
 */
public final class TreeSelectionContent extends Content<TreePath[]>
        implements TreeSelectionListener, MouseListener {
    private final JTree      tree;
    private final JPopupMenu popup;

    public TreeSelectionContent(JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        this.tree = tree;
        popup     = tree.getComponentPopupMenu();
        tree.addMouseListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        setTreePaths(tree.getSelectionPaths());
    }

    private void setTreePaths(TreePath[] paths) {
        if (paths == null) {
            remove();
        } else {
            set(paths);
        }
    }

    private void displayPopup(MouseEvent evt) {
        if (TreeUtil.isMouseOverTreePath(evt, tree)) {
            setTreePaths(evt);
            popup.show(tree, evt.getX(), evt.getY());
        }
    }

    private boolean setTreePaths(MouseEvent evt) {
        assert TreeUtil.isMouseOverTreePath(evt, tree);

        TreePath mouseCursorPath = TreeUtil.getTreePath(evt);

        if (tree.isPathSelected(mouseCursorPath)) {
            setTreePaths(tree.getSelectionPaths());
        } else {
            setTreePaths(new TreePath[] { mouseCursorPath });
        }

        return true;
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if ((popup != null) && SystemUtil.isWindows()
                && MouseEventUtil.isPopupTrigger(evt)) {
            displayPopup(evt);
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if ((popup != null) && SystemUtil.isMac()
                && MouseEventUtil.isPopupTrigger(evt)) {
            displayPopup(evt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseExited(MouseEvent evt) {

        // ignore
    }
}
