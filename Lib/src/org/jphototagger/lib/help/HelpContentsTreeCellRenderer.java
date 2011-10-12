package org.jphototagger.lib.help;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jphototagger.lib.swing.IconUtil;

/**
 * Renders items and text for a tree displaying help contents for values that
 * are an instance of
 * {@code org.jphototagger.lib.util.help.HelpNode} and
 * {@code org.jphototagger.lib.util.help.HelpPage}.
 *
 * @author Elmar Baumann
 */
public final class HelpContentsTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final ImageIcon ICON_SECTION_COLLAPSED = IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_help_section_collapsed.png");
    private static final ImageIcon ICON_SECTION_EXPANDED = IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_help_section_expanded.png");
    private static final ImageIcon ICON_PAGE = IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_help_page.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        if (value instanceof HelpPage) {
            setIcon(ICON_PAGE);
            setText(((HelpPage) value).getTitle());
        } else if (value instanceof HelpNode) {
            setIcon(expanded
                    ? ICON_SECTION_EXPANDED
                    : ICON_SECTION_COLLAPSED);
            setText(((HelpNode) value).getTitle());
        } else if (value == tree.getModel().getRoot()) {
            setIcon(null);
            setText("");
        }

        return this;
    }
}
