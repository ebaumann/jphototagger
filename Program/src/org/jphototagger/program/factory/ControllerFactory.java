/*
 * @(#)ControllerFactory.java    Created on 2008-09-29
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

package org.jphototagger.program.factory;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.controller.actions.ControllerActionExecutor;
import org.jphototagger.program.controller.actions.ControllerActionsMenuUpdater;
import org.jphototagger.program.controller.actions.ControllerActionsShowDialog;
import org.jphototagger.program.controller.directories.ControllerCreateDirectory;
import org.jphototagger.program.controller.directories.ControllerDeleteDirectory;
import org.jphototagger.program.controller.directories
    .ControllerDirectoryPasteFiles;
import org.jphototagger.program.controller.directories.ControllerDirectorySelected;
import org.jphototagger.program.controller.directories
    .ControllerRefreshDirectoryTree;
import org.jphototagger.program.controller.directories.ControllerRenameDirectory;
import org.jphototagger.program.controller.favorites.ControllerDeleteFavorite;
import org.jphototagger.program.controller.favorites
    .ControllerFavoritesAddFilesystemFolder;
import org.jphototagger.program.controller.favorites
    .ControllerFavoritesDeleteFilesystemFolder;
import org.jphototagger.program.controller.favorites.ControllerFavoriteSelected;
import org.jphototagger.program.controller.favorites
    .ControllerFavoritesRenameFilesystemFolder;
import org.jphototagger.program.controller.favorites.ControllerInsertFavorite;
import org.jphototagger.program.controller.favorites.ControllerMoveFavorite;
import org.jphototagger.program.controller.favorites
    .ControllerOpenFavoriteInFolders;
import org.jphototagger.program.controller.favorites.ControllerRefreshFavorites;
import org.jphototagger.program.controller.favorites.ControllerUpdateFavorite;
import org.jphototagger.program.controller.filesystem
    .ControllerCopyFilesToDirectory;
import org.jphototagger.program.controller.filesystem.ControllerDeleteFiles;
import org.jphototagger.program.controller.filesystem.ControllerImportImageFiles;
import org.jphototagger.program.controller.filesystem.ControllerMoveFiles;
import org.jphototagger.program.controller.filesystem.ControllerRenameFiles;
import org.jphototagger.program.controller.imagecollection
    .ControllerAddImageCollection;
import org.jphototagger.program.controller.imagecollection
    .ControllerAddToImageCollection;
import org.jphototagger.program.controller.imagecollection
    .ControllerDeleteFromImageCollection;
import org.jphototagger.program.controller.imagecollection
    .ControllerDeleteImageCollection;
import org.jphototagger.program.controller.imagecollection
    .ControllerImageCollectionSelected;
import org.jphototagger.program.controller.imagecollection.ControllerPickReject;
import org.jphototagger.program.controller.imagecollection
    .ControllerRenameImageCollection;
import org.jphototagger.program.controller.keywords.list.ControllerDeleteKeywords;
import org.jphototagger.program.controller.keywords.list
    .ControllerDeleteKeywordsFromEditPanel;
import org.jphototagger.program.controller.keywords.list.ControllerDisplayKeyword;
import org.jphototagger.program.controller.keywords.list
    .ControllerEditKeywordSynonyms;
import org.jphototagger.program.controller.keywords.list.ControllerInsertKeywords;
import org.jphototagger.program.controller.keywords.list
    .ControllerKeywordItemSelected;
import org.jphototagger.program.controller.keywords.list.ControllerRenameKeywords;
import org.jphototagger.program.controller.keywords.tree.ControllerAddKeyword;
import org.jphototagger.program.controller.keywords.tree
    .ControllerCopyCutPasteKeyword;
import org.jphototagger.program.controller.keywords.tree
    .ControllerDeleteKeywordFromEditPanel;
import org.jphototagger.program.controller.keywords.tree.ControllerExportKeywords;
import org.jphototagger.program.controller.keywords.tree
    .ControllerHighlightKeywordsTree;
import org.jphototagger.program.controller.keywords.tree.ControllerImportKeywords;
import org.jphototagger.program.controller.keywords.tree
    .ControllerKeywordsDbUpdates;
import org.jphototagger.program.controller.keywords.tree
    .ControllerKeywordsDisplayImages;
import org.jphototagger.program.controller.keywords.tree
    .ControllerKeywordsSelection;
import org.jphototagger.program.controller.keywords.tree.ControllerRenameKeyword;
import org.jphototagger.program.controller.keywords.tree
    .ControllerShowKeywordsDialog;
import org.jphototagger.program.controller.keywords.tree
    .ControllerToggleButtonSelKeywords;
import org.jphototagger.program.controller.keywords.tree
    .ControllerToggleRealKeyword;
import org.jphototagger.program.controller.metadata.ControllerCopyPasteMetadata;
import org.jphototagger.program.controller.metadata.ControllerEmptyMetadata;
import org.jphototagger.program.controller.metadata
    .ControllerEnableCreateMetadataTemplate;
import org.jphototagger.program.controller.metadata
    .ControllerEnableInsertMetadataTemplate;
import org.jphototagger.program.controller.metadata.ControllerExifToXmp;
import org.jphototagger.program.controller.metadata.ControllerExtractEmbeddedXmp;
import org.jphototagger.program.controller.metadata.ControllerIptcToXmp;
import org.jphototagger.program.controller.metadata.ControllerMetadataTemplates;
import org.jphototagger.program.controller.metadata.ControllerShowMetadata;
import org.jphototagger.program.controller.metadata
    .ControllerShowUpdateMetadataDialog;
import org.jphototagger.program.controller.metadata
    .ControllerThumbnailSelectionEditMetadata;
import org.jphototagger.program.controller.metadatatemplates
    .ControllerMetadataTemplateAdd;
import org.jphototagger.program.controller.metadatatemplates
    .ControllerMetadataTemplateDelete;
import org.jphototagger.program.controller.metadatatemplates
    .ControllerMetadataTemplateEdit;
import org.jphototagger.program.controller.metadatatemplates
    .ControllerMetadataTemplateRename;
import org.jphototagger.program.controller.metadatatemplates
    .ControllerMetadataTemplateSetToSelImages;
import org.jphototagger.program.controller.misc.ControllerAboutApp;
import org.jphototagger.program.controller.misc.ControllerBackupDatabase;
import org.jphototagger.program.controller.misc.ControllerGoTo;
import org.jphototagger.program.controller.misc.ControllerHelp;
import org.jphototagger.program.controller.misc
    .ControllerItemsMutualExcludeSelection;
import org.jphototagger.program.controller.misc.ControllerLogfileDialog;
import org.jphototagger.program.controller.misc.ControllerMaintainDatabase;
import org.jphototagger.program.controller.misc.ControllerMenuItemEnabler;
import org.jphototagger.program.controller.misc.ControllerPlugins;
import org.jphototagger.program.controller.misc.ControllerShowSynonymsDialog;
import org.jphototagger.program.controller.misc.ControllerShowSystemOutput;
import org.jphototagger.program.controller.misc.ControllerShowUserSettingsDialog;
import org.jphototagger.program.controller.misc.ControllerThumbnailCountDisplay;
import org.jphototagger.program.controller.misc.SizeAndLocationController;
import org.jphototagger.program.controller.miscmetadata
    .ControllerDeleteMiscMetadata;
import org.jphototagger.program.controller.miscmetadata
    .ControllerMiscMetadataItemSelected;
import org.jphototagger.program.controller.miscmetadata
    .ControllerRenameMiscMetadata;
import org.jphototagger.program.controller.nometadata
    .ControllerNoMetadataItemSelected;
import org.jphototagger.program.controller.programs.ControllerOpenFilesWithOtherApp;
import org.jphototagger.program.controller.programs
    .ControllerOpenFilesWithStandardApp;
import org.jphototagger.program.controller.rating.ControllerSetRating;
import org.jphototagger.program.controller.search.ControllerAdvancedSearch;
import org.jphototagger.program.controller.search.ControllerCreateSavedSearch;
import org.jphototagger.program.controller.search.ControllerDeleteSavedSearch;
import org.jphototagger.program.controller.search.ControllerEditSavedSearch;
import org.jphototagger.program.controller.search.ControllerFastSearch;
import org.jphototagger.program.controller.search.ControllerRenameSavedSearch;
import org.jphototagger.program.controller.search.ControllerSavedSearchSelected;
import org.jphototagger.program.controller.search
    .ControllerShowAdvancedSearchDialog;
import org.jphototagger.program.controller.thumbnail
    .ControllerCopyOrCutFilesToClipboard;
import org.jphototagger.program.controller.thumbnail
    .ControllerCreateMetadataOfDisplayedThumbnails;
import org.jphototagger.program.controller.thumbnail
    .ControllerCreateMetadataOfSelectedThumbnails;
import org.jphototagger.program.controller.thumbnail
    .ControllerDeleteThumbnailsFromDatabase;
import org.jphototagger.program.controller.thumbnail
    .ControllerPasteFilesFromClipboard;
import org.jphototagger.program.controller.thumbnail
    .ControllerRefreshThumbnailsPanel;
import org.jphototagger.program.controller.thumbnail.ControllerRotateThumbnail;
import org.jphototagger.program.controller.thumbnail.ControllerSliderThumbnailSize;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.controller.thumbnail
    .ControllerThumbnailsDatabaseChanges;
import org.jphototagger.program.controller.thumbnail
    .ControllerThumbnailsPanelPersistence;
import org.jphototagger.program.controller.thumbnail
    .ControllerThumbnailsSelectAllOrNothing;
import org.jphototagger.program.controller.thumbnail.ControllerToggleKeywordOverlay;
import org.jphototagger.program.controller.timeline.ControllerTimelineItemSelected;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.dialog.HelpBrowser;
import org.jphototagger.lib.dialog.SystemOutputDialog;

import java.util.List;
import org.jphototagger.program.controller.miscmetadata.ControllerAddMetadataToSelImages;
import org.jphototagger.program.controller.miscmetadata.ControllerRemoveMetadataFromSelImages;

/**
 * Erzeugt alle Controller.
 *
 * @author  Elmar Baumann
 */
public final class ControllerFactory {
    public static final ControllerFactory INSTANCE = new ControllerFactory();
    private final Support                 support  = new Support();
    private volatile boolean              init;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        AppLogger.logFine(getClass(), "ControllerFactory.Init.Start");
        GUI.INSTANCE.getAppPanel().setStatusbarText(
            JptBundle.INSTANCE.getString("ControllerFactory.Init.Start"),
            MessageLabel.MessageType.INFO, -1);
        support.add(new ControllerThumbnailsPanelPersistence());
        support.add(new ControllerItemsMutualExcludeSelection());
        support.add(new ControllerKeywordItemSelected());
        support.add(new ControllerSavedSearchSelected());
        support.add(new ControllerImageCollectionSelected());
        support.add(new ControllerMenuItemEnabler());
        support.add(new ControllerThumbnailCountDisplay());
        support.add(new ControllerCreateMetadataOfSelectedThumbnails());
        support.add(new ControllerAddImageCollection());
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
        support.add(new ControllerImportImageFiles());
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
        support.add(new ControllerDisplayKeyword());
        support.add(new ControllerMetadataTemplates());
        support.add(new ControllerShowMetadata());
        support.add(new ControllerMiscMetadataItemSelected());
        support.add(new ControllerTimelineItemSelected());
        support.add(new ControllerFavoriteSelected());
        support.add(new ControllerDirectorySelected());
        support.add(new ControllerEnableCreateMetadataTemplate());
        support.add(new ControllerActionsMenuUpdater());
        support.add(new ControllerThumbnailsSelectAllOrNothing());
        support.add(new ControllerAboutApp());
        support.add(new ControllerHelp());
        support.add(new ControllerMaintainDatabase());
        support.add(new ControllerShowUpdateMetadataDialog());
        support.add(new ControllerShowUserSettingsDialog());
        support.add(new ControllerShowAdvancedSearchDialog());
        support.add(new ControllerShowSynonymsDialog());
        support.add(new ControllerEditKeywordSynonyms());
        support.add(new ControllerBackupDatabase());
        support.add(new ControllerInsertKeywords());
        support.add(new ControllerDeleteMiscMetadata());
        support.add(new ControllerRenameMiscMetadata());
        support.add(new org.jphototagger.program.controller.keywords.list
            .ControllerAddKeywordsToEditPanel());
        support.add(new ControllerDeleteKeywordsFromEditPanel());
        support.add(new ControllerAddMetadataToSelImages());
        support.add(new ControllerRemoveMetadataFromSelImages());
        addKeywordsTreeControllers();
        addSizeAndLocationController();
        AppLogger.logFine(getClass(), "ControllerFactory.Init.Finished");
        GUI.INSTANCE.getAppPanel().setStatusbarText(
            JptBundle.INSTANCE.getString("ControllerFactory.Init.Finished"),
            MessageLabel.MessageType.INFO, 1000);
    }

    private void addKeywordsTreeControllers() {
        KeywordsPanel[] keywordPanels = { GUI.INSTANCE.getAppPanel()
                                            .getPanelEditKeywords(),
                                          InputHelperDialog.INSTANCE
                                              .getPanelKeywords() };

        for (KeywordsPanel keywordsPanel : keywordPanels) {
            support.add(new ControllerToggleRealKeyword(keywordsPanel));
            support.add(new ControllerRenameKeyword(keywordsPanel));
            support.add(new ControllerAddKeyword(keywordsPanel));
            support.add(new org.jphototagger.program.controller.keywords.tree
                .ControllerDeleteKeywords(keywordsPanel));
            support.add(new org.jphototagger.program.controller.keywords.tree
                .ControllerAddKeywordsToEditPanel(keywordsPanel));
            support.add(
                new ControllerDeleteKeywordFromEditPanel(keywordsPanel));
            support.add(new ControllerCopyCutPasteKeyword(keywordsPanel));
            support.add(new ControllerKeywordsDisplayImages());
        }
    }

    private void addSizeAndLocationController() {
        SizeAndLocationController ctrl = new SizeAndLocationController();

        support.add(ctrl);
        SystemOutputDialog.INSTANCE.addWindowListener(ctrl);
        HelpBrowser.INSTANCE.addWindowListener(ctrl);
    }

    /**
     * Returns all instances of a specific controller.
     *
     * @param  <T>             type of controller class
     * @param  controllerClass controller class (key)
     * @return                 controller instances or null if no controller of
     *                         that class was instanciated
     */
    public <T> List<T> getControllers(Class<T> controllerClass) {
        return support.getAll(controllerClass);
    }

    /**
     * Returns the first added instance of a specific controller.
     *
     * @param  <T>             type of controller class
     * @param  controllerClass controller class (key)
     * @return                 controller instance or null if no controller of
     *                         that class was instanciated
     */
    public <T> T getController(Class<T> controllerClass) {
        return support.getFirst(controllerClass);
    }
}
