/*
 * @(#)TreeCellRendererKeywords.java    Created on 2009-06-12
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

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.view.panels.KeywordsPanel;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for nodes in the tree of the
 * {@link KeywordsPanel}.
 *
 * @author  Elmar Baumann
 */
public final class TreeCellRendererKeywords extends DefaultTreeCellRenderer {
    private static final Icon ICON_REAL =
        AppLookAndFeel.getIcon("icon_keyword.png");
    private static final Icon ICON_IMG_HAS_KEYWORD =
        AppLookAndFeel.getIcon("icon_keyword_hk_highlighted.png");
    private static final Icon ICON_HELPER =
        AppLookAndFeel.getIcon("icon_folder.png");
    private static final long  serialVersionUID  = -1948927991470364757L;
    private int                tempSelectionRow  = -1;
    private final List<String> highLightKeywords = new ArrayList<String>();

    public TreeCellRendererKeywords() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                                           row, hasFocus);
        render(((DefaultMutableTreeNode) value).getUserObject(), row);

        return this;
    }

    private void render(Object userObject, int row) {
        boolean selImgHasKeyword = false;
        boolean real             = false;
        boolean helper           = false;    // to know whether to render root item

        if (userObject instanceof Keyword) {
            Keyword keyword = (Keyword) userObject;

            real             = keyword.isReal();
            helper           = !real;
            selImgHasKeyword = real && isKeyword(keyword.getName());
            setText(keyword.getName());
        }

        boolean tempSelExists = tempSelectionRow >= 0;
        boolean isTempSelRow  = row == tempSelectionRow;

        setIcon(selImgHasKeyword
                ? ICON_IMG_HAS_KEYWORD
                : real
                  ? ICON_REAL
                  : helper
                    ? ICON_HELPER
                    : ICON_REAL);    // Last: Root item
        setForeground((isTempSelRow || (selected &&!tempSelExists))
                      ? AppLookAndFeel.getTreeSelectionForeground()
                      : selImgHasKeyword
                        ? AppLookAndFeel.TREE_SEL_IMG_HAS_KEYWORD_FOREGROUND
                        : AppLookAndFeel.getTreeTextForeground());
        setBackground((isTempSelRow || (selected &&!tempSelExists))
                      ? AppLookAndFeel.getTreeSelectionBackground()
                      : selImgHasKeyword
                        ? AppLookAndFeel.TREE_SEL_IMG_HAS_KEYWORD_BACKGROUND
                        : AppLookAndFeel.getTreeTextBackground());
    }

    private boolean isKeyword(Object value) {
        synchronized (highLightKeywords) {
            return highLightKeywords.contains(value.toString());
        }
    }

    public void setSelImgKeywords(Collection<? extends String> keywords) {
        synchronized (this.highLightKeywords) {
            this.highLightKeywords.clear();
            this.highLightKeywords.addAll(keywords);
        }
    }

    public void addSelImgKeywords(Collection<? extends String> keywords) {
        synchronized (this.highLightKeywords) {
            this.highLightKeywords.addAll(keywords);
        }
    }

    public void removeSelImgKeywords(Collection<? extends String> keywords) {
        synchronized (this.highLightKeywords) {
            this.highLightKeywords.removeAll(keywords);
        }
    }

    public void addSelImgKeyword(String keyword) {
        synchronized (highLightKeywords) {
            highLightKeywords.add(keyword);
        }
    }

    public void removeSelImgKeyword(String keyword) {
        synchronized (highLightKeywords) {
            highLightKeywords.remove(keyword);
        }
    }

    public void setTempSelectionRow(int index) {
        tempSelectionRow = index;
    }
}
