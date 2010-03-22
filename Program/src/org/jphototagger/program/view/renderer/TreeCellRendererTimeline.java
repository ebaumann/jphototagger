/*
 * @(#)TreeCellRendererTimeline.java    Created on 2009-06-12
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

package org.jphototagger.program.view.renderer;

import org.jphototagger.program.data.Timeline;
import org.jphototagger.lib.image.util.IconUtil;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Renders items and text for {@link org.jphototagger.program.data.Timeline} nodes.
 *
 * @author  Elmar Baumann
 */
public final class TreeCellRendererTimeline extends TreeCellRendererExt {
    private static final ImageIcon ICON_YEAR =
        IconUtil.getImageIcon(
            "/org/jphototagger/program/resource/icons/icon_timeline.png");
    private static final ImageIcon ICON_MONTH =
        IconUtil.getImageIcon(
            "/org/jphototagger/program/resource/icons/icon_timeline_month.png");
    private static final ImageIcon ICON_DAY =
        IconUtil.getImageIcon(
            "/org/jphototagger/program/resource/icons/icon_timeline_day.png");
    private static final ImageIcon ICON_UNKNOWN =
        IconUtil.getImageIcon(
            "/org/jphototagger/program/resource/icons/icon_timeline_unknown.png");
    private static final long serialVersionUID = -6142860231033161129L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                                           row, hasFocus);

        DefaultMutableTreeNode node       = (DefaultMutableTreeNode) value;
        Object                 userObject = node.getUserObject();
        TreeNode               root       = node.getRoot();

        if (userObject instanceof Timeline.Date) {
            Timeline.Date          date   = (Timeline.Date) userObject;
            DefaultMutableTreeNode parent =
                (DefaultMutableTreeNode) node.getParent();

            if (parent != null) {
                boolean isYear  = parent.equals(root);
                boolean isMonth = node.getPath().length == 3;

                setIcon(isYear
                        ? ICON_YEAR
                        : isMonth
                          ? ICON_MONTH
                          : ICON_DAY);
                setText(isYear
                        ? String.valueOf(date.year)
                        : isMonth
                          ? date.getMonthDisplayName()
                          : String.valueOf(date.day));
            }
        } else if (node.equals(Timeline.getUnknownNode())) {
            setIcon(ICON_UNKNOWN);
            setText(node.getUserObject().toString());
        }

        setColors(row, selected);

        return this;
    }

    @Override
    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
