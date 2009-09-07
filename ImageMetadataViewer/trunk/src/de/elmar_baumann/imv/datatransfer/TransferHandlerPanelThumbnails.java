package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.imv.io.ImageUtil;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.types.ContentUtil;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.datatransfer.TransferableFileCollection;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Handler for <strong>copying</strong> or <strong>moving</strong> a list of
 * thumbnails from the {@link ThumbnailsPanel}.
 *
 * The selected files will be transferred as
 * {@link DataFlavor#javaFileListFlavor}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-24
 */
public final class TransferHandlerPanelThumbnails extends TransferHandler {

    private static final Map<String, Column> COLUMN_OF_PREFIX =
            new HashMap<String, Column>();

    static {

        COLUMN_OF_PREFIX.put(TransferHandlerDragListItemsString.PREFIX_KEYWORDS,
                ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMN_OF_PREFIX.put(
                TransferHandlerDragListItemsString.PREFIX_CATEGORIES,
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
    }

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        Component component = transferSupport.getComponent();
        return isImageFilePanel(component) &&
                metadataTransferred(transferSupport) ||
                isImageCollection((ThumbnailsPanel) component) ||
                canImportFiles(component) &&
                Flavors.filesTransfered(transferSupport.getTransferable());
    }

    private boolean isImageFilePanel(Component component) {
        return component instanceof ThumbnailsPanel;
    }

    private boolean canImportFiles(Component component) {
        return ContentUtil.isSingleDirectoryContent(
                ((ThumbnailsPanel) component).getContent());
    }

    private File getCurrentDirectory() {
        JTree treeDirectories = GUI.INSTANCE.getAppPanel().getTreeDirectories();
        JTree treeFavorites = GUI.INSTANCE.getAppPanel().getTreeFavorites();
        if (treeDirectories.getSelectionCount() > 0) {
            return ViewUtil.getSelectedFile(treeDirectories);
        } else if (treeFavorites.getSelectionCount() > 0) {
            return ViewUtil.getSelectedDirectoryFromFavoriteDirectories();
        }
        return null;
    }

    private boolean metadataTransferred(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor) &&
                containsMetadata(transferSupport);
    }

    private boolean containsMetadata(TransferSupport transferSupport) {

        String string = null;
        DefaultMutableTreeNode node = null;
        try {
            Object transferData = transferSupport.getTransferable().
                    getTransferData(DataFlavor.stringFlavor);
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

    @Override
    protected Transferable createTransferable(JComponent c) {

        assert c instanceof ThumbnailsPanel :
                "Not an ThumbnailsPanel: " + c; // NOI18N

        return new TransferableFileCollection(ImageUtil.addSidecarFiles(
                ((ThumbnailsPanel) c).getSelectedFiles()));
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    // Possible drop actions:
    //
    // * Drag from external application, e.g. a file manager
    // * Drag whithin the thumbnails panel to order images (image collections)
    // * Drag from another window whitin this application
    //
    // Bugs:
    //   * If images are selected files can nether be imported from external
    //     apps
    //   * Dragged images of an image collection are only reordered if the data
    //     flavor is not stringFlavor. This can be ensured throug this class
    //     in #createTransferable()
    //
    // The problem, to decide what to do, should be used in future releases
    // through unique (separate) data flavors whithin this application
    //
    @Override
    public boolean importData(TransferSupport transferSupport) {

        if (!transferSupport.isDrop()) return false;

        ThumbnailsPanel panel =
                (ThumbnailsPanel) transferSupport.getComponent();
        boolean imagesSelected = panel.getSelectionCount() > 0;
        boolean hasStringFlavor =
                transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);

        if (!imagesSelected && hasStringFlavor && maybeMetadata(transferSupport)) {
            errorMessageMetadataNoImagesSelected();
            return false;
        }

        if (imagesSelected && hasStringFlavor && insertMetadata(transferSupport))
            return true;

        if (imagesSelected && isImageCollection(panel)) {
            moveSelectedImages(transferSupport, panel);
            return true;
        }

        if (importFiles(getCurrentDirectory(), transferSupport)) {
            panel.refresh();
            return true;
        }

        return false;
    }

    public boolean isImageCollection(ThumbnailsPanel panel) {
        return panel.getContent().equals(Content.IMAGE_COLLECTION);
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore, moving removes files from source directory
    }

    private void moveSelectedImages(
            TransferSupport transferSupport, ThumbnailsPanel panel) {
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

    private boolean maybeMetadata(TransferSupport transferSupport) {
        Transferable t = transferSupport.getTransferable();
        try {
            Object transferData = t.getTransferData(DataFlavor.stringFlavor);
            return transferData instanceof String ||
                    transferData instanceof DefaultMutableTreeNode;
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
        }
        return false;
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
        ThumbnailsPanel panel =
                (ThumbnailsPanel) transferSupport.getComponent();
        return panel.isSelected(panel.getDropIndex(p.x, p.y));
    }

    private boolean importFiles(File targetDir, TransferSupport transferSupport) {
        if (targetDir == null) return false;
        List<File> srcFiles =
                TransferUtil.getFiles(transferSupport.getTransferable(), ""); // NOI18N
        int dropAction = transferSupport.getDropAction();
        if (dropAction == TransferHandler.COPY) {
            ImageUtil.copyImageFiles(
                    ImageUtil.getImageFiles(srcFiles), targetDir, true);
            return true;
        } else if (dropAction == TransferHandler.MOVE) {
            ImageUtil.moveImageFiles(
                    ImageUtil.getImageFiles(srcFiles), targetDir, true);
            return true;
        }
        return false;
    }

    private void errorMessageMetadataNoImagesSelected() {
        MessageDisplayer.error(GUI.INSTANCE.getAppPanel(),
                "TransferHandlerPanelThumbnails.Error.MessageMetadataNoImagesSelected");
    }
}
