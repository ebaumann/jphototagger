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
package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.controller.actions.ControllerActionExecutor;
import de.elmar_baumann.jpt.controller.actions.ControllerActionsShowDialog;
import de.elmar_baumann.jpt.controller.directories.ControllerCreateDirectory;
import de.elmar_baumann.jpt.controller.directories.ControllerDeleteDirectory;
import de.elmar_baumann.jpt.controller.directories.ControllerDirectoryPasteFiles;
import de.elmar_baumann.jpt.controller.metadata.ControllerEnableInsertMetadataTemplate;
import de.elmar_baumann.jpt.controller.directories.ControllerRefreshDirectoryTree;
import de.elmar_baumann.jpt.controller.directories.ControllerRenameDirectory;
import de.elmar_baumann.jpt.controller.favorites.ControllerDeleteFavorite;
import de.elmar_baumann.jpt.controller.favorites.ControllerOpenFavoriteInFolders;
import de.elmar_baumann.jpt.controller.favorites.ControllerFavoritesAddFilesystemFolder;
import de.elmar_baumann.jpt.controller.favorites.ControllerFavoritesDeleteFilesystemFolder;
import de.elmar_baumann.jpt.controller.favorites.ControllerFavoritesRenameFilesystemFolder;
import de.elmar_baumann.jpt.controller.favorites.ControllerInsertFavorite;
import de.elmar_baumann.jpt.controller.favorites.ControllerMoveFavorite;
import de.elmar_baumann.jpt.controller.favorites.ControllerRefreshFavorites;
import de.elmar_baumann.jpt.controller.favorites.ControllerUpdateFavorite;
import de.elmar_baumann.jpt.controller.filesystem.ControllerCopyFilesToDirectory;
import de.elmar_baumann.jpt.controller.programs.ControllerOpenFilesWithOtherApp;
import de.elmar_baumann.jpt.controller.programs.ControllerOpenFilesWithStandardApp;
import de.elmar_baumann.jpt.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.jpt.controller.filesystem.ControllerMoveFiles;
import de.elmar_baumann.jpt.controller.filesystem.ControllerRenameFiles;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerAddToImageCollection;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerCreateImageCollection;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerDeleteFromImageCollection;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerDeleteImageCollection;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerImageCollectionSelected;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerRenameImageCollection;
import de.elmar_baumann.jpt.controller.keywords.ControllerKeywordItemSelected;
import de.elmar_baumann.jpt.controller.metadata.ControllerEmptyMetadata;
import de.elmar_baumann.jpt.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import de.elmar_baumann.jpt.controller.search.ControllerAdvancedSearch;
import de.elmar_baumann.jpt.controller.filesystem.ControllerAutocopyDirectory;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerExportHierarchicalKeywords;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerHierarchicalKeywordsDbUpdates;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerHierarchicalKeywordsSelection;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerHighlightHierarchicalKeywords;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerImportHierarchicalKeywords;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerShowHierarchicalKeywordsDialog;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerPickReject;
import de.elmar_baumann.jpt.controller.keywords.ControllerDeleteKeywords;
import de.elmar_baumann.jpt.controller.keywords.ControllerRenameKeywords;
import de.elmar_baumann.jpt.controller.metadata.ControllerCopyPasteMetadata;
import de.elmar_baumann.jpt.controller.metadata.ControllerExifToXmp;
import de.elmar_baumann.jpt.controller.metadata.ControllerExtractEmbeddedXmp;
import de.elmar_baumann.jpt.controller.search.ControllerFastSearch;
import de.elmar_baumann.jpt.controller.misc.ControllerGoTo;
import de.elmar_baumann.jpt.controller.metadata.ControllerIptcToXmp;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataEditTemplateAdd;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataEditTemplateDelete;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataEditTemplateEdit;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataEditTemplateRename;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataEditTemplateSetToSelImages;
import de.elmar_baumann.jpt.controller.misc.ControllerItemsMutualExcludeSelection;
import de.elmar_baumann.jpt.controller.misc.ControllerLogfileDialog;
import de.elmar_baumann.jpt.controller.misc.ControllerMenuItemEnabler;
import de.elmar_baumann.jpt.controller.misc.ControllerShowSystemOutput;
import de.elmar_baumann.jpt.controller.misc.ControllerThumbnailCountDisplay;
import de.elmar_baumann.jpt.controller.search.ControllerCreateSavedSearch;
import de.elmar_baumann.jpt.controller.search.ControllerDeleteSavedSearch;
import de.elmar_baumann.jpt.controller.search.ControllerEditSavedSearch;
import de.elmar_baumann.jpt.controller.search.ControllerRenameSavedSearch;
import de.elmar_baumann.jpt.controller.search.ControllerSavedSearchSelected;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerCopyOrCutFilesToClipboard;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerCreateMetadataOfDisplayedThumbnails;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerCreateMetadataOfSelectedThumbnails;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerDeleteThumbnailsFromDatabase;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerPasteFilesFromClipboard;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerRefreshThumbnailsPanel;
import de.elmar_baumann.jpt.controller.misc.ControllerPlugins;
import de.elmar_baumann.jpt.controller.nometadata.ControllerNoMetadataItemSelected;
import de.elmar_baumann.jpt.controller.rating.ControllerSetRating;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerRotateThumbnail;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerSliderThumbnailSize;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerThumbnailsDatabaseChanges;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerThumbnailsPanelPersistence;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerToggleKeywordOverlay;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;

/**
 * Erzeugt alle Controller.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ControllerFactory {

    static final ControllerFactory INSTANCE = new ControllerFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(ControllerFactory.class, init);
        if (!init) {
            GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString("ControllerFactory.Init.Start"), AppPanel.MessageType.INFO, 1000);
            new ControllerThumbnailsPanelPersistence();
            new ControllerItemsMutualExcludeSelection();
            new ControllerKeywordItemSelected();
            new ControllerSavedSearchSelected();
            new ControllerImageCollectionSelected();
            new ControllerMenuItemEnabler();
            new ControllerThumbnailCountDisplay();
            new ControllerCreateMetadataOfSelectedThumbnails();
            new ControllerCreateImageCollection();
            new ControllerDeleteImageCollection();
            new ControllerDeleteFromImageCollection();
            new ControllerAddToImageCollection();
            new ControllerRenameImageCollection();
            new ControllerCreateSavedSearch();
            new ControllerDeleteSavedSearch();
            new ControllerEditSavedSearch();
            new ControllerRenameSavedSearch();
            new ControllerRotateThumbnail();
            new ControllerOpenFilesWithStandardApp();
            new ControllerOpenFilesWithOtherApp();
            new ControllerDeleteThumbnailsFromDatabase();
            new ControllerCreateMetadataOfDisplayedThumbnails();
            new ControllerLogfileDialog();
            new ControllerFastSearch();
            new ControllerAdvancedSearch();
            new ControllerThumbnailSelectionEditMetadata();
            new ControllerEmptyMetadata();
            new ControllerEnableInsertMetadataTemplate();
            new ControllerInsertFavorite();
            new ControllerDeleteFavorite();
            new ControllerUpdateFavorite();
            new ControllerCopyFilesToDirectory();
            new ControllerIptcToXmp();
            new ControllerExifToXmp();
            new ControllerGoTo();
            new ControllerSliderThumbnailSize();
            new ControllerToggleKeywordOverlay();
            new ControllerDeleteFiles();
            new ControllerRenameFiles();
            new ControllerMoveFiles();
            new ControllerSortThumbnails();
            new ControllerThumbnailsDatabaseChanges();
            new ControllerAutocopyDirectory();
            new ControllerCopyOrCutFilesToClipboard();
            new ControllerDirectoryPasteFiles();
            new ControllerPasteFilesFromClipboard();
            new ControllerOpenFavoriteInFolders();
            new ControllerActionsShowDialog();
            new ControllerActionExecutor();
            new ControllerExtractEmbeddedXmp();
            new ControllerShowSystemOutput();
            new ControllerMoveFavorite();
            new ControllerCreateDirectory();
            new ControllerRenameDirectory();
            new ControllerDeleteDirectory();
            new ControllerRefreshFavorites();
            new ControllerRefreshDirectoryTree();
            new ControllerFavoritesAddFilesystemFolder();
            new ControllerFavoritesRenameFilesystemFolder();
            new ControllerFavoritesDeleteFilesystemFolder();
            new ControllerRefreshThumbnailsPanel();
            new ControllerPickReject();
            new ControllerHighlightHierarchicalKeywords();
            new ControllerShowHierarchicalKeywordsDialog();
            new ControllerImportHierarchicalKeywords();
            new ControllerExportHierarchicalKeywords();
            new ControllerSetRating();
            new ControllerNoMetadataItemSelected();
            new ControllerCopyPasteMetadata();
            new ControllerHierarchicalKeywordsSelection();
            new ControllerPlugins();
            new ControllerHierarchicalKeywordsDbUpdates();
            new ControllerRenameKeywords();
            new ControllerDeleteKeywords();
            new ControllerMetadataEditTemplateSetToSelImages();
            new ControllerMetadataEditTemplateAdd();
            new ControllerMetadataEditTemplateEdit();
            new ControllerMetadataEditTemplateDelete();
            new ControllerMetadataEditTemplateRename();
            init = true;
            GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString("ControllerFactory.Init.Finished"), AppPanel.MessageType.INFO, 1000);
        }
    }
}
