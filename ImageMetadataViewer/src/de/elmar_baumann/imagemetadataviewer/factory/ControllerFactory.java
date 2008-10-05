package de.elmar_baumann.imagemetadataviewer.factory;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.controller.categories.ControllerCategoryItemSelected;
import de.elmar_baumann.imagemetadataviewer.controller.directories.ControllerEnableInsertMetaDataTemplate;
import de.elmar_baumann.imagemetadataviewer.controller.directories.ControllerShowThumbnailsOfSelectedDirectory;
import de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories.ControllerDeleteFavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories.ControllerFavoriteDirectoryItemSelected;
import de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories.ControllerInsertFavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories.ControllerMoveFavoriteItemDown;
import de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories.ControllerMoveFavoriteItemUp;
import de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories.ControllerUpdateFavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.controller.files.ControllerCopyFilesToDirectory;
import de.elmar_baumann.imagemetadataviewer.controller.files.ControllerOpenFilesWithOtherApp;
import de.elmar_baumann.imagemetadataviewer.controller.files.ControllerOpenFilesWithStandardApp;
import de.elmar_baumann.imagemetadataviewer.controller.imagecollection.ControllerAddToImageCollection;
import de.elmar_baumann.imagemetadataviewer.controller.imagecollection.ControllerCreateImageCollection;
import de.elmar_baumann.imagemetadataviewer.controller.imagecollection.ControllerDeleteFromImageCollection;
import de.elmar_baumann.imagemetadataviewer.controller.imagecollection.ControllerDeleteImageCollection;
import de.elmar_baumann.imagemetadataviewer.controller.imagecollection.ControllerImageCollectionSelected;
import de.elmar_baumann.imagemetadataviewer.controller.imagecollection.ControllerRenameImageCollection;
import de.elmar_baumann.imagemetadataviewer.controller.metadata.ControllerMetaDataTemplates;
import de.elmar_baumann.imagemetadataviewer.controller.metadata.ControllerSaveMetaData;
import de.elmar_baumann.imagemetadataviewer.controller.metadata.ControllerShowMetadata;
import de.elmar_baumann.imagemetadataviewer.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerAdvancedSearch;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerFastSearch;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerIptcToXmp;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerItemsMutualExcludeSelection;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerLogfileDialog;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerThumbnailCountDisplay;
import de.elmar_baumann.imagemetadataviewer.controller.savedsearch.ControllerCreateSavedSearch;
import de.elmar_baumann.imagemetadataviewer.controller.savedsearch.ControllerDeleteSavedSearch;
import de.elmar_baumann.imagemetadataviewer.controller.savedsearch.ControllerEditSafedSearch;
import de.elmar_baumann.imagemetadataviewer.controller.savedsearch.ControllerRenameSavedSearch;
import de.elmar_baumann.imagemetadataviewer.controller.savedsearch.ControllerSafedSearchSelected;
import de.elmar_baumann.imagemetadataviewer.controller.tasks.ControllerArrayScheduledTasks;
import de.elmar_baumann.imagemetadataviewer.controller.thumbnail.ControllerCreateMetaDataOfCurrentThumbnails;
import de.elmar_baumann.imagemetadataviewer.controller.thumbnail.ControllerCreateMetaDataOfSelectedThumbnails;
import de.elmar_baumann.imagemetadataviewer.controller.thumbnail.ControllerDeleteThumbnailsFromDatabase;
import de.elmar_baumann.imagemetadataviewer.controller.thumbnail.ControllerRenameInXmpColumns;
import de.elmar_baumann.imagemetadataviewer.controller.thumbnail.ControllerRotateThumbnail;
import java.util.ArrayList;

/**
 * Erzeugt alle Controller.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class ControllerFactory {

    private ArrayList<Controller> controllers = new ArrayList<Controller>();
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
        controllers.add(new ControllerFavoriteDirectoryItemSelected());
        controllers.add(new ControllerShowThumbnailsOfSelectedDirectory());
        controllers.add(new ControllerInsertFavoriteDirectory());
        controllers.add(new ControllerDeleteFavoriteDirectory());
        controllers.add(new ControllerUpdateFavoriteDirectory());
        controllers.add(new ControllerMoveFavoriteItemUp());
        controllers.add(new ControllerMoveFavoriteItemDown());
        controllers.add(new ControllerCopyFilesToDirectory());
        controllers.add(new ControllerRenameInXmpColumns());
        controllers.add(new ControllerIptcToXmp());
        controllerScheduledTasks = new ControllerArrayScheduledTasks();
        controllers.add(controllerScheduledTasks);
    }
}
