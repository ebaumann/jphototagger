package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.model.TreeModelMiscMetadata;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 * Renders items and text for
 * {@link de.elmar_baumann.imv.model.TreeModelMiscMetadata} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeCellRendererMiscMetadata extends DefaultTreeCellRenderer {

    private static final ImageIcon ICON_MISC_METADATA = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_misc_metadata.png"); // NOI18N
    private static final ImageIcon ICON_EXIF = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_exif.png"); // NOI18N
    private static final ImageIcon ICON_XMP = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_xmp.png"); // NOI18N
    private static final ImageIcon ICON_DETAIL =
            IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_misc_metadata_detail.png"); // NOI18N
    private static final Map<Column, ImageIcon> ICON_OF_COLUMN =
            new HashMap<Column, ImageIcon>();

    static {
        for (Column exifColumn : TreeModelMiscMetadata.getExifColumns()) {
            ICON_OF_COLUMN.put(exifColumn, ICON_EXIF);
        }
        for (Column xmpColumn : TreeModelMiscMetadata.getXmpColumns()) {
            ICON_OF_COLUMN.put(xmpColumn, ICON_XMP);
        }
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
        setIcon(userObject, parentNode, (TreeNode) tree.getModel().getRoot(),
                leaf);
        setText(userObject, parentUserObject);

        return this;
    }

    private void setIcon(Object userObject, DefaultMutableTreeNode parentNode,
            TreeNode root, boolean leaf) {
        if (userObject instanceof Column) {
            setColumnIcon((Column) userObject);
        } else if (leaf) {
            setIcon(ICON_DETAIL);
        } else if (parentNode != null && parentNode.equals(root)) {
            setIcon(ICON_MISC_METADATA);
        }
    }

    private void setColumnIcon(Column column) {
        if (column != null) {
            ImageIcon icon = ICON_OF_COLUMN.get(column);
            assert icon != null : "No icon defined for column: " + column; // NOI18N
            if (icon != null) {
                setIcon(icon);
            }
        }
    }

    private void setText(Object userObject, Object parenUObject) {
        if (userObject == null) return;
        DecimalFormat shortFormat = new DecimalFormat("#"); // NOI18N
        DecimalFormat doubleFormat = new DecimalFormat("#.#"); // NOI18N
        if (userObject instanceof Column) {
            Column col = (Column) (userObject);
            setText(col.getDescription());
        } else if (userObject instanceof String) {
            setText((String) userObject);
        } else if (userObject instanceof Short || userObject instanceof Long) {
            setText(shortFormat.format(userObject));
        } else if (userObject instanceof Double) {
            setText(doubleFormat.format(userObject) + getTextPostfix(
                    parenUObject));
        } else {
            assert false : "Unrecognized data type: " + // NOI18N
                    userObject.getClass().getName();
        }
    }

    private String getTextPostfix(Object userObject) {
        if (userObject instanceof Column) {
            Column column = (Column) userObject;
            if (column.equals(ColumnExifFocalLength.INSTANCE)) {
                return " mm"; // NOI18N
            }
        }
        return ""; // NOI18N
    }
}
