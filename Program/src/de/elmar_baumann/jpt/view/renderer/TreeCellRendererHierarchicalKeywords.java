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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.view.panels.HierarchicalKeywordsPanel;
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
 * {@link HierarchicalKeywordsPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeCellRendererHierarchicalKeywords extends DefaultTreeCellRenderer {

    private static final Icon         ICON_REAL             = AppLookAndFeel.getIcon("icon_keyword.png");
    private static final Icon         ICON_REAL_HIGHLIGHTED = AppLookAndFeel.getIcon("icon_keyword_hk_highlighted.png");
    private static final Icon         ICON_HELPER           = AppLookAndFeel.getIcon("icon_folder.png");
    private final        List<String> keywords              = new ArrayList<String>();
    private              int          popupHighLightRow     = -1;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        assert value instanceof DefaultMutableTreeNode : "Not a DefaultMutableTreeNode: " + value;
        render(((DefaultMutableTreeNode) value).getUserObject(), row);
        return this;
    }

    private void render(Object userObject, int row) {
        boolean highlight = false;
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            boolean real = keyword.isReal() == null ? false : keyword.isReal();
            highlight = keyword.isReal() && isKeyword(keyword.getKeyword());
            setText(keyword.getKeyword());
            setIcon(real ? ICON_REAL : ICON_HELPER);
            if (highlight) {
                setForeground(AppLookAndFeel.COLOR_FOREGROUND_HIERARCHICAL_KEYWORD_TREE_IMG_HAS_KEYWORD);
                setBackground(AppLookAndFeel.COLOR_BACKGROUND_HIERARCHICAL_KEYWORD_TREE_IMG_HAS_KEYWORD);
                // Necessary here and not obove
                setIcon(ICON_REAL_HIGHLIGHTED);
            }
        } else { // Root item
            setIcon(ICON_REAL);
        }
        setOpaque(row == popupHighLightRow || highlight);
        if (row == popupHighLightRow) {
            setForeground(AppLookAndFeel.COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE);
            setBackground(AppLookAndFeel.COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE);
        }
    }

    private boolean isKeyword(Object value) {
        assert value != null : "value is null!";
        synchronized (keywords) {
            return keywords.contains(value.toString());
        }
    }

    /**
     * Sets keywords to highlight.
     *
     * @param keywords keywords
     */
    public void setKeywords(Collection<? extends String> keywords) {
        if (keywords == null) throw new NullPointerException("keywords == null");
        synchronized (this.keywords) {
            this.keywords.clear();
            this.keywords.addAll(keywords);
        }
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
