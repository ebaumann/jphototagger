package org.jphototagger.program.factory;

import org.jphototagger.lib.dialog.SystemOutputDialog;
import org.jphototagger.program.controller.actions.ActionsMenuUpdater;
import org.jphototagger.program.controller.directories.ControllerCreateDirectory;
import org.jphototagger.program.controller.directories.ControllerDeleteDirectory;
import org.jphototagger.program.controller.directories.ControllerDirectoryPasteFiles;
import org.jphototagger.program.controller.directories.ControllerDirectorySelected;
import org.jphototagger.program.controller.directories.ControllerRefreshDirectoryTree;
import org.jphototagger.program.controller.directories.ControllerRenameDirectory;
import org.jphototagger.program.controller.favorites.ControllerDeleteFavorite;
import org.jphototagger.program.controller.favorites.ControllerFavoritesAddFilesystemFolder;
import org.jphototagger.program.controller.favorites.ControllerFavoritesDeleteFilesystemFolder;
import org.jphototagger.program.controller.favorites.ControllerFavoriteSelected;
import org.jphototagger.program.controller.favorites.ControllerFavoritesRenameFilesystemFolder;
import org.jphototagger.program.controller.favorites.ControllerInsertFavorite;
import org.jphototagger.program.controller.favorites.ControllerMoveFavorite;
import org.jphototagger.program.controller.favorites.ControllerOpenFavoriteInFolders;
import org.jphototagger.program.controller.favorites.ControllerRefreshFavorites;
import org.jphototagger.program.controller.favorites.ControllerUpdateFavorite;
import org.jphototagger.program.controller.filesystem.ControllerCopyFilesToDirectory;
import org.jphototagger.program.controller.filesystem.ControllerDeleteFiles;
import org.jphototagger.program.controller.filesystem.ControllerImportImageFiles;
import org.jphototagger.program.controller.filesystem.ControllerMoveFiles;
import org.jphototagger.program.controller.filesystem.ControllerRenameFiles;
import org.jphototagger.program.controller.imagecollection.ControllerAddImageCollection;
import org.jphototagger.program.controller.imagecollection.ControllerAddToImageCollection;
import org.jphototagger.program.controller.imagecollection.ControllerDeleteFromImageCollection;
import org.jphototagger.program.controller.imagecollection.ControllerDeleteImageCollection;
import org.jphototagger.program.controller.imagecollection.ControllerImageCollectionSelected;
import org.jphototagger.program.controller.imagecollection.ControllerPickReject;
import org.jphototagger.program.controller.imagecollection.ControllerRenameImageCollection;
import org.jphototagger.program.controller.keywords.list.ControllerDeleteKeywords;
import org.jphototagger.program.controller.keywords.list.ControllerDeleteKeywordsFromEditPanel;
import org.jphototagger.program.controller.keywords.list.ControllerDisplayKeyword;
import org.jphototagger.program.controller.keywords.list.ControllerEditKeywordSynonyms;
import org.jphototagger.program.controller.keywords.list.ControllerInsertKeywords;
import org.jphototagger.program.controller.keywords.list.ControllerKeywordItemSelected;
import org.jphototagger.program.controller.keywords.list.ControllerRenameKeywords;
import org.jphototagger.program.controller.keywords.tree.ControllerAddKeyword;
import org.jphototagger.program.controller.keywords.tree.ControllerCopyCutPasteKeyword;
import org.jphototagger.program.controller.keywords.tree.ControllerDeleteKeywordFromEditPanel;
import org.jphototagger.program.controller.keywords.tree.ControllerExportKeywords;
import org.jphototagger.program.controller.keywords.tree.ControllerHighlightKeywordsTree;
import org.jphototagger.program.controller.keywords.tree.ControllerImportKeywords;
import org.jphototagger.program.controller.keywords.tree.ControllerKeywordsDbUpdates;
import org.jphototagger.program.controller.keywords.tree.ControllerKeywordsDisplayImages;
import org.jphototagger.program.controller.keywords.tree.ControllerKeywordsSelection;
import org.jphototagger.program.controller.keywords.tree.ControllerRenameKeyword;
import org.jphototagger.program.controller.keywords.tree.ControllerShowKeywordsDialog;
import org.jphototagger.program.controller.keywords.tree.ControllerToggleButtonSelKeywords;
import org.jphototagger.program.controller.keywords.tree.ControllerToggleRealKeyword;
import org.jphototagger.program.controller.metadata.ControllerCopyPasteMetadata;
import org.jphototagger.program.controller.metadata.ControllerEmptyMetadata;
import org.jphototagger.program.controller.metadata.ControllerEnableCreateMetadataTemplate;
import org.jphototagger.program.controller.metadata.ControllerEnableInsertMetadataTemplate;
import org.jphototagger.program.controller.metadata.ControllerExifToXmp;
import org.jphototagger.program.controller.metadata.ControllerExtractEmbeddedXmp;
import org.jphototagger.program.controller.metadata.ControllerIptcToXmp;
import org.jphototagger.program.controller.metadata.ControllerMetadataTemplates;
import org.jphototagger.program.controller.metadata.ControllerShowMetadata;
import org.jphototagger.program.controller.metadata.ControllerShowUpdateMetadataDialog;
import org.jphototagger.program.controller.metadata.ControllerThumbnailSelectionEditMetadata;
import org.jphototagger.program.controller.metadatatemplates.ControllerMetadataTemplateAdd;
import org.jphototagger.program.controller.metadatatemplates.ControllerMetadataTemplateDelete;
import org.jphototagger.program.controller.metadatatemplates.ControllerMetadataTemplateEdit;
import org.jphototagger.program.controller.metadatatemplates.ControllerMetadataTemplateRename;
import org.jphototagger.program.controller.metadatatemplates.ControllerMetadataTemplateSetToSelImages;
import org.jphototagger.program.controller.misc.ControllerAboutApp;
import org.jphototagger.program.controller.misc.ControllerBackupDatabase;
import org.jphototagger.program.controller.misc.ControllerGoTo;
import org.jphototagger.program.controller.misc.ControllerHelp;
import org.jphototagger.program.controller.misc.ControllerItemsMutualExcludeSelection;
import org.jphototagger.program.controller.misc.ControllerLogfileDialog;
import org.jphototagger.program.controller.misc.ControllerMaintainDatabase;
import org.jphototagger.program.controller.misc.ControllerMenuItemEnabler;
import org.jphototagger.program.controller.plugin.ControllerFileProcessorPlugins;
import org.jphototagger.program.controller.misc.ControllerShowSynonymsDialog;
import org.jphototagger.program.controller.misc.ControllerShowSystemOutput;
import org.jphototagger.program.controller.misc.ControllerShowUserSettingsDialog;
import org.jphototagger.program.controller.misc.ControllerThumbnailCountDisplay;
import org.jphototagger.program.controller.misc.ControllerUpdateCheck;
import org.jphototagger.program.controller.misc.SizeAndLocationController;
import org.jphototagger.program.controller.miscmetadata.ControllerAddMetadataToSelImages;
import org.jphototagger.program.controller.miscmetadata.ControllerDeleteMiscMetadata;
import org.jphototagger.program.controller.miscmetadata.ControllerMiscMetadataItemSelected;
import org.jphototagger.program.controller.miscmetadata.ControllerRemoveMetadataFromSelImages;
import org.jphototagger.program.controller.miscmetadata.ControllerRenameMiscMetadata;
import org.jphototagger.program.controller.nometadata.ControllerNoMetadataItemSelected;
import org.jphototagger.program.controller.programs.ControllerOpenFilesWithOtherApp;
import org.jphototagger.program.controller.programs.ControllerOpenFilesWithStandardApp;
import org.jphototagger.program.controller.rating.ControllerSetRating;
import org.jphototagger.program.controller.search.ControllerAdvancedSearch;
import org.jphototagger.program.controller.search.ControllerCreateSavedSearch;
import org.jphototagger.program.controller.search.ControllerDeleteSavedSearch;
import org.jphototagger.program.controller.search.ControllerEditSavedSearch;
import org.jphototagger.program.controller.search.ControllerFastSearch;
import org.jphototagger.program.controller.search.ControllerRenameSavedSearch;
import org.jphototagger.program.controller.search.ControllerSavedSearchSelected;
import org.jphototagger.program.controller.search.ControllerShowAdvancedSearchDialog;
import org.jphototagger.program.controller.thumbnail.ControllerCopyOrCutFilesToClipboard;
import org.jphototagger.program.controller.thumbnail.ControllerCreateMetadataOfDisplayedThumbnails;
import org.jphototagger.program.controller.thumbnail.ControllerCreateMetadataOfSelectedThumbnails;
import org.jphototagger.program.controller.thumbnail.ControllerDeleteThumbnailsFromDatabase;
import org.jphototagger.program.controller.thumbnail.ControllerPasteFilesFromClipboard;
import org.jphototagger.program.controller.thumbnail.ControllerRefreshThumbnailsPanel;
import org.jphototagger.program.controller.thumbnail.ControllerRotateThumbnail;
import org.jphototagger.program.controller.thumbnail.ControllerSliderThumbnailSize;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.controller.thumbnail.ControllerThumbnailFileFilter;
import org.jphototagger.program.controller.thumbnail.ControllerThumbnailsDatabaseChanges;
import org.jphototagger.program.controller.thumbnail.ControllerThumbnailsPanelPersistence;
import org.jphototagger.program.controller.thumbnail.ControllerThumbnailsSelectAllOrNothing;
import org.jphototagger.program.controller.thumbnail.ControllerToggleKeywordOverlay;
import org.jphototagger.program.controller.timeline.ControllerTimelineItemSelected;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;
import java.util.List;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.controller.metadata.ControllerDisplayIptcUserSettings;

/**
 * Erzeugt alle Controller.
 *
 * @author Elmar Baumann
 */
public final class ControllerFactory {

    public static final ControllerFactory INSTANCE = new ControllerFactory();
    private final Support support = new Support();
    private volatile boolean init;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                Support.setStatusbarInfo("ControllerFactory.Init.Start");
                addAppWindowMenuItemControllers();
                addAppWindowSelectionControllers();
                addImageCollectionControllers();
                addSavedSearchControllers();
                addSearchControllers();
                addThumbnailsPanelControllers();
                addMetadataEditPanelsControllers();
                addFavoritesControllers();
                addMetadataTablesControllers();
                addDirectoryTreeControllers();
                addMetadataTemplatesControllers();
                addMiscMetadataControllers();
                addKeywordsControllers();
                addMiscControllers();
                addSizeAndLocationController();
                Support.setStatusbarInfo("ControllerFactory.Init.Finished");
            }
        });
    }

    private void addAppWindowMenuItemControllers() {
        support.add(new ControllerMenuItemEnabler());
        support.add(new ControllerLogfileDialog());
        support.add(new ControllerGoTo());
        support.add(new ControllerToggleKeywordOverlay());
        support.add(new ControllerSliderThumbnailSize());
        support.add(new ControllerSortThumbnails());
        support.add(new ControllerImportImageFiles());
        support.add(new ControllerExtractEmbeddedXmp());
        support.add(new ControllerShowSystemOutput());
        support.add(new ControllerImportKeywords());
        support.add(new ControllerExportKeywords());
        support.add(new ControllerAboutApp());
        support.add(new ControllerHelp());
        support.add(new ControllerMaintainDatabase());
        support.add(new ControllerShowUpdateMetadataDialog());
        support.add(new ControllerShowUserSettingsDialog());
        support.add(new ControllerShowAdvancedSearchDialog());
        support.add(new ControllerShowSynonymsDialog());
        support.add(new ControllerEditKeywordSynonyms());
        support.add(new ControllerBackupDatabase());
    }

    private void addAppWindowSelectionControllers() {
        support.add(new ControllerItemsMutualExcludeSelection());
        support.add(new ControllerKeywordItemSelected());
        support.add(new ControllerSavedSearchSelected());
        support.add(new ControllerImageCollectionSelected());
        support.add(new ControllerCreateMetadataOfSelectedThumbnails());
        support.add(new ControllerThumbnailSelectionEditMetadata());
        support.add(new ControllerNoMetadataItemSelected());
        support.add(new ControllerKeywordsSelection());
        support.add(new ControllerMiscMetadataItemSelected());
        support.add(new ControllerTimelineItemSelected());
        support.add(new ControllerFavoriteSelected());
        support.add(new ControllerDirectorySelected());
        support.add(new ControllerThumbnailsSelectAllOrNothing());
    }

    private void addImageCollectionControllers() {
        support.add(new ControllerAddImageCollection());
        support.add(new ControllerDeleteImageCollection());
        support.add(new ControllerDeleteFromImageCollection());
        support.add(new ControllerAddToImageCollection());
        support.add(new ControllerRenameImageCollection());
    }

    private void addSavedSearchControllers() {
        support.add(new ControllerCreateSavedSearch());
        support.add(new ControllerDeleteSavedSearch());
        support.add(new ControllerEditSavedSearch());
        support.add(new ControllerRenameSavedSearch());
    }

    private void addSearchControllers() {
        support.add(new ControllerFastSearch());
        support.add(new ControllerAdvancedSearch());
    }

    private void addThumbnailsPanelControllers() {
        support.add(new ControllerThumbnailsPanelPersistence());
        support.add(new ControllerThumbnailCountDisplay());
        support.add(new ControllerRotateThumbnail());
        support.add(new ControllerOpenFilesWithStandardApp());
        support.add(new ControllerOpenFilesWithOtherApp());
        support.add(new ControllerDeleteThumbnailsFromDatabase());
        support.add(new ControllerCreateMetadataOfDisplayedThumbnails());
        support.add(new ControllerCopyFilesToDirectory());
        support.add(new ControllerDeleteFiles());
        support.add(new ControllerRenameFiles());
        support.add(new ControllerMoveFiles());
        support.add(new ControllerThumbnailsDatabaseChanges());
        support.add(new ControllerCopyOrCutFilesToClipboard());
        support.add(new ControllerPasteFilesFromClipboard());
        support.add(new ControllerRefreshThumbnailsPanel());
        support.add(new ControllerPickReject());
        support.add(new ControllerSetRating());
        support.add(new ControllerCopyPasteMetadata());
        support.add(new ActionsMenuUpdater());
        support.add(new ControllerThumbnailFileFilter());
    }

    private void addMetadataEditPanelsControllers() {
        support.add(new ControllerEmptyMetadata());
        support.add(new ControllerEnableInsertMetadataTemplate());
        support.add(new ControllerShowKeywordsDialog());
    }

    private void addMiscMetadataControllers() {
        PopupMenuMiscMetadata popupAppWindow = new PopupMenuMiscMetadata(GUI.getAppPanel().getTreeMiscMetadata());

        support.add(new ControllerDeleteMiscMetadata(popupAppWindow));
        support.add(new ControllerRenameMiscMetadata(popupAppWindow));
        support.add(new ControllerAddMetadataToSelImages(popupAppWindow));
        support.add(new ControllerRemoveMetadataFromSelImages(popupAppWindow));

        PopupMenuMiscMetadata popupInputHelper =
                new PopupMenuMiscMetadata(InputHelperDialog.INSTANCE.getPanelMiscXmpMetadata().getTree());

        support.add(new ControllerDeleteMiscMetadata(popupInputHelper));
        support.add(new ControllerRenameMiscMetadata(popupInputHelper));
        support.add(new ControllerAddMetadataToSelImages(popupInputHelper));
        support.add(new ControllerRemoveMetadataFromSelImages(popupInputHelper));
        support.add(new ControllerUpdateCheck());
    }

    private void addFavoritesControllers() {
        support.add(new ControllerInsertFavorite());
        support.add(new ControllerDeleteFavorite());
        support.add(new ControllerUpdateFavorite());
        support.add(new ControllerOpenFavoriteInFolders());
        support.add(new ControllerMoveFavorite());
        support.add(new ControllerRefreshFavorites());
        support.add(new ControllerFavoritesAddFilesystemFolder());
        support.add(new ControllerFavoritesRenameFilesystemFolder());
        support.add(new ControllerFavoritesDeleteFilesystemFolder());
    }

    private void addMetadataTablesControllers() {
        support.add(new ControllerShowMetadata());
        support.add(new ControllerIptcToXmp());
        support.add(new ControllerExifToXmp());
        support.add(new ControllerDisplayIptcUserSettings());
    }

    private void addDirectoryTreeControllers() {
        support.add(new ControllerDirectoryPasteFiles());
        support.add(new ControllerCreateDirectory());
        support.add(new ControllerRenameDirectory());
        support.add(new ControllerDeleteDirectory());
        support.add(new ControllerRefreshDirectoryTree());
    }

    private void addKeywordsControllers() {
        KeywordsPanel[] keywordPanels = {GUI.getAppPanel().getPanelEditKeywords(),
            InputHelperDialog.INSTANCE.getPanelKeywords()};

        for (KeywordsPanel keywordsPanel : keywordPanels) {
            support.add(new ControllerToggleRealKeyword(keywordsPanel));
            support.add(new ControllerRenameKeyword(keywordsPanel));
            support.add(new ControllerAddKeyword(keywordsPanel));
            support.add(new org.jphototagger.program.controller.keywords.tree.ControllerDeleteKeywords(keywordsPanel));
            support.add(
                    new org.jphototagger.program.controller.keywords.tree.ControllerAddKeywordsToEditPanel(keywordsPanel));
            support.add(new ControllerDeleteKeywordFromEditPanel(keywordsPanel));
            support.add(new ControllerCopyCutPasteKeyword(keywordsPanel));
            support.add(new ControllerKeywordsDisplayImages());
        }

        support.add(new ControllerHighlightKeywordsTree());
        support.add(new ControllerKeywordsDbUpdates());
        support.add(new ControllerRenameKeywords());
        support.add(new ControllerDeleteKeywords());
        support.add(new ControllerToggleButtonSelKeywords());
        support.add(new ControllerDisplayKeyword());
        support.add(new ControllerInsertKeywords());
        support.add(new org.jphototagger.program.controller.keywords.list.ControllerAddKeywordsToEditPanel());
        support.add(new ControllerDeleteKeywordsFromEditPanel());
    }

    private void addMetadataTemplatesControllers() {
        support.add(new ControllerMetadataTemplateSetToSelImages());
        support.add(new ControllerMetadataTemplateAdd());
        support.add(new ControllerMetadataTemplateEdit());
        support.add(new ControllerMetadataTemplateDelete());
        support.add(new ControllerMetadataTemplateRename());
        support.add(new ControllerMetadataTemplates());
        support.add(new ControllerEnableCreateMetadataTemplate());
    }

    private void addMiscControllers() {
        support.add(new ControllerFileProcessorPlugins());
    }

    private void addSizeAndLocationController() {
        SizeAndLocationController sizeLocCtrl = new SizeAndLocationController();

        SystemOutputDialog.INSTANCE.addWindowListener(sizeLocCtrl);
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
        if (controllerClass == null) {
            throw new NullPointerException("controllerClass == null");
        }

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
        if (controllerClass == null) {
            throw new NullPointerException("controllerClass == null");
        }

        return support.getFirst(controllerClass);
    }
}
