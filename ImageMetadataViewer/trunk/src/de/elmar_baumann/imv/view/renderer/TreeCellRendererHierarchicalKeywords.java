package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppColors;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
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

    private static final Icon ICON_REAL = AppIcons.getIcon("icon_keyword.png"); // NOI18N
    private static final Icon ICON_REAL_HIGHLIGHTED =
            AppIcons.getIcon("icon_keyword_hk_highlighted.png"); // NOI18N
    private static final Icon ICON_HELPER = AppIcons.getIcon("icon_folder.png"); // NOI18N
    private final List<String> keywords = new ArrayList<String>();
    private int popupHighLightRow = -1;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, false, row, hasFocus);

        assert value instanceof DefaultMutableTreeNode;
        render(((DefaultMutableTreeNode) value).getUserObject(), row);
        return this;
    }

    private void render(Object userObject, int row) {
        boolean highlight = false;
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            boolean real = keyword.isReal() == null
                           ? false
                           : keyword.isReal();
            highlight =
                    keyword.isReal() && isKeyword(keyword.getKeyword());
            setText(keyword.getKeyword());
            setIcon(real
                    ? ICON_REAL
                      : ICON_HELPER);
            if (highlight) {
                setForeground(
                        AppColors.COLOR_FOREGROUND_HIERARCHICAL_KEYWORD_TREE_IMG_HAS_KEYWORD);
                setBackground(
                        AppColors.COLOR_BACKGROUND_HIERARCHICAL_KEYWORD_TREE_IMG_HAS_KEYWORD);
                // Necessary here and not obove
                setIcon(ICON_REAL_HIGHLIGHTED);
            }
        }
        setOpaque(row == popupHighLightRow || highlight);
        if (row == popupHighLightRow) {
            setForeground(AppColors.COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE);
            setBackground(AppColors.COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE);
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
        if (keywords == null)
            throw new NullPointerException("keywords == null"); // NOI18N
        synchronized (this.keywords) {
            this.keywords.clear();
            this.keywords.addAll(keywords);
        }
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
