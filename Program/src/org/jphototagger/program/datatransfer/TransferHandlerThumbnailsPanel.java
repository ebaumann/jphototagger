package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.ColumnData;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.helper.FavoritesHelper;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.io.ImageUtil;
import org.jphototagger.program.io.ImageUtil.ConfirmOverwrite;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.types.ContentUtil;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.ViewUtil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.Point;

import java.io.File;

import java.util.ArrayList;
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
 * @author Elmar Baumann
 */
public final class TransferHandlerThumbnailsPanel extends TransferHandler {
    private static final long serialVersionUID = 1831860682951562565L;

    @Override
    public boolean canImport(TransferSupport support) {
        ThumbnailsPanel tnPanel = (ThumbnailsPanel) support.getComponent();

        return isMetadataDrop(support) || isImageCollection(tnPanel)
               || (!support.isDataFlavorSupported(Flavor.THUMBNAILS_PANEL)
                   && canImportFiles(tnPanel)
                   && Flavor.hasFiles(support.getTransferable()));
    }

    private boolean canImportFiles(ThumbnailsPanel tnPanel) {
        return ContentUtil.isSingleDirectoryContent(tnPanel.getContent());
    }

    private boolean isMetadataDrop(TransferSupport support) {
        return isThumbnailPos(support)
               && Flavor.isMetadataTransferred(support.getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new TransferableObject(
            ImageUtil.addSidecarFiles(
                ((ThumbnailsPanel) c).getSelectedFiles()), getFlavors(c));
    }

    private java.awt.datatransfer.DataFlavor[] getFlavors(JComponent c) {
        return ((ThumbnailsPanel) c).getContent().equals(
            Content.IMAGE_COLLECTION)
               ? new java.awt.datatransfer.DataFlavor[] {
                   Flavor.THUMBNAILS_PANEL,
                   Flavor.FILE_LIST_FLAVOR, Flavor.URI_LIST,
                   Flavor.IMAGE_COLLECTION }
               : new java.awt.datatransfer.DataFlavor[] {
                   Flavor.THUMBNAILS_PANEL,
                   Flavor.FILE_LIST_FLAVOR, Flavor.URI_LIST };
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        ThumbnailsPanel tnPanel = (ThumbnailsPanel) support.getComponent();

        if (isMetadataDrop(support)) {
            insertMetadata(support);

            return true;
        }

        if (tnPanel.isFileSelected() && isImageCollection(tnPanel)) {
            moveSelectedImages(support, tnPanel);

            return true;
        }

        if (importFiles(getCurrentDirectory(), support)) {
            tnPanel.refresh();

            return true;
        }

        return false;
    }

    public boolean isImageCollection(ThumbnailsPanel panel) {
        if (panel == null) {
            throw new NullPointerException("panel == null");
        }

        return panel.getContent().equals(Content.IMAGE_COLLECTION);
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {

        // ignore, moving removes files from source directory
    }

    private void moveSelectedImages(TransferSupport support,
                                    ThumbnailsPanel panel) {
        Point dropPoint = support.getDropLocation().getDropPoint();

        panel.moveSelectedToIndex(panel.getImageMoveDropIndex(dropPoint.x,
                dropPoint.y));

        String imageCollectionName = getImageCollectionName();

        if (imageCollectionName != null) {
            DatabaseImageCollections.INSTANCE.insert(imageCollectionName,
                    panel.getFiles());
        }
    }

    private String getImageCollectionName() {
        JList listImageCollections =
            GUI.getAppPanel().getListImageCollections();
        Object selElement = null;
        int    selIndex   = listImageCollections.getSelectedIndex();

        if (selIndex >= 0) {
            selElement = listImageCollections.getModel().getElementAt(selIndex);
        }

        return (selElement == null)
               ? null
               : selElement.toString();
    }

    private boolean insertMetadata(TransferSupport support) {
        Transferable t = support.getTransferable();
        boolean      dropOverSelectedThumbnail =
            isDropOverSelectedThumbnail(support);
        ThumbnailsPanel panel     = (ThumbnailsPanel) support.getComponent();
        File            imageFile = getImageFile(support);
        boolean         selected  = panel.getSelectionCount() > 0;

        if (Flavor.hasKeywordsFromList(support)) {
            insertKeywords(t, dropOverSelectedThumbnail, imageFile);
        } else if (Flavor.hasKeywordsFromTree(support)) {
            insertHierarchicalKeywords(t, dropOverSelectedThumbnail, imageFile);
        } else if (Flavor.hasMetadataTemplate(support)) {
            insertTemplates(selected, support);
        } else if (Flavor.hasColumnData(support)) {
            importColumnData(support, dropOverSelectedThumbnail, imageFile);
        } else {
            return false;
        }

        return true;
    }

    private void insertKeywords(Transferable t,
                                boolean dropOverSelectedThumbnail,
                                File imageFile) {
        importStrings(Flavor.KEYWORDS_LIST, Support.getKeywords(t),
                      dropOverSelectedThumbnail, imageFile);
    }

    private void insertHierarchicalKeywords(Transferable t,
            boolean dropOverSelectedThumbnail, File imageFile) {
        List<String> keywords = new ArrayList<String>();

        for (DefaultMutableTreeNode node : Support.getKeywordNodes(t)) {
            if (dropOverSelectedThumbnail) {
                KeywordsHelper.addKeywordsToEditPanel(node);
            } else {
                keywords.addAll(KeywordsHelper.getKeywordStrings(node, true));
            }
        }

        if (!keywords.isEmpty()) {
            KeywordsHelper.saveKeywordsToImageFile(keywords, imageFile);
        }
    }

    private void insertTemplates(boolean selected, TransferSupport support) {
        if (selected) {
            importMetadataTemplate(support);
        } else {
            errorMessageSelCount();
        }
    }

    /**
     *
     * @param dataFlavor
     * @param strings                    can be null
     * @param dropOverSelectedThumbnail
     * @param imageFile
     */
    public void importStrings(DataFlavor dataFlavor, Object[] strings,
                              boolean dropOverSelectedThumbnail,
                              File imageFile) {
        if (dataFlavor == null) {
            throw new NullPointerException("dataFlavor == null");
        }

        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if ((strings == null) || (strings.length <= 0)) {
            return;
        }

        List<String> keywords = new ArrayList<String>(strings.length);

        for (Object o : strings) {
            keywords.add(o.toString());
        }

        if (dropOverSelectedThumbnail) {
            EditMetadataPanels editPanels =
                GUI.getAppPanel().getEditMetadataPanels();
            Column column = dataFlavor.equals(Flavor.KEYWORDS_LIST)
                            ? ColumnXmpDcSubjectsSubject.INSTANCE
                            : null;

            for (String keyword : keywords) {
                editPanels.addText(column, keyword);
            }
        } else {
            KeywordsHelper.saveKeywordsToImageFile(keywords, imageFile);
        }
    }

    public boolean isDropOverSelectedThumbnail(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        Point           p     = support.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();

        return panel.isSelected(panel.getImageMoveDropIndex(p.x, p.y));
    }

    private boolean isThumbnailPos(TransferSupport support) {
        Point           p     = support.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();
        int             index = panel.getThumbnailIndexAtPoint(p.x, p.y);

        return (panel.isIndex(index));
    }

    private File getImageFile(TransferSupport support) {
        Point           p     = support.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();
        int             index = panel.getThumbnailIndexAtPoint(p.x, p.y);

        if (panel.isIndex(index)) {
            return panel.getFile(index);
        } else {
            return null;
        }
    }

    private boolean importFiles(File targetDir, TransferSupport support) {
        if (targetDir == null) {
            return false;
        }

        List<File> srcFiles = TransferUtil.getFiles(support.getTransferable(),
                                  FilenameDelimiter.EMPTY);
        int dropAction = support.getDropAction();

        if (dropAction == TransferHandler.COPY) {
            ImageUtil.copyImageFiles(ImageFileFilterer.getImageFiles(srcFiles),
                                     targetDir, ConfirmOverwrite.YES);

            return true;
        } else if (dropAction == TransferHandler.MOVE) {
            ImageUtil.moveImageFiles(ImageFileFilterer.getImageFiles(srcFiles),
                                     targetDir, ConfirmOverwrite.YES);

            return true;
        }

        return false;
    }

    private File getCurrentDirectory() {
        JTree treeDirectories = GUI.getAppPanel().getTreeDirectories();
        JTree treeFavorites   = GUI.getAppPanel().getTreeFavorites();

        if (treeDirectories.getSelectionCount() > 0) {
            return ViewUtil.getSelectedFile(treeDirectories);
        } else if (treeFavorites.getSelectionCount() > 0) {
            return FavoritesHelper.getSelectedFavorite();
        }

        return null;
    }

    private void importMetadataTemplate(TransferSupport support) {
        try {
            Object[] selTemplates =
                (Object[]) support.getTransferable().getTransferData(
                    Flavor.METADATA_TEMPLATES);

            if (selTemplates == null) {
                return;
            }

            assert selTemplates.length == 1;
            GUI.getAppPanel().getEditMetadataPanels().setMetadataTemplate(
                (MetadataTemplate) selTemplates[0]);
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerThumbnailsPanel.class, ex);
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void importColumnData(TransferSupport support,
                                  boolean dropOverSelectedThumbnail,
                                  File imageFile) {
        try {
            List<ColumnData> colData =
                (List<ColumnData>) support.getTransferable().getTransferData(
                    Flavor.COLUMN_DATA);

            if (dropOverSelectedThumbnail) {
                if (!ViewUtil.checkSelImagesEditable(true)) {
                    return;
                }

                EditMetadataPanels ep =
                    GUI.getAppPanel().getEditMetadataPanels();

                for (ColumnData data : colData) {
                    ep.addText(data.getColumn(), (String) data.getData());
                }
            } else {
                MiscMetadataHelper.saveToImageFile(colData, imageFile);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerThumbnailsPanel.class, ex);
        }
    }

    private void errorMessageSelCount() {
        MessageDisplayer.error(
            null, "TransferHandlerThumbnailsPanel.Error.NoSelection");
    }
}
