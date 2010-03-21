/*
 * @(#)TransferHandlerThumbnailsPanel.java    Created on 2008-10-24
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.io.ImageUtil;
import de.elmar_baumann.jpt.io.ImageUtil.ConfirmOverwrite;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.types.ContentUtil;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.datatransfer.TransferUtil.FilenameDelimiter;

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
 * @author  Elmar Baumann
 */
public final class TransferHandlerThumbnailsPanel extends TransferHandler {
    private static final long serialVersionUID = 1831860682951562565L;

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        ThumbnailsPanel tnPanel =
            (ThumbnailsPanel) transferSupport.getComponent();

        return metadataTransferred(transferSupport)
               || isImageCollection(tnPanel)
               || (!transferSupport.isDataFlavorSupported(
                   Flavor.THUMBNAILS_PANEL) && canImportFiles(tnPanel)
                       && Flavor.hasFiles(transferSupport.getTransferable()));
    }

    private boolean canImportFiles(ThumbnailsPanel tnPanel) {
        return ContentUtil.isSingleDirectoryContent(tnPanel.getContent());
    }

    private boolean metadataTransferred(TransferSupport transferSupport) {
        boolean dropOverSelectedThumbnail =
            isDropOverSelectedThumbnail(transferSupport);
        boolean isThumbnailPos = isThumbnailPos(transferSupport);

        return (Flavor.hasKeywordsFromList(
            transferSupport) || Flavor.hasKeywordsFromTree(
            transferSupport)) && isThumbnailPos || (Flavor.hasMetadataTemplate(
            transferSupport) && dropOverSelectedThumbnail);
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
    public boolean importData(TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }

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

    private void moveSelectedImages(TransferSupport transferSupport,
                                    ThumbnailsPanel panel) {
        Point dropPoint = transferSupport.getDropLocation().getDropPoint();

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
            GUI.INSTANCE.getAppPanel().getListImageCollections();
        Object selElement = null;
        int    selIndex   = listImageCollections.getSelectedIndex();

        if (selIndex >= 0) {
            selElement = listImageCollections.getModel().getElementAt(selIndex);
        }

        return (selElement == null)
               ? null
               : selElement.toString();
    }

    private boolean insertMetadata(TransferSupport transferSupport) {
        Transferable t                         =
            transferSupport.getTransferable();
        boolean      dropOverSelectedThumbnail =
            isDropOverSelectedThumbnail(transferSupport);
        File imageFile = getImageFile(transferSupport);

        if (Flavor.hasKeywordsFromList(transferSupport)) {
            importStrings(Flavor.KEYWORDS_LIST, Support.getKeywords(t),
                          dropOverSelectedThumbnail, imageFile);
        } else if (Flavor.hasKeywordsFromTree(transferSupport)) {
            List<String> keywords = new ArrayList<String>();

            for (DefaultMutableTreeNode node : Support.getKeywordNodes(t)) {
                if (dropOverSelectedThumbnail) {
                    KeywordsHelper.addKeywordsToEditPanel(node);
                } else {
                    keywords.addAll(KeywordsHelper.getKeywordStrings(node,
                            true));
                }
            }

            if (!keywords.isEmpty()) {
                KeywordsHelper.saveKeywordsToImageFile(keywords, imageFile);
            }
        } else if (Flavor.hasMetadataTemplate(transferSupport)) {
            importMetadataTemplate(transferSupport);
        } else {
            return false;
        }

        return true;
    }

    public void importStrings(DataFlavor dataFlavor, Object[] strings,
                              boolean dropOverSelectedThumbnail,
                              File imageFile) {
        if ((strings == null) || (strings.length <= 0)) {
            return;
        }

        List<String> keywords = new ArrayList<String>(strings.length);

        for (Object o : strings) {
            keywords.add(o.toString());
        }

        if (dropOverSelectedThumbnail) {
            EditMetadataPanels editPanels =
                GUI.INSTANCE.getAppPanel().getEditMetadataPanels();
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

    public boolean isDropOverSelectedThumbnail(
            TransferSupport transferSupport) {
        Point           p     =
            transferSupport.getDropLocation().getDropPoint();
        ThumbnailsPanel panel =
            (ThumbnailsPanel) transferSupport.getComponent();

        return panel.isSelected(panel.getImageMoveDropIndex(p.x, p.y));
    }

    private boolean isThumbnailPos(TransferSupport transferSupport) {
        Point           p     =
            transferSupport.getDropLocation().getDropPoint();
        ThumbnailsPanel panel =
            (ThumbnailsPanel) transferSupport.getComponent();
        int index = panel.getThumbnailIndexAtPoint(p.x, p.y);

        return (panel.isIndex(index));
    }

    private File getImageFile(TransferSupport transferSupport) {
        Point           p     =
            transferSupport.getDropLocation().getDropPoint();
        ThumbnailsPanel panel =
            (ThumbnailsPanel) transferSupport.getComponent();
        int index = panel.getThumbnailIndexAtPoint(p.x, p.y);

        if (panel.isIndex(index)) {
            return panel.getFile(index);
        } else {
            return null;
        }
    }

    private boolean importFiles(File targetDir,
                                TransferSupport transferSupport) {
        if (targetDir == null) {
            return false;
        }

        List<File> srcFiles =
            TransferUtil.getFiles(transferSupport.getTransferable(),
                                  FilenameDelimiter.EMPTY);
        int dropAction = transferSupport.getDropAction();

        if (dropAction == TransferHandler.COPY) {
            ImageUtil.copyImageFiles(ImageUtil.getImageFiles(srcFiles),
                                     targetDir, ConfirmOverwrite.YES);

            return true;
        } else if (dropAction == TransferHandler.MOVE) {
            ImageUtil.moveImageFiles(ImageUtil.getImageFiles(srcFiles),
                                     targetDir, ConfirmOverwrite.YES);

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

    private void importMetadataTemplate(TransferSupport transferSupport) {
        try {
            Object[] selTemplates =
                (Object[]) transferSupport.getTransferable().getTransferData(
                    Flavor.METADATA_TEMPLATES);

            if (selTemplates == null) {
                return;
            }

            assert selTemplates.length == 1;
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels()
                .setMetadataTemplate((MetadataTemplate) selTemplates[0]);
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerThumbnailsPanel.class, ex);
        }
    }
}
