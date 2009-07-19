package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import java.awt.Component;
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
    private static final Icon ICON_HELPER = AppIcons.getIcon("icon_folder.png"); // NOI18N

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, false, row, hasFocus);

        assert value instanceof DefaultMutableTreeNode;
        render(((DefaultMutableTreeNode) value).getUserObject());
        return this;
    }

    private void render(Object userObject) {
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            boolean real = keyword.isReal() == null
                           ? false
                           : keyword.isReal();
            setText(keyword.getKeyword());
            setIcon(real
                    ? ICON_REAL
                    : ICON_HELPER);
        }
    }
}
