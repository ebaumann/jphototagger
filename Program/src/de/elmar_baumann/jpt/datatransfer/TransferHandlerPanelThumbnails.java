/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.MetadataEditTemplate;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.jpt.io.ImageUtil;
import de.elmar_baumann.jpt.io.ImageUtil.ConfirmOverwrite;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.types.ContentUtil;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
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

        ThumbnailsPanel tnPanel = (ThumbnailsPanel) transferSupport.getComponent();

        return metadataTransferred(transferSupport) ||
               isImageCollection(tnPanel) ||
               !transferSupport.isDataFlavorSupported(Flavors.THUMBNAILS_PANEL_FLAVOR) &&
               canImportFiles(tnPanel) &&
               Flavors.hasFiles(transferSupport.getTransferable());
    }

    private boolean canImportFiles(ThumbnailsPanel tnPanel) {
        return ContentUtil.isSingleDirectoryContent(tnPanel.getContent());
    }

    private boolean metadataTransferred(TransferSupport transferSupport) {
        final boolean dropOverSelectedThumbnail = isDropOverSelectedThumbnail(transferSupport);
        return (Flavors.hasKeywords(transferSupport) ||
                Flavors.hasHierarchicalKeywords(transferSupport)) && dropOverSelectedThumbnail ||
                Flavors.hasMetadataEditTemplate(transferSupport)  && dropOverSelectedThumbnail;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new TransferableObject(
                ImageUtil.addSidecarFiles(((ThumbnailsPanel) c).getSelectedFiles()),
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

        ThumbnailsPanel panel          = (ThumbnailsPanel) transferSupport.getComponent();
        boolean         imagesSelected = panel.getSelectionCount() > 0;

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

    private void moveSelectedImages(TransferSupport transferSupport, ThumbnailsPanel panel) {

        Point dropPoint = transferSupport.getDropLocation().getDropPoint();

        panel.moveSelectedToIndex(panel.getDnDIndex(dropPoint.x, dropPoint.y));

        String imageCollectionName = getImageCollectionName();

        if (imageCollectionName != null) {
            DatabaseImageCollections.INSTANCE.insertImageCollection(
                    imageCollectionName,
                    FileUtil.getAsFilenames(panel.getFiles()));
        }
    }

    private String getImageCollectionName() {
        JList  listImageCollections = GUI.INSTANCE.getAppPanel().getListImageCollections();
        Object selElement           = null;
        int    selIndex             = listImageCollections.getSelectedIndex();

        if (selIndex >= 0) {
            selElement = listImageCollections.getModel().getElementAt(selIndex);
        }

        return selElement == null
                ? null
                : selElement.toString();
    }

    private boolean insertMetadata(TransferSupport transferSupport) {

        if (!GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray().isEditable()) {
            return true;
        }

        Transferable t = transferSupport.getTransferable();

        if (Flavors.hasKeywords(transferSupport)) {

            importStrings(Flavors.KEYWORDS_FLAVOR, Support.getKeywords(t));

        } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {

            for (DefaultMutableTreeNode node : Support.getHierarchicalKeywordsNodes(t)) {
                HierarchicalKeywordsHelper.addKeywordsToEditPanel(node);
            }
        } else if (Flavors.hasMetadataEditTemplate(transferSupport)) {
            importMetadataEditTemplate(transferSupport);
        } else {
            return false;
        }
        return true;
    }

    public void importStrings(DataFlavor dataFlavor, Object[] strings) {
        if (strings == null || strings.length <= 0) return;

        EditMetadataPanelsArray editPanels = GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray();
        Column                  column     = dataFlavor.equals(Flavors.KEYWORDS_FLAVOR)
                                                ? ColumnXmpDcSubjectsSubject.INSTANCE
                                                : null;
        for (Object string : strings) {
            editPanels.addText(column, string.toString());
        }
    }

    public boolean isDropOverSelectedThumbnail(TransferSupport transferSupport) {

        Point           p     = transferSupport.getDropLocation().getDropPoint();
        ThumbnailsPanel panel = (ThumbnailsPanel) transferSupport.getComponent();

        return panel.isSelected(panel.getDnDIndex(p.x, p.y));
    }

    private boolean importFiles(File targetDir, TransferSupport transferSupport) {

        if (targetDir == null) return false;

        List<File> srcFiles   = TransferUtil.getFiles(transferSupport.getTransferable(), "");
        int        dropAction = transferSupport.getDropAction();

        if (dropAction == TransferHandler.COPY) {
            ImageUtil.copyImageFiles(
                    ImageUtil.getImageFiles(srcFiles), targetDir, ConfirmOverwrite.YES);
            return true;
        } else if (dropAction == TransferHandler.MOVE) {
            ImageUtil.moveImageFiles(
                    ImageUtil.getImageFiles(srcFiles), targetDir, ConfirmOverwrite.YES);
            return true;
        }
        return false;
    }

    private File getCurrentDirectory() {

        JTree treeDirectories = GUI.INSTANCE.getAppPanel().getTreeDirectories();
        JTree treeFavorites   = GUI.INSTANCE.getAppPanel().getTreeFavorites();

        if (treeDirectories.getSelectionCount() > 0) {
            return ViewUtil.getSelectedFile(treeDirectories);
        } else if (treeFavorites.getSelectionCount() > 0) {
            return ViewUtil.getSelectedDirectoryFromFavoriteDirectories();
        }

        return null;
    }

    private void importMetadataEditTemplate(TransferSupport transferSupport) {
        try {
            Object[] selTemplates = (Object[]) transferSupport
                                        .getTransferable()
                                        .getTransferData(Flavors.METADATA_EDIT_TEMPLATES);

            if (selTemplates == null) return;

            assert selTemplates.length == 1;

            GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray()
                    .setMetadataEditTemplate((MetadataEditTemplate)selTemplates[0]);

        } catch (Exception ex) {
            AppLog.logSevere(TransferHandlerPanelThumbnails.class, ex);
        }
    }
}
