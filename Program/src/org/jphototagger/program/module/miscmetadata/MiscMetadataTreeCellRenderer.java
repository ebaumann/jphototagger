package org.jphototagger.program.module.miscmetadata;

import org.jphototagger.program.module.search.MetaDataValueIcons;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.TreeCellRendererExt;

/**
 * @author Elmar Baumann
 */
public final class MiscMetadataTreeCellRenderer extends TreeCellRendererExt {

    private static final ImageIcon ICON_MISC_METADATA = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_misc_metadata.png");
    private static final ImageIcon ICON_EXIF = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_exif.png");
    private static final ImageIcon ICON_XMP = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_xmp.png");
    private static final ImageIcon ICON_DETAIL = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_misc_metadata_detail.png");
    private static final Map<MetaDataValue, ImageIcon> ICON_OF_META_DATA_VALUE = new HashMap<MetaDataValue, ImageIcon>();
    private static final long serialVersionUID = 1L;

    static {
        for (MetaDataValue exifMetaDataValue : MiscMetadataTreeModel.getExifMetaDataValues()) {
            ICON_OF_META_DATA_VALUE.put(exifMetaDataValue, ICON_EXIF);
        }

        for (MetaDataValue xmpMetaDataValue : MiscMetadataTreeModel.getXmpMetaDataValues()) {
            ICON_OF_META_DATA_VALUE.put(xmpMetaDataValue, ICON_XMP);
        }
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        Object userObject = node.getUserObject();
        Object parentUserObject = (parentNode == null) ? null : parentNode.getUserObject();
        int tempSelRow = getTempSelectionRow();
        boolean tempSelRowIsSelected = tempSelRow < 0 ? false : tree.isRowSelected(tempSelRow);

        setIcon(userObject, parentUserObject, parentNode, (TreeNode) tree.getModel().getRoot(), leaf);
        setText(userObject, parentUserObject);
        setColors(row, selected, tempSelRowIsSelected);

        return this;
    }

    private void setIcon(Object userObject, Object parentUserObject, DefaultMutableTreeNode parentNode, TreeNode root,
            boolean leaf) {
        if (userObject instanceof MetaDataValue) {
            setMetaDataValueIcon((MetaDataValue) userObject);
        } else if (leaf) {
            Icon iconDetail = getIconDetail(parentUserObject);

            setIcon((iconDetail == null)
                    ? ICON_DETAIL
                    : iconDetail);
        } else if ((parentNode != null) && parentNode.equals(root)) {
            setIcon(ICON_MISC_METADATA);
        }
    }

    private Icon getIconDetail(Object userObject) {
        if (userObject instanceof MetaDataValue) {
            return MetaDataValueIcons.getIcon((MetaDataValue) userObject);
        }

        return null;
    }

    private void setMetaDataValueIcon(MetaDataValue metaDataValue) {
        if (metaDataValue != null) {
            ImageIcon icon = ICON_OF_META_DATA_VALUE.get(metaDataValue);

            assert icon != null : "No icon defined for metadata value: " + metaDataValue;

            if (icon != null) {
                setIcon(icon);
            }
        }
    }

    private void setText(Object userObject, Object parentUserObject) {
        if (userObject == null) {
            return;
        }

        DecimalFormat shortFormat = new DecimalFormat("#");
        DecimalFormat doubleFormat = new DecimalFormat("#.#");

        if (userObject instanceof MetaDataValue) {
            MetaDataValue col = (MetaDataValue) (userObject);

            setText(col.getDescription());
        } else if (userObject instanceof String) {
            setText((String) userObject);
        } else if ((userObject instanceof Short) || (userObject instanceof Long)) {
            setText(shortFormat.format(userObject));
        } else if (userObject instanceof Double) {
            setText(doubleFormat.format(userObject) + getTextPostfix(parentUserObject));
        } else if (userObject instanceof Long) {
            setText(Long.toString((Long) userObject) + getTextPostfix(parentUserObject));
        } else {
            assert false : "Unrecognized data type: " + userObject.getClass().getName();
        }
    }

    private String getTextPostfix(Object userObject) {
        if (userObject instanceof MetaDataValue) {
            MetaDataValue metaDataValue = (MetaDataValue) userObject;

            if (metaDataValue.equals(ExifFocalLengthMetaDataValue.INSTANCE)) {
                return " mm";
            } else if (metaDataValue.equals(XmpRatingMetaDataValue.INSTANCE)) {
                return Bundle.getString(MiscMetadataTreeCellRenderer.class, "MiscMetadataTreeCellRenderer.PostfixXmpRatingMetaDataValue");
            }
        }

        return "";
    }

    // TreeItemTempSelectionRowSetter calls this reflective not if only in super class defined
    @Override
    public void setTempSelectionRow(int index) {
        super.setTempSelectionRow(index);
    }
}
