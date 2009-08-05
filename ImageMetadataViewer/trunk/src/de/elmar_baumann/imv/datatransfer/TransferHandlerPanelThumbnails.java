package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Handler for <strong>copying</strong> or <strong>moving</strong> a list of
 * thumbnails. The filenames of the thumbnails will be transferred as
 * <code>DataFlavor.stringFlavor</code>, each filename is separated by 
 * {@link #DELIMITER}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-24
 */
public final class TransferHandlerPanelThumbnails extends TransferHandler {

    /**
     * Delimiter between the filenames in the transfered string.
     */
    static final String DELIMITER = "\n"; // NOI18N
    private static final Map<String, Column> COLUMN_OF_PREFIX =
            new HashMap<String, Column>();
    private static final List<Content> CONTENT_IS_A_FILESYSTEM_DIRECTORY =
            new ArrayList<Content>();

    static {
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(Content.DIRECTORY);
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(Content.FAVORITE);

        COLUMN_OF_PREFIX.put(TransferHandlerDragListItemsString.PREFIX_KEYWORDS,
                ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMN_OF_PREFIX.put(
                TransferHandlerDragListItemsString.PREFIX_CATEGORIES,
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
    }

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor) &&
                checkMetadata(transferSupport);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        List<String> filenames = FileUtil.getAsFilenames(
                ((ImageFileThumbnailsPanel) c).getSelectedFiles());
        return TransferUtil.getStringListTransferable(filenames, DELIMITER);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final TransferSupport transferSupport) {
        final ImageFileThumbnailsPanel panel =
                (ImageFileThumbnailsPanel) transferSupport.getComponent();
        if (panel.getSelectionCount() > 0) {
            if (!insertMetadata(transferSupport) && isImageCollection(
                    transferSupport)) {
                moveSelectedImages(transferSupport, panel);
            }
            return true;
        }
        return false;
    }

    public boolean isImageCollection(final TransferSupport transferSupport) {
        Component c = transferSupport.getComponent();
        if (c instanceof ImageFileThumbnailsPanel) {
            return ((ImageFileThumbnailsPanel) c).getContent().equals(
                    Content.IMAGE_COLLECTION);
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore
    }

    private void moveSelectedImages(TransferSupport transferSupport,
            ImageFileThumbnailsPanel panel) {
        Point dropPoint = transferSupport.getDropLocation().getDropPoint();
        panel.moveSelectedToIndex(panel.getDropIndex(dropPoint.x, dropPoint.y));
        String imageCollectionName = getImageCollectionName();
        if (imageCollectionName != null) {
            DatabaseImageCollections.INSTANCE.insertImageCollection(
                    imageCollectionName,
                    FileUtil.getAsFilenames(panel.getFiles()));
        }
    }

    private String getImageCollectionName() {
        JList listImageCollections = GUI.INSTANCE.getAppPanel().
                getListImageCollections();
        Object element = null;
        int index = listImageCollections.getSelectedIndex();
        if (index >= 0) {
            element = listImageCollections.getModel().getElementAt(index);
        }
        return element == null
               ? null
               : element.toString();
    }

    private boolean checkMetadata(TransferSupport transferSupport) {
        Component c = transferSupport.getComponent();
        if (c instanceof ImageFileThumbnailsPanel) {
            Transferable t = transferSupport.getTransferable();
            String string = null;
            DefaultMutableTreeNode node = null;
            try {
                Object transferData = t.getTransferData(DataFlavor.stringFlavor);
                if (transferData instanceof String) {
                    string = (String) transferData;
                } else if (transferData instanceof DefaultMutableTreeNode) {
                    node = (DefaultMutableTreeNode) transferData;
                }
            } catch (Exception ex) {
                AppLog.logSevere(getClass(), ex);
            }
            if (node != null &&
                    node.getUserObject() instanceof HierarchicalKeyword &&
                    isDropOverSelectedThumbnail(transferSupport))
                return true;
            if (string == null) return false;
            if (!TransferHandlerDragListItemsString.startsWithPrefix(string))
                return true;
            if (!GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray().
                    isEditable()) return false;
            return isDropOverSelectedThumbnail(transferSupport);
        }
        return true;
    }

    private boolean insertMetadata(TransferSupport transferSupport) {
        if (!GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray().isEditable())
            return true;
        Transferable t = transferSupport.getTransferable();
        String string = null;
        DefaultMutableTreeNode node = null;
        try {
            Object transferData = t.getTransferData(DataFlavor.stringFlavor);
            if (transferData instanceof String) {
                string = (String) transferData;
            } else if (transferData instanceof DefaultMutableTreeNode) {
                node = (DefaultMutableTreeNode) transferData;
            }
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
            return false;
        }

        if (string != null) {
            importString(string);
        } else if (node != null) {
            HierarchicalKeywordsHelper.addKeywordsToEditPanel(node);
        }
        return true;
    }

    public void importString(String string) {
        if (!isKeywordsOrCategoriesString(string)) return;
        StringTokenizer tokenizer =
                new StringTokenizer(string,
                TransferHandlerDragListItemsString.DELIMITER);
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray();
        Column column = COLUMN_OF_PREFIX.get(getPrefix(string));
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!isKeywordsOrCategoriesString(token)) {
                editPanels.addText(column, token);
            }
        }
    }

    private String getPrefix(String s) {
        return s.substring(
                0, s.indexOf(TransferHandlerDragListItemsString.DELIMITER));
    }

    private boolean isKeywordsOrCategoriesString(String s) {
        return isKeywordsString(s) || isCategoriesString(s);
    }

    private boolean isKeywordsString(String s) {
        return s.startsWith(TransferHandlerDragListItemsString.PREFIX_KEYWORDS);
    }

    private boolean isCategoriesString(String s) {
        return s.startsWith(TransferHandlerDragListItemsString.PREFIX_CATEGORIES);
    }

    public boolean isDropOverSelectedThumbnail(TransferSupport transferSupport) {
        Point p = transferSupport.getDropLocation().getDropPoint();
        ImageFileThumbnailsPanel panel =
                (ImageFileThumbnailsPanel) transferSupport.getComponent();
        return panel.isSelected(panel.getDropIndex(p.x, p.y));
    }
}
