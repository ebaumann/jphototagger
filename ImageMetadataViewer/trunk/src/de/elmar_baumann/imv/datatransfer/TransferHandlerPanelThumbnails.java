package de.elmar_baumann.imv.datatransfer;

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
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
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

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        ThumbnailsPanel tnPanel =
                (ThumbnailsPanel) transferSupport.getComponent();
        return metadataTransferred(transferSupport) ||
                isImageCollection(tnPanel) ||
                !transferSupport.isDataFlavorSupported(
                Flavors.THUMBNAILS_PANEL_FLAVOR) &&
                canImportFiles(tnPanel) &&
                Flavors.hasFiles(transferSupport.getTransferable());
    }

    private boolean canImportFiles(ThumbnailsPanel tnPanel) {
        return ContentUtil.isSingleDirectoryContent(tnPanel.getContent());
    }

    private boolean metadataTransferred(TransferSupport transferSupport) {
        return (Flavors.hasCategories(transferSupport) ||
                Flavors.hasKeywords(transferSupport) ||
                Flavors.hasHierarchicalKeywords(transferSupport)) &&
                isDropOverSelectedThumbnail(transferSupport);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new TransferableObject(
                ImageUtil.addSidecarFiles(
                ((ThumbnailsPanel) c).getSelectedFiles()),
                Flavors.THUMBNAILS_PANEL_FLAVOR,
                Flavors.FILE_LIST_FLAVOR,
                Flavors.URI_LIST_FLAVOR);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {

        if (!transferSupport.isDrop()) return false;

        ThumbnailsPanel panel =
                (ThumbnailsPanel) transferSupport.getComponent();
        boolean imagesSelected = panel.getSelectionCount() > 0;

        if (metadataTransferred(transferSupport)) {
            insertMetadata(transferSupport);
            return true;
        }

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

    private boolean insertMetadata(TransferSupport transferSupport) {
        if (!GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray().isEditable())
            return true;
        Transferable t = transferSupport.getTransferable();
        if (Flavors.hasCategories(transferSupport)) {
            importStrings(Flavors.CATEGORIES_FLAVOR, Support.getCategories(t));
        } else if (Flavors.hasKeywords(transferSupport)) {
            importStrings(Flavors.KEYWORDS_FLAVOR, Support.getKeywords(t));
        } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
            List<DefaultMutableTreeNode> nodes =
                    Support.getHierarchicalKeywordsNodes(t);
            for (DefaultMutableTreeNode node : nodes) {
                HierarchicalKeywordsHelper.addKeywordsToEditPanel(node);
            }
        } else {
            return false;
        }
        return true;
    }

    public void importStrings(DataFlavor dataFlavor, Object[] strings) {
        if (strings == null || strings.length <= 0) return;
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray();
        Column column = dataFlavor.equals(Flavors.CATEGORIES_FLAVOR)
                ? ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE
                : dataFlavor.equals(Flavors.KEYWORDS_FLAVOR)
                ? ColumnXmpDcSubjectsSubject.INSTANCE
                : null;
        for (Object string : strings) {
            editPanels.addText(column, string.toString());
        }
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
}
