package org.jphototagger.program.module.thumbnails;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;

import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.datatransfer.DataTransferSupport;
import org.jphototagger.program.datatransfer.Flavor;
import org.jphototagger.program.module.favorites.FavoritesUtil;
import org.jphototagger.program.module.filesystem.FilesystemImageUtil;
import org.jphototagger.program.module.filesystem.FilesystemImageUtil.ConfirmOverwrite;
import org.jphototagger.program.module.keywords.KeywordsUtil;
import org.jphototagger.program.module.miscmetadata.MiscMetadataUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.ViewUtil;

/**
 * Handler for <strong>copying</strong> or <strong>moving</strong> a list of
 * thumbnails from the {@code ThumbnailsPanel}.
 *
 * The selected files will be transferred as
 * {@code DataFlavor#javaFileListFlavor}.
 *
 * @author Elmar Baumann
 */
public final class ThumbnailsPanelTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean canImport(TransferSupport support) {
        ThumbnailsPanel tnPanel = (ThumbnailsPanel) support.getComponent();

        return isMetadataDrop(support) || isImageCollection(tnPanel)
                || (!support.isDataFlavorSupported(Flavor.THUMBNAILS_PANEL) && canImportFiles(tnPanel)
                && Flavor.hasFiles(support.getTransferable()));
    }

    private boolean canImportFiles(ThumbnailsPanel tnPanel) {
        return ContentUtil.isSingleDirectoryContent(tnPanel.getOriginOfDisplayedThumbnails());
    }

    private boolean isMetadataDrop(TransferSupport support) {
        return isThumbnailPos(support) && Flavor.isMetadataTransferred(support.getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new TransferableObject(FilesystemImageUtil.addSidecarFiles(((ThumbnailsPanel) c).getSelectedFiles()),
                getFlavors(c));
    }

    private java.awt.datatransfer.DataFlavor[] getFlavors(JComponent c) {
        return ((ThumbnailsPanel) c).getOriginOfDisplayedThumbnails().equals(OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION)
                ? new java.awt.datatransfer.DataFlavor[]{Flavor.THUMBNAILS_PANEL, Flavor.FILE_LIST_FLAVOR,
                    Flavor.URI_LIST, Flavor.IMAGE_COLLECTION}
                : new java.awt.datatransfer.DataFlavor[]{Flavor.THUMBNAILS_PANEL, Flavor.FILE_LIST_FLAVOR,
                    Flavor.URI_LIST};
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

        if (tnPanel.isAFileSelected() && isImageCollection(tnPanel)) {
            moveSelectedImages(support, tnPanel);

            return true;
        }

        if (importFiles(getCurrentDirectory(), support)) {
            tnPanel.refresh();

            return true;
        }

        return false;
    }

    private boolean isImageCollection(ThumbnailsPanel panel) {
        if (panel == null) {
            throw new NullPointerException("panel == null");
        }

        return panel.getOriginOfDisplayedThumbnails().equals(OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION);
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore, moving removes files from source directory
    }

    private void moveSelectedImages(TransferSupport support, ThumbnailsPanel panel) {
        Point dropPoint = support.getDropLocation().getDropPoint();

        panel.moveSelectedToIndex(panel.getImageMoveDropIndex(dropPoint.x, dropPoint.y));

        String imageCollectionName = getImageCollectionName();
        ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

        if (imageCollectionName != null) {
            repo.saveImageCollection(imageCollectionName, panel.getFiles());
        }
    }

    private String getImageCollectionName() {
        JXList listImageCollections = GUI.getAppPanel().getListImageCollections();
        Object selElement = null;
        int selectedIndex = listImageCollections.getSelectedIndex();

        if (selectedIndex >= 0) {
            int modelIndex = listImageCollections.convertIndexToModel(selectedIndex);

            selElement = listImageCollections.getModel().getElementAt(modelIndex);
        }

        return (selElement == null)
                ? null
                : selElement.toString();
    }

    private boolean insertMetadata(TransferSupport support) {
        Transferable t = support.getTransferable();
        boolean dropOverSelectedThumbnail = isDropOverSelectedThumbnail(support);
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();
        File imageFile = getImageFile(support);
        boolean selected = panel.getSelectionCount() > 0;

        if (Flavor.hasKeywordsFromList(support)) {
            insertKeywords(t, dropOverSelectedThumbnail, imageFile);
        } else if (Flavor.hasKeywordsFromTree(support)) {
            insertHierarchicalKeywords(t, dropOverSelectedThumbnail, imageFile);
        } else if (Flavor.hasMetadataTemplate(support)) {
            insertTemplates(selected, support);
        } else if (Flavor.hasMetaDataValue(support)) {
            importMetaDataValueData(support, dropOverSelectedThumbnail, imageFile);
        } else {
            return false;
        }

        return true;
    }

    private void insertKeywords(Transferable t, boolean dropOverSelectedThumbnail, File imageFile) {
        importStrings(Flavor.KEYWORDS_LIST, DataTransferSupport.getKeywords(t), dropOverSelectedThumbnail, imageFile);
    }

    private void insertHierarchicalKeywords(Transferable t, boolean dropOverSelectedThumbnail, File imageFile) {
        List<String> keywords = new ArrayList<String>();

        for (DefaultMutableTreeNode node : DataTransferSupport.getKeywordNodes(t)) {
            if (dropOverSelectedThumbnail) {
                KeywordsUtil.addKeywordsToEditPanel(node);
            } else {
                keywords.addAll(KeywordsUtil.getKeywordStrings(node, true));
            }
        }

        if (!keywords.isEmpty()) {
            KeywordsUtil.saveKeywordsToImageFile(keywords, imageFile);
        }
    }

    private void insertTemplates(boolean selected, TransferSupport support) {
        if (selected) {
            importMetadataTemplate(support);
        } else {
            errorMessageSelCount();
        }
    }

    private void importStrings(DataFlavor dataFlavor, Object[] strings, boolean dropOverSelectedThumbnail,
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
            SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);
            MetaDataValue column = dataFlavor.equals(Flavor.KEYWORDS_LIST)
                    ? XmpDcSubjectsSubjectMetaDataValue.INSTANCE
                    : null;

            for (String keyword : keywords) {
                editor.setOrAddText(column, keyword);
            }
        } else {
            KeywordsUtil.saveKeywordsToImageFile(keywords, imageFile);
        }
    }

    private boolean isDropOverSelectedThumbnail(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        Point p = support.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();

        return panel.isSelectedAtIndex(panel.getImageMoveDropIndex(p.x, p.y));
    }

    private boolean isThumbnailPos(TransferSupport support) {
        Point p = support.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();
        int index = panel.getThumbnailIndexAtPoint(p.x, p.y);

        return (panel.isIndex(index));
    }

    private File getImageFile(TransferSupport support) {
        Point p = support.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) support.getComponent();
        int index = panel.getThumbnailIndexAtPoint(p.x, p.y);

        if (panel.isIndex(index)) {
            return panel.getFileAtIndex(index);
        } else {
            return null;
        }
    }

    private boolean importFiles(File targetDir, TransferSupport support) {
        if (targetDir == null) {
            return false;
        }

        List<File> srcFiles = TransferUtil.getFiles(support.getTransferable(), FilenameDelimiter.EMPTY);
        int dropAction = support.getDropAction();

        if (dropAction == TransferHandler.COPY) {
            FilesystemImageUtil.copyImageFiles(FileFilterUtil.getImageFiles(srcFiles), targetDir, ConfirmOverwrite.YES);

            return true;
        } else if (dropAction == TransferHandler.MOVE) {
            FilesystemImageUtil.moveImageFiles(FileFilterUtil.getImageFiles(srcFiles), targetDir, ConfirmOverwrite.YES);

            return true;
        }

        return false;
    }

    private File getCurrentDirectory() {
        JTree treeDirectories = GUI.getAppPanel().getTreeDirectories();
        JTree treeFavorites = GUI.getAppPanel().getTreeFavorites();

        if (treeDirectories.getSelectionCount() > 0) {
            return ViewUtil.getSelectedFile(treeDirectories);
        } else if (treeFavorites.getSelectionCount() > 0) {
            return FavoritesUtil.getSelectedFavorite();
        }

        return null;
    }

    private void importMetadataTemplate(TransferSupport support) {
        try {
            Object[] selTemplates = (Object[]) support.getTransferable().getTransferData(Flavor.METADATA_TEMPLATES);

            if (selTemplates == null) {
                return;
            }

            assert selTemplates.length == 1;
            SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);
            editor.setMetadataTemplate((MetadataTemplate) selTemplates[0]);
        } catch (Exception ex) {
            Logger.getLogger(ThumbnailsPanelTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void importMetaDataValueData(TransferSupport support, boolean dropOverSelectedThumbnail, File imageFile) {
        try {
            List<MetaDataValueData> mdValueData = (List<MetaDataValueData>) support.getTransferable().getTransferData(Flavor.META_DATA_VALUE);

            if (dropOverSelectedThumbnail) {
                if (!ViewUtil.checkSelImagesEditable(true)) {
                    return;
                }

                SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

                for (MetaDataValueData data : mdValueData) {
                    editor.setOrAddText(data.getMetaDataValue(), (String) data.getData());
                }
            } else {
                MiscMetadataUtil.saveToImageFile(mdValueData, imageFile);
            }
        } catch (Exception ex) {
            Logger.getLogger(ThumbnailsPanelTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void errorMessageSelCount() {
        String message = Bundle.getString(ThumbnailsPanelTransferHandler.class, "ThumbnailsPanelTransferHandler.Error.NoSelection");

        MessageDisplayer.error(null, message);
    }
}
