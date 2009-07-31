package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.controller.actions.ControllerActionExecutor;
import de.elmar_baumann.imv.controller.actions.ControllerActionsShowDialog;
import de.elmar_baumann.imv.controller.categories.ControllerCategoryItemSelected;
import de.elmar_baumann.imv.controller.directories.ControllerCreateDirectory;
import de.elmar_baumann.imv.controller.directories.ControllerDeleteDirectory;
import de.elmar_baumann.imv.controller.directories.ControllerDirectoryPasteFiles;
import de.elmar_baumann.imv.controller.metadata.ControllerEnableInsertMetadataTemplate;
import de.elmar_baumann.imv.controller.directories.ControllerRefreshDirectoryTree;
import de.elmar_baumann.imv.controller.directories.ControllerRenameDirectory;
import de.elmar_baumann.imv.controller.favorites.ControllerDeleteFavorite;
import de.elmar_baumann.imv.controller.favorites.ControllerOpenFavoriteInFolders;
import de.elmar_baumann.imv.controller.favorites.ControllerFavoritesAddFilesystemFolder;
import de.elmar_baumann.imv.controller.favorites.ControllerFavoritesDeleteFilesystemFolder;
import de.elmar_baumann.imv.controller.favorites.ControllerFavoritesRenameFilesystemFolder;
import de.elmar_baumann.imv.controller.favorites.ControllerInsertFavorite;
import de.elmar_baumann.imv.controller.favorites.ControllerMoveFavorite;
import de.elmar_baumann.imv.controller.favorites.ControllerRefreshFavorites;
import de.elmar_baumann.imv.controller.favorites.ControllerUpdateFavorite;
import de.elmar_baumann.imv.controller.filesystem.ControllerCopyFilesToDirectory;
import de.elmar_baumann.imv.controller.programs.ControllerOpenFilesWithOtherApp;
import de.elmar_baumann.imv.controller.programs.ControllerOpenFilesWithStandardApp;
import de.elmar_baumann.imv.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.imv.controller.filesystem.ControllerMoveFiles;
import de.elmar_baumann.imv.controller.filesystem.ControllerRenameFiles;
import de.elmar_baumann.imv.controller.imagecollection.ControllerAddToImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerCreateImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerDeleteFromImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerDeleteImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerImageCollectionSelected;
import de.elmar_baumann.imv.controller.imagecollection.ControllerRenameImageCollection;
import de.elmar_baumann.imv.controller.keywords.ControllerKeywordItemSelected;
import de.elmar_baumann.imv.controller.metadata.ControllerEmptyMetadata;
import de.elmar_baumann.imv.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import de.elmar_baumann.imv.controller.search.ControllerAdvancedSearch;
import de.elmar_baumann.imv.controller.filesystem.ControllerAutocopyDirectory;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerHighlightHierarchicalKeywords;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerShowHierarchicalKeywordsDialog;
import de.elmar_baumann.imv.controller.imagecollection.ControllerPickReject;
import de.elmar_baumann.imv.controller.metadata.ControllerExtractEmbeddedXmp;
import de.elmar_baumann.imv.controller.search.ControllerFastSearch;
import de.elmar_baumann.imv.controller.misc.ControllerGoTo;
import de.elmar_baumann.imv.controller.metadata.ControllerIptcToXmp;
import de.elmar_baumann.imv.controller.misc.ControllerItemsMutualExcludeSelection;
import de.elmar_baumann.imv.controller.misc.ControllerLogfileDialog;
import de.elmar_baumann.imv.controller.misc.ControllerMenuItemEnabler;
import de.elmar_baumann.imv.controller.misc.ControllerRenameFilenamesInDb;
import de.elmar_baumann.imv.controller.misc.ControllerShowSystemOutput;
import de.elmar_baumann.imv.controller.misc.ControllerThumbnailCountDisplay;
import de.elmar_baumann.imv.controller.search.ControllerCreateSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerDeleteSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerEditSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerRenameSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerSavedSearchSelected;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCopyOrCutFilesToClipboard;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCreateMetadataOfCurrentThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCreateMetadataOfSelectedThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDeleteThumbnailsFromDatabase;
import de.elmar_baumann.imv.controller.thumbnail.ControllerPasteFilesFromClipboard;
import de.elmar_baumann.imv.controller.thumbnail.ControllerRefreshThumbnailsPanel;
import de.elmar_baumann.imv.controller.metadata.ControllerRenameXmpMetadata;
import de.elmar_baumann.imv.controller.metadata.ControllerShowTextSelectionDialog;
import de.elmar_baumann.imv.controller.thumbnail.ControllerRotateThumbnail;
import de.elmar_baumann.imv.controller.thumbnail.ControllerSliderThumbnailSize;
import de.elmar_baumann.imv.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerThumbnailsDatabaseChanges;
import de.elmar_baumann.imv.controller.thumbnail.ControllerThumbnailsPanelPersistence;
import de.elmar_baumann.imv.controller.thumbnail.ControllerToggleKeywordOverlay;

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
            new ControllerThumbnailsPanelPersistence();
            new ControllerItemsMutualExcludeSelection();
            new ControllerCategoryItemSelected();
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
            new ControllerCreateMetadataOfCurrentThumbnails();
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
            new ControllerRenameXmpMetadata();
            new ControllerIptcToXmp();
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
            new ControllerRenameFilenamesInDb();
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
            new ControllerShowTextSelectionDialog();
            init = true;
        }
    }
}
