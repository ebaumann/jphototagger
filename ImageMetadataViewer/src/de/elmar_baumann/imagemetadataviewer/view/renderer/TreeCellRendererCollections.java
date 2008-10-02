package de.elmar_baumann.imagemetadataviewer.view.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public class TreeCellRendererCollections extends DefaultTreeCellRenderer {

    private static final Icon rootIcon = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_image_collection_root.png"); // NOI18N
    private static final Icon childIcon = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_image_collection_child.png"); // NOI18N

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
        int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
        setIcon(leaf ? childIcon : rootIcon);
        return this;
    }
}
