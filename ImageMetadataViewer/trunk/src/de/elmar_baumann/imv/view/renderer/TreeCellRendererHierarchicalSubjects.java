package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.HierarchicalSubject;
import de.elmar_baumann.imv.data.Timeline;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for {@link de.elmar_baumann.imv.data.Timeline} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeCellRendererHierarchicalSubjects extends DefaultTreeCellRenderer {

    private static final Icon ICON = AppIcons.getIcon("icon_keyword.png"); // NOI18N

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
        if (userObject instanceof HierarchicalSubject) {
            setText(((HierarchicalSubject) userObject).getSubject());
            setIcon(ICON);
        }
    }
}
