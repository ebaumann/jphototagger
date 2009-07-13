package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.util.help.HelpPage;
import de.elmar_baumann.lib.util.help.HelpNode;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for a tree displaying help contents for values that
 * are an instance of
 * {@link de.elmar_baumann.lib.util.help.HelpNode} and
 * {@link de.elmar_baumann.lib.util.help.HelpPage}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public final class TreeCellRendererHelpContents extends DefaultTreeCellRenderer {

    private static final ImageIcon ICON_SECTION_COLLAPSED =
            IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_help_section_collapsed.png"); // NOI18N
    private static final ImageIcon ICON_SECTION_EXPANDED =
            IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_help_section_expanded.png"); // NOI18N
    private static final ImageIcon ICON_PAGE =
            IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_help_page.png"); // NOI18N

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, false, row, hasFocus);

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
            setText(""); // NOI18N
        }
        return this;
    }
}
