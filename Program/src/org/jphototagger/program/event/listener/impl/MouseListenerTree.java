/*
 * @(#)MouseListenerTree.java    Created on 2009-12-20
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree}.
 *
 * @author Elmar Baumann
 */
public class MouseListenerTree extends MouseAdapter implements ActionListener {
    private JTree     tree;
    private TreePath  path;
    private int       x;
    private int       y;
    private JMenuItem itemExpandAllSubItems;
    private JMenuItem itemCollapseExpandAllSubItems;

    public void listenExpandAllSubItems(JMenuItem item, boolean listen) {
        if (item == null) {
            throw new NullPointerException("item == null");
        }

        itemExpandAllSubItems = listen
                                ? item
                                : null;

        if (listen) {
            item.addActionListener(this);
        } else {
            item.removeActionListener(this);
        }
    }

    public void listenCollapseAllSubItems(JMenuItem item, boolean listen) {
        if (item == null) {
            throw new NullPointerException("item == null");
        }

        itemCollapseExpandAllSubItems = listen
                                        ? item
                                        : null;

        if (listen) {
            item.addActionListener(this);
        } else {
            item.removeActionListener(this);
        }
    }

    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {}

    @Override
    public void mousePressed(MouseEvent evt) {
        if (!MouseEventUtil.isPopupTrigger(evt)) {
            return;
        }

        reset(TreeUtil.getTreePath(evt));

        if (path != null) {
            Object source = evt.getSource();

            if (source instanceof JTree) {
                tree = (JTree) source;
                popupTrigger(tree, path, evt.getX(), evt.getY());
            }
        }
    }

    private void reset(TreePath p) {
        path = p;
        tree = null;
        x    = -1;
        y    = -1;
    }

    public JTree getTree() {
        return tree;
    }

    public TreePath getPath() {
        return path;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if ((source == itemExpandAllSubItems) && (tree != null)) {
            TreeUtil.expandAll(tree, path, true);
        } else if ((source == itemCollapseExpandAllSubItems)
                   && (tree != null)) {
            TreeUtil.expandAll(tree, path, false);
        }
    }
}
