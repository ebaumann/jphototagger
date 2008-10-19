package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.controller.categories.ControllerCategoryItemSelected;
import de.elmar_baumann.imv.controller.directories.ControllerEnableInsertMetaDataTemplate;
import de.elmar_baumann.imv.controller.directories.ControllerDirectorySelected;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerDeleteFavoriteDirectory;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerFavoriteDirectorySelected;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerInsertFavoriteDirectory;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerMoveFavoriteItemDown;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerMoveFavoriteItemUp;
import de.elmar_baumann.imv.controller.favoritedirectories.ControllerUpdateFavoriteDirectory;
import de.elmar_baumann.imv.controller.files.ControllerCopyFilesToDirectory;
import de.elmar_baumann.imv.controller.files.ControllerOpenFilesWithOtherApp;
import de.elmar_baumann.imv.controller.files.ControllerOpenFilesWithStandardApp;
import de.elmar_baumann.imv.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.imv.controller.filesystem.ControllerRenameFiles;
import de.elmar_baumann.imv.controller.imagecollection.ControllerAddToImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerCreateImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerDeleteFromImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerDeleteImageCollection;
import de.elmar_baumann.imv.controller.imagecollection.ControllerImageCollectionSelected;
import de.elmar_baumann.imv.controller.imagecollection.ControllerRenameImageCollection;
import de.elmar_baumann.imv.controller.metadata.ControllerMetaDataTemplates;
import de.elmar_baumann.imv.controller.metadata.ControllerSaveMetaData;
import de.elmar_baumann.imv.controller.metadata.ControllerShowMetadata;
import de.elmar_baumann.imv.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import de.elmar_baumann.imv.controller.misc.ControllerAdvancedSearch;
import de.elmar_baumann.imv.controller.misc.ControllerAutocopyDirectory;
import de.elmar_baumann.imv.controller.misc.ControllerFastSearch;
import de.elmar_baumann.imv.controller.misc.ControllerGoto;
import de.elmar_baumann.imv.controller.misc.ControllerIptcToXmp;
import de.elmar_baumann.imv.controller.misc.ControllerItemsMutualExcludeSelection;
import de.elmar_baumann.imv.controller.misc.ControllerLogfileDialog;
import de.elmar_baumann.imv.controller.misc.ControllerThumbnailCountDisplay;
import de.elmar_baumann.imv.controller.savedsearch.ControllerCreateSavedSearch;
import de.elmar_baumann.imv.controller.savedsearch.ControllerDeleteSavedSearch;
import de.elmar_baumann.imv.controller.savedsearch.ControllerEditSafedSearch;
import de.elmar_baumann.imv.controller.savedsearch.ControllerRenameSavedSearch;
import de.elmar_baumann.imv.controller.savedsearch.ControllerSafedSearchSelected;
import de.elmar_baumann.imv.controller.tasks.ControllerArrayScheduledTasks;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCreateMetaDataOfCurrentThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerCreateMetaDataOfSelectedThumbnails;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDeleteThumbnailsFromDatabase;
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

    void startController() {
        for (Controller controller : controllers) {
            controller.start();
        }
        startScheduledTasks();
    }

    private void startScheduledTasks() {
        Thread thread = new Thread(controllerScheduledTasks);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
    }

    void stopController() {
        for (Controller controller : controllers) {
            controller.stop();
        }
        controllerScheduledTasks.stop();
    }

    private ControllerFactory() {
        createController();
    }

    private void createController() {
        controllers.add(new ControllerItemsMutualExcludeSelection());
        controllers.add(new ControllerThumbnailCountDisplay());
        controllers.add(new ControllerThumbnailsPanelPersistence());
        controllers.add(new ControllerCreateMetaDataOfSelectedThumbnails());
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
        controllers.add(new ControllerCreateMetaDataOfCurrentThumbnails());
        controllers.add(new ControllerSafedSearchSelected());
        controllers.add(new ControllerImageCollectionSelected());
        controllers.add(new ControllerLogfileDialog());
        controllers.add(new ControllerFastSearch());
        controllers.add(new ControllerAdvancedSearch());
        controllers.add(new ControllerShowMetadata());
        controllers.add(new ControllerThumbnailSelectionEditMetadata());
        controllers.add(new ControllerSaveMetaData());
        controllers.add(new ControllerMetaDataTemplates());
        controllers.add(new ControllerEnableInsertMetaDataTemplate());
        controllers.add(new ControllerCategoryItemSelected());
        controllers.add(new ControllerFavoriteDirectorySelected());
        controllers.add(new ControllerDirectorySelected());
        controllers.add(new ControllerInsertFavoriteDirectory());
        controllers.add(new ControllerDeleteFavoriteDirectory());
        controllers.add(new ControllerUpdateFavoriteDirectory());
        controllers.add(new ControllerMoveFavoriteItemUp());
        controllers.add(new ControllerMoveFavoriteItemDown());
        controllers.add(new ControllerCopyFilesToDirectory());
        controllers.add(new ControllerRenameInXmpColumns());
        controllers.add(new ControllerIptcToXmp());
        controllers.add(new ControllerGoto());
        controllers.add(new ControllerSliderThumbnailSize());
        controllers.add(new ControllerDeleteFiles());
        controllers.add(new ControllerRenameFiles());
        controllers.add(new ControllerSortThumbnails());
        controllers.add(new ControllerThumbnailsDatabaseChanges());
        controllers.add(new ControllerAutocopyDirectory());
        controllerScheduledTasks = new ControllerArrayScheduledTasks();
        controllers.add(controllerScheduledTasks);
    }
}
