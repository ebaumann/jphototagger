package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.util.help.HelpPage;
import de.elmar_baumann.lib.util.help.HelpNode;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class TreeCellRendererHelpContents extends DefaultTreeCellRenderer {

    private static final ImageIcon iconSectionCollapsed = IconUtil.getImageIcon("/de/elmar_baumann/lib/resource/icon_help_section_collapsed.png");
    private static final ImageIcon iconSectionExpanded = IconUtil.getImageIcon("/de/elmar_baumann/lib/resource/icon_help_section_expanded.png");
    private static final ImageIcon iconPage = IconUtil.getImageIcon("/de/elmar_baumann/lib/resource/icon_help_page.png");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
        
        if (value instanceof HelpPage) {
            setIcon(iconPage);
            setText(((HelpPage) value).getTitle());
        } else if (value instanceof HelpNode) {
            setIcon(expanded ? iconSectionExpanded : iconSectionCollapsed);
            setText(((HelpNode) value).getTitle());
        } else if (value == tree.getModel().getRoot()) {
            setIcon(null);
            setText("");
        }
        return this;
    }
}
