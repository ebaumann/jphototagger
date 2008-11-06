package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
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
import de.elmar_baumann.imv.controller.search.ControllerFastSearch;
import de.elmar_baumann.imv.controller.misc.ControllerGoto;
import de.elmar_baumann.imv.controller.metadata.ControllerIptcToXmp;
import de.elmar_baumann.imv.controller.misc.ControllerItemsMutualExcludeSelection;
import de.elmar_baumann.imv.controller.misc.ControllerLogfileDialog;
import de.elmar_baumann.imv.controller.thumbnail.ControllerMenuItemEnabler;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Erzeugt alle Controller.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class ControllerFactory {

    private List<Controller> controllers = new ArrayList<Controller>();
    private ControllerArrayScheduledTasks controllerScheduledTasks;
    private static ControllerFactory instance = new ControllerFactory();

    static ControllerFactory getInstance() {
        return instance;
    }

    void setControl(boolean control) {
        for (Controller controller : controllers) {
            controller.setControl(control);
        }
        if (control) {
            startScheduledTasks();
        }
    }

    private void startScheduledTasks() {
        Thread thread = new Thread(controllerScheduledTasks);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
    }

    private ControllerFactory() {
        createController();
    }

    private void createController() {
        controllers.add(new ControllerItemsMutualExcludeSelection());
        controllers.add(new ControllerMenuItemEnabler());
        controllers.add(new ControllerThumbnailCountDisplay());
        controllers.add(new ControllerThumbnailsPanelPersistence());
        controllers.add(new ControllerCreateMetadataOfSelectedThumbnails());
        controllers.add(new ControllerCreateImageCollection());
        controllers.add(new ControllerDeleteImageCollection());
        controllers.add(new ControllerDeleteFromImageCollection());
        controllers.add(new ControllerAddToImageCollection());
        controllers.add(new ControllerRenameImageCollection());
        controllers.add(new ControllerCreateSavedSearch());
        controllers.add(new ControllerDeleteSavedSearch());
        controllers.add(new ControllerEditSafedSearch());
        controllers.add(new ControllerRenameSavedSearch());
        controllers.add(new ControllerRotateThumbnail());
        controllers.add(new ControllerOpenFilesWithStandardApp());
        controllers.add(new ControllerOpenFilesWithOtherApp());
        controllers.add(new ControllerDeleteThumbnailsFromDatabase());
        controllers.add(new ControllerCreateMetadataOfCurrentThumbnails());
        controllers.add(new ControllerSafedSearchSelected());
        controllers.add(new ControllerImageCollectionSelected());
        controllers.add(new ControllerLogfileDialog());
        controllers.add(new ControllerFastSearch());
        controllers.add(new ControllerAdvancedSearch());
        controllers.add(new ControllerShowMetadata());
        controllers.add(new ControllerThumbnailSelectionEditMetadata());
        controllers.add(new ControllerSaveMetadata());
        controllers.add(new ControllerEmptyMetadata());
        controllers.add(new ControllerMetadataTemplates());
        controllers.add(new ControllerEnableInsertMetadataTemplate());
        controllers.add(new ControllerCategoryItemSelected());
        controllers.add(new ControllerKeywordItemSelected());
        controllers.add(new ControllerFavoriteDirectorySelected());
        controllers.add(new ControllerDirectorySelected());
        controllers.add(new ControllerInsertFavoriteDirectory());
        controllers.add(new ControllerDeleteFavoriteDirectory());
        controllers.add(new ControllerUpdateFavoriteDirectory());
        controllers.add(new ControllerCopyFilesToDirectory());
        controllers.add(new ControllerRenameInXmpColumns());
        controllers.add(new ControllerIptcToXmp());
        controllers.add(new ControllerGoto());
        controllers.add(new ControllerSliderThumbnailSize());
        controllers.add(new ControllerDeleteFiles());
        controllers.add(new ControllerRenameFiles());
        controllers.add(new ControllerMoveFiles());
        controllers.add(new ControllerSortThumbnails());
        controllers.add(new ControllerThumbnailsDatabaseChanges());
        controllers.add(new ControllerAutocopyDirectory());
        controllers.add(new ControllerCopyFilesToClipboard());
        controllers.add(new ControllerDirectoryCopyFiles());
        controllers.add(new ControllerPasteFilesFromClipboard());
        controllers.add(new ControllerFavoriteDirectoryOpenInFolders());
        controllers.add(new ControllerActionsShowDialog());
        controllers.add(new ControllerActionExecutor());
        controllerScheduledTasks = new ControllerArrayScheduledTasks();
        controllers.add(controllerScheduledTasks);
    }
}
