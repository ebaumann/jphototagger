package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.actions.ControllerActionExecutor;
import de.elmar_baumann.imv.controller.actions.ControllerActionsShowDialog;
import de.elmar_baumann.imv.controller.categories.ControllerCategoryItemSelected;
import de.elmar_baumann.imv.controller.directories.ControllerDirectoryCopyFiles;
import de.elmar_baumann.imv.controller.directories.ControllerEnableInsertMetadataTemplate;
import de.elmar_baumann.imv.controller.directories.ControllerDirectorySelected;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerDeleteFavoriteDirectory;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerFavoriteDirectoryOpenInFolders;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerFavoriteDirectorySelected;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerInsertFavoriteDirectory;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerUpdateFavoriteDirectory;
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
import de.elmar_baumann.imv.controller.metadata.ControllerMetadataTemplates;
import de.elmar_baumann.imv.controller.metadata.ControllerSaveMetadata;
import de.elmar_baumann.imv.controller.metadata.ControllerShowMetadata;
import de.elmar_baumann.imv.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import de.elmar_baumann.imv.controller.search.ControllerAdvancedSearch;
import de.elmar_baumann.imv.controller.filesystem.ControllerAutocopyDirectory;
import de.elmar_baumann.imv.controller.metadata.ControllerExtractEmbeddedXmp;
import de.elmar_baumann.imv.controller.search.ControllerFastSearch;
import de.elmar_baumann.imv.controller.misc.ControllerGoTo;
import de.elmar_baumann.imv.controller.metadata.ControllerIptcToXmp;
import de.elmar_baumann.imv.controller.misc.ControllerItemsMutualExcludeSelection;
import de.elmar_baumann.imv.controller.misc.ControllerLogfileDialog;
import de.elmar_baumann.imv.controller.misc.ControllerMenuItemEnabler;
import de.elmar_baumann.imv.controller.misc.ControllerShowSystemOutput;
import de.elmar_baumann.imv.controller.misc.ControllerThumbnailCountDisplay;
import de.elmar_baumann.imv.controller.search.ControllerCreateSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerDeleteSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerEditSafedSearch;
import de.elmar_baumann.imv.controller.search.ControllerRenameSavedSearch;
import de.elmar_baumann.imv.controller.search.ControllerSafedSearchSelected;
import de.elmar_baumann.imv.controller.tasks.ControllerArrayScheduledTasks;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCopyFilesToClipboard;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCreateMetadataOfCurrentThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCreateMetadataOfSelectedThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDeleteThumbnailsFromDatabase;
import de.elmar_baumann.imv.controller.thumbnail.ControllerPasteFilesFromClipboard;
import de.elmar_baumann.imv.controller.thumbnail.ControllerRenameInXmpColumns;
import de.elmar_baumann.imv.controller.thumbnail.ControllerRotateThumbnail;
import de.elmar_baumann.imv.controller.thumbnail.ControllerSliderThumbnailSize;
import de.elmar_baumann.imv.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerThumbnailsDatabaseChanges;
import de.elmar_baumann.imv.controller.thumbnail.ControllerThumbnailsPanelPersistence;
import de.elmar_baumann.imv.controller.timeline.ControllerTimelineItemSelected;

/**
 * Erzeugt alle Controller.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class ControllerFactory {

    static final ControllerFactory INSTANCE = new ControllerFactory();
    private boolean init = false;

    private void startScheduledTasks() {
        Thread thread = new Thread(new ControllerArrayScheduledTasks());
        thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
        thread.setName("ControllerFactory#startScheduledTasks"); // NOI18N
        thread.start();
    }

    synchronized void init() {
        Util.checkInit(ControllerFactory.class, init);
        if (!init) {
            new ControllerItemsMutualExcludeSelection();
            new ControllerCategoryItemSelected();
            new ControllerKeywordItemSelected();
            new ControllerFavoriteDirectorySelected();
            new ControllerDirectorySelected();
            new ControllerSafedSearchSelected();
            new ControllerImageCollectionSelected();
            new ControllerMenuItemEnabler();
            new ControllerThumbnailCountDisplay();
            new ControllerThumbnailsPanelPersistence();
            new ControllerCreateMetadataOfSelectedThumbnails();
            new ControllerCreateImageCollection();
            new ControllerDeleteImageCollection();
            new ControllerDeleteFromImageCollection();
            new ControllerAddToImageCollection();
            new ControllerRenameImageCollection();
            new ControllerCreateSavedSearch();
            new ControllerDeleteSavedSearch();
            new ControllerEditSafedSearch();
            new ControllerRenameSavedSearch();
            new ControllerRotateThumbnail();
            new ControllerOpenFilesWithStandardApp();
            new ControllerOpenFilesWithOtherApp();
            new ControllerDeleteThumbnailsFromDatabase();
            new ControllerCreateMetadataOfCurrentThumbnails();
            new ControllerLogfileDialog();
            new ControllerFastSearch();
            new ControllerAdvancedSearch();
            new ControllerShowMetadata();
            new ControllerThumbnailSelectionEditMetadata();
            new ControllerSaveMetadata();
            new ControllerEmptyMetadata();
            new ControllerMetadataTemplates();
            new ControllerEnableInsertMetadataTemplate();
            new ControllerInsertFavoriteDirectory();
            new ControllerDeleteFavoriteDirectory();
            new ControllerUpdateFavoriteDirectory();
            new ControllerCopyFilesToDirectory();
            new ControllerRenameInXmpColumns();
            new ControllerIptcToXmp();
            new ControllerGoTo();
            new ControllerSliderThumbnailSize();
            new ControllerDeleteFiles();
            new ControllerRenameFiles();
            new ControllerMoveFiles();
            new ControllerSortThumbnails();
            new ControllerThumbnailsDatabaseChanges();
            new ControllerAutocopyDirectory();
            new ControllerCopyFilesToClipboard();
            new ControllerDirectoryCopyFiles();
            new ControllerPasteFilesFromClipboard();
            new ControllerFavoriteDirectoryOpenInFolders();
            new ControllerActionsShowDialog();
            new ControllerActionExecutor();
            new ControllerExtractEmbeddedXmp();
            new ControllerShowSystemOutput();
            new ControllerTimelineItemSelected();
            startScheduledTasks();
            init = true;
        }
    }
}
