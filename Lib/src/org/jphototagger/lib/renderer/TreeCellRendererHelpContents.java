package org.jphototagger.lib.renderer;

import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.lib.util.help.HelpNode;
import org.jphototagger.lib.util.help.HelpPage;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for a tree displaying help contents for values that
 * are an instance of
 * {@link org.jphototagger.lib.util.help.HelpNode} and
 * {@link org.jphototagger.lib.util.help.HelpPage}.
 *
 * @author Elmar Baumann
 */
public final class TreeCellRendererHelpContents extends DefaultTreeCellRenderer {
    private static final ImageIcon ICON_SECTION_COLLAPSED =
        IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_help_section_collapsed.png");
    private static final ImageIcon ICON_SECTION_EXPANDED =
        IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_help_section_expanded.png");
    private static final ImageIcon ICON_PAGE =
        IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_help_page.png");
    private static final long serialVersionUID = 205076451185009235L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
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
