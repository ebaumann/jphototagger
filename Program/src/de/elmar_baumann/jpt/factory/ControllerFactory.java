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
import de.elmar_baumann.jpt.controller.keywords.list.ControllerKeywordItemSelected;
import de.elmar_baumann.jpt.controller.metadata.ControllerEmptyMetadata;
import de.elmar_baumann.jpt.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import de.elmar_baumann.jpt.controller.search.ControllerAdvancedSearch;
import de.elmar_baumann.jpt.controller.filesystem.ControllerAutocopyDirectory;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerExportKeywords;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerKeywordsDbUpdates;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerKeywordsSelection;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerHighlightKeywordsTree;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerImportKeywords;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerShowKeywordsDialog;
import de.elmar_baumann.jpt.controller.imagecollection.ControllerPickReject;
import de.elmar_baumann.jpt.controller.keywords.list.ControllerDeleteKeywords;
import de.elmar_baumann.jpt.controller.keywords.list.ControllerRenameKeywords;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerToggleButtonSelKeywords;
import de.elmar_baumann.jpt.controller.metadata.ControllerCopyPasteMetadata;
import de.elmar_baumann.jpt.controller.metadata.ControllerExifToXmp;
import de.elmar_baumann.jpt.controller.metadata.ControllerExtractEmbeddedXmp;
import de.elmar_baumann.jpt.controller.search.ControllerFastSearch;
import de.elmar_baumann.jpt.controller.misc.ControllerGoTo;
import de.elmar_baumann.jpt.controller.metadata.ControllerIptcToXmp;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataTemplateAdd;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataTemplateDelete;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataTemplateEdit;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataTemplateRename;
import de.elmar_baumann.jpt.controller.metadatatemplates.ControllerMetadataTemplateSetToSelImages;
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
import de.elmar_baumann.lib.componentutil.MessageLabel;
import java.util.List;

/**
 * Erzeugt alle Controller.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ControllerFactory {

    public static final ControllerFactory INSTANCE = new ControllerFactory();
    private  final      Support           support  = new Support();
    private volatile    boolean           init;

    synchronized void init() {
        Util.checkInit(ControllerFactory.class, init);
        if (!init) {
            GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ControllerFactory.Init.Start"), MessageLabel.MessageType.INFO, 1000);
            support.add(new ControllerThumbnailsPanelPersistence());
            support.add(new ControllerItemsMutualExcludeSelection());
            support.add(new ControllerKeywordItemSelected());
            support.add(new ControllerSavedSearchSelected());
            support.add(new ControllerImageCollectionSelected());
            support.add(new ControllerMenuItemEnabler());
            support.add(new ControllerThumbnailCountDisplay());
            support.add(new ControllerCreateMetadataOfSelectedThumbnails());
            support.add(new ControllerCreateImageCollection());
            support.add(new ControllerDeleteImageCollection());
            support.add(new ControllerDeleteFromImageCollection());
            support.add(new ControllerAddToImageCollection());
            support.add(new ControllerRenameImageCollection());
            support.add(new ControllerCreateSavedSearch());
            support.add(new ControllerDeleteSavedSearch());
            support.add(new ControllerEditSavedSearch());
            support.add(new ControllerRenameSavedSearch());
            support.add(new ControllerRotateThumbnail());
            support.add(new ControllerOpenFilesWithStandardApp());
            support.add(new ControllerOpenFilesWithOtherApp());
            support.add(new ControllerDeleteThumbnailsFromDatabase());
            support.add(new ControllerCreateMetadataOfDisplayedThumbnails());
            support.add(new ControllerLogfileDialog());
            support.add(new ControllerFastSearch());
            support.add(new ControllerAdvancedSearch());
            support.add(new ControllerThumbnailSelectionEditMetadata());
            support.add(new ControllerEmptyMetadata());
            support.add(new ControllerEnableInsertMetadataTemplate());
            support.add(new ControllerInsertFavorite());
            support.add(new ControllerDeleteFavorite());
            support.add(new ControllerUpdateFavorite());
            support.add(new ControllerCopyFilesToDirectory());
            support.add(new ControllerIptcToXmp());
            support.add(new ControllerExifToXmp());
            support.add(new ControllerGoTo());
            support.add(new ControllerSliderThumbnailSize());
            support.add(new ControllerToggleKeywordOverlay());
            support.add(new ControllerDeleteFiles());
            support.add(new ControllerRenameFiles());
            support.add(new ControllerMoveFiles());
            support.add(new ControllerSortThumbnails());
            support.add(new ControllerThumbnailsDatabaseChanges());
            support.add(new ControllerAutocopyDirectory());
            support.add(new ControllerCopyOrCutFilesToClipboard());
            support.add(new ControllerDirectoryPasteFiles());
            support.add(new ControllerPasteFilesFromClipboard());
            support.add(new ControllerOpenFavoriteInFolders());
            support.add(new ControllerActionsShowDialog());
            support.add(new ControllerActionExecutor());
            support.add(new ControllerExtractEmbeddedXmp());
            support.add(new ControllerShowSystemOutput());
            support.add(new ControllerMoveFavorite());
            support.add(new ControllerCreateDirectory());
            support.add(new ControllerRenameDirectory());
            support.add(new ControllerDeleteDirectory());
            support.add(new ControllerRefreshFavorites());
            support.add(new ControllerRefreshDirectoryTree());
            support.add(new ControllerFavoritesAddFilesystemFolder());
            support.add(new ControllerFavoritesRenameFilesystemFolder());
            support.add(new ControllerFavoritesDeleteFilesystemFolder());
            support.add(new ControllerRefreshThumbnailsPanel());
            support.add(new ControllerPickReject());
            support.add(new ControllerHighlightKeywordsTree());
            support.add(new ControllerShowKeywordsDialog());
            support.add(new ControllerImportKeywords());
            support.add(new ControllerExportKeywords());
            support.add(new ControllerSetRating());
            support.add(new ControllerNoMetadataItemSelected());
            support.add(new ControllerCopyPasteMetadata());
            support.add(new ControllerKeywordsSelection());
            support.add(new ControllerPlugins());
            support.add(new ControllerKeywordsDbUpdates());
            support.add(new ControllerRenameKeywords());
            support.add(new ControllerDeleteKeywords());
            support.add(new ControllerMetadataTemplateSetToSelImages());
            support.add(new ControllerMetadataTemplateAdd());
            support.add(new ControllerMetadataTemplateEdit());
            support.add(new ControllerMetadataTemplateDelete());
            support.add(new ControllerMetadataTemplateRename());
            support.add(new ControllerToggleButtonSelKeywords());
            init = true;
            GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ControllerFactory.Init.Finished"), MessageLabel.MessageType.INFO, 1000);
        }
    }

    /**
     * Returns all instances of a specific controller.
     *
     * @param  <T>             type of controller class
     * @param  controllerClass controller class (key)
     * @return                 controller instances or null if no controller of
     *                         that class was instanciated
     */
    public <T> List<T> getController(Class<T> controllerClass) {
        return support.get(controllerClass);
    }

    void add(Object controller) {
        support.add(controller);
    }
}
