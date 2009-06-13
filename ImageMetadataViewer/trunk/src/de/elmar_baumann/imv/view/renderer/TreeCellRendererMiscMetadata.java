package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.model.TreeModelMiscMetadata;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for
 * {@link de.elmar_baumann.imv.model.TreeModelMiscMetadata} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeCellRendererMiscMetadata extends DefaultTreeCellRenderer {

    private static final ImageIcon iconMiscMetadata = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_misc_metadata.png");
    private static final ImageIcon iconExif = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_exif.png");
    private static final Map<Column, ImageIcon> iconOfColumn =
            new HashMap<Column, ImageIcon>();


    static {
        iconOfColumn.put(ColumnExifFocalLength.INSTANCE, iconExif);
        iconOfColumn.put(ColumnExifIsoSpeedRatings.INSTANCE, iconExif);
        iconOfColumn.put(ColumnExifRecordingEquipment.INSTANCE, iconExif);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.
                getParent();
        Object userObject = node.getUserObject();
        Object parentUserObject = parentNode == null
                                  ? null
                                  : parentNode.getUserObject();
        if (userObject instanceof Column || parentUserObject != null &&
                parentUserObject instanceof Column) {
            Column column = (Column) (leaf
                                      ? parentUserObject
                                      : userObject);
            ImageIcon icon = iconOfColumn.get(column);
            assert icon != null : "No icon defined for column: " + column;
            if (icon != null) {
                setIcon(icon);
            }
            setText(leaf ? userObject.toString() : column.getDescription());
        } else if (parentNode != null && parentNode.equals(tree.getModel().
                getRoot())) {
            setIcon(iconMiscMetadata);
        }

        return this;
    }
}
