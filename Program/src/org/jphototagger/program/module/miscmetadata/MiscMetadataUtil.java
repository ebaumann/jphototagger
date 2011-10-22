package org.jphototagger.program.module.miscmetadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.MetaDataStringValue;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.misc.SaveXmp;
import org.jphototagger.program.module.filesystem.FilesystemImageUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.xmp.XmpMetadata;

/**
 * @author Elmar Baumann
 */
public final class MiscMetadataUtil {

    private static final List<MetaDataValue> XMP_META_DATA_VALUES = XmpMetaDataValues.get();

    public static void saveToImageFiles(List<MetaDataValueData> valueData, Collection<? extends File> imageFiles) {
        if (valueData == null) {
            throw new NullPointerException("values == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFile == null");
        }

        List<FileXmp> saveList = new ArrayList<FileXmp>();

        for (File imageFile : imageFiles) {
            if (imageFile.exists() && FilesystemImageUtil.checkImageEditable(imageFile)) {
                Xmp xmp = null;

                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(MiscMetadataUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (xmp == null) {
                    xmp = new Xmp();
                }

                for (MetaDataValueData valueDate : valueData) {
                    xmp.setValue(valueDate.getMetaDataValue(), valueDate.getData());
                }

                saveList.add(new FileXmp(imageFile, xmp));
            }
        }

        if (!saveList.isEmpty()) {
            SaveXmp.save(saveList);
        }
    }

    public static void saveToImageFile(List<MetaDataValueData> colData, File imageFile) {
        if (colData == null) {
            throw new NullPointerException("colData == null");
        }

        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        saveToImageFiles(colData, Collections.singleton(imageFile));
    }

    public static void addMetadataToSelectedImages(final Collection<? extends DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        final List<MetaDataValue> xmpMetaDataValues = XmpMetaDataValues.get();

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

                if (!editor.isEditable()) {
                    return;
                }

                for (DefaultMutableTreeNode node : nodes) {
                    if (isParentUserObjectAMetaDataValue(node, xmpMetaDataValues)) {
                        String text = (String) node.getUserObject();
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                        MetaDataValue mdValue = (MetaDataValue) parent.getUserObject();

                        editor.setOrAddText(mdValue, text);
                    }
                }
            }
        });
    }

    public static void removeMetadataFromSelectedImages(final Collection<? extends DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        final List<MetaDataValue> xmpMetaDataValues = XmpMetaDataValues.get();

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

                if (!editor.isEditable()) {
                    return;
                }

                for (DefaultMutableTreeNode node : nodes) {
                    if (isParentUserObjectAMetaDataValue(node, xmpMetaDataValues)) {
                        String text = (String) node.getUserObject();
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                        MetaDataValue mdValue = (MetaDataValue) parent.getUserObject();

                        editor.removeText(mdValue, text);
                    }
                }
            }
        });
    }

    public static void addMetadataToSelectedImages(final MetaDataValue mdValue, final String text) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

                if (editor.isEditable()) {
                    editor.setOrAddText(mdValue, text);
                }
            }
        });
    }

    public static void removeMetadataFromSelectedImages(final MetaDataValue mdValue, final String text) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

                if (editor.isEditable()) {
                    editor.removeText(mdValue, text);
                }
            }
        });
    }

    /**
     * Returns wether the parent's user object of a specific node is a metadata value
     * contained in a collection of medata values.
     *
     * @param  node    node
     * @param  mdValues
     * @return         true if the parent's user object is a value contained in
     *                 <code>mdValues</code>
     */
    public static boolean isParentUserObjectAMetaDataValue(DefaultMutableTreeNode node, Collection<? extends MetaDataValue> mdValues) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        if (mdValues == null) {
            throw new NullPointerException("mdValues == null");
        }

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        Object userObject = parent.getUserObject();

        if (userObject instanceof MetaDataValue) {
            return mdValues.contains((MetaDataValue) userObject);
        }

        return false;
    }

    private static DefaultTreeModel getModel() {
        return (DefaultTreeModel) GUI.getAppPanel().getTreeMiscMetadata().getModel();
    }

    /**
     * Returns the first node with a specific metadata value as user object.
     *
     * @param  mdValue
     * @return        node with that value as user object
     */
    public static DefaultMutableTreeNode findNodeContains(MetaDataValue mdValue) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();

        for (Enumeration<?> e = root.depthFirstEnumeration(); e.hasMoreElements();) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Object userObject = node.getUserObject();

            if ((userObject instanceof MetaDataValue) && ((MetaDataValue) userObject).equals(mdValue)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Removes from the model a node with a string value as user object from a
     * parent containing a specific metadata value as user object.
     *
     * @param mdValue value of parent
     * @param value  value of child
     */
    public static void removeChildValueFrom(final MetaDataValue mdValue, final String value) {
        if (mdValue == null) {
            throw new NullPointerException("value == null");
        }

        if (value == null) {
            throw new NullPointerException("value == null");
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                DefaultMutableTreeNode node = findNodeContains(mdValue);

                if (node != null) {
                    int count = node.getChildCount();

                    for (int i = 0; i < count; i++) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                        Object uo = (childNode).getUserObject();

                        if ((uo instanceof String) && ((String) uo).equals(value)) {
                            getModel().removeNodeFromParent(childNode);

                            return;
                        }
                    }
                }
            }
        });
    }

    /**
     *
     * @param  paths
     * @return       values or empty list
     */
    public static List<MetaDataStringValue> getColValuesFrom(List<TreePath> paths) {
        if (paths == null) {
            throw new NullPointerException("paths == null");
        }

        List<MetaDataStringValue> values = new ArrayList<MetaDataStringValue>(paths.size());

        for (TreePath path : paths) {
            MetaDataStringValue value = getColValueFrom(path);

            if (value != null) {
                values.add(value);
            }
        }

        return values;
    }

    /**
     *
     * @param  path
     * @return       value or null
     */
    public static MetaDataStringValue getColValueFrom(TreePath path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

        return getColValueFrom(node);
    }

    /**
     *
     * @param  node
     * @return       value or null
     */
    public static MetaDataStringValue getColValueFrom(DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        if (isParentUserObjectAMetaDataValue(node, XMP_META_DATA_VALUES)) {
            String value = node.getUserObject().toString();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            MetaDataValue mdValue = (MetaDataValue) parentNode.getUserObject();

            return new MetaDataStringValue(mdValue, value);
        }

        return null;
    }

    private MiscMetadataUtil() {
    }
}
