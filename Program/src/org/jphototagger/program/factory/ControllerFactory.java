package org.jphototagger.program.factory;

import java.util.List;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.logging.ErrorLogHandler;
import org.jphototagger.program.help.ShowAboutJPhotoTaggerAction;
import org.jphototagger.program.misc.GoToController;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.misc.MaximumOneTreeOrListItemSelectedController;
import org.jphototagger.program.misc.MenuItemEnablerController;
import org.jphototagger.program.module.actions.ActionsMenuUpdater;
import org.jphototagger.program.module.directories.CreateDirectoryController;
import org.jphototagger.program.module.directories.DeleteDirectoryController;
import org.jphototagger.program.module.directories.DirectorySelectedController;
import org.jphototagger.program.module.directories.OpenDirectoryInDesktopController;
import org.jphototagger.program.module.directories.PasteFilesIntoDirectoryController;
import org.jphototagger.program.module.directories.RefreshDirectoryTreeController;
import org.jphototagger.program.module.directories.RenameDirectoryController;
import org.jphototagger.program.module.favorites.AddFilesystemFolderToFavoritesController;
import org.jphototagger.program.module.favorites.DeleteFavoriteController;
import org.jphototagger.program.module.favorites.DeleteFilesystemFolderFromFavoritesController;
import org.jphototagger.program.module.favorites.FavoriteSelectedController;
import org.jphototagger.program.module.favorites.InsertFavoriteController;
import org.jphototagger.program.module.favorites.MoveFavoriteController;
import org.jphototagger.program.module.favorites.OpenFavoriteInDirectoriesTreeController;
import org.jphototagger.program.module.favorites.RefreshFavoritesController;
import org.jphototagger.program.module.favorites.RenameFilesystemFolderInFavoritesController;
import org.jphototagger.program.module.favorites.UpdateFavoriteController;
import org.jphototagger.program.module.filesystem.CopyFilesToDirectoryController;
import org.jphototagger.program.module.filesystem.DeleteFilesController;
import org.jphototagger.program.module.filesystem.MoveFilesController;
import org.jphototagger.program.module.filesystem.RenameFilesController;
import org.jphototagger.program.module.imagecollections.AddImageCollectionController;
import org.jphototagger.program.module.imagecollections.AddToImageCollectionController;
import org.jphototagger.program.module.imagecollections.DeleteFromImageCollectionController;
import org.jphototagger.program.module.imagecollections.DeleteImageCollectionController;
import org.jphototagger.program.module.imagecollections.ImageCollectionSelectedController;
import org.jphototagger.program.module.imagecollections.PickRejectController;
import org.jphototagger.program.module.imagecollections.RenameImageCollectionController;
import org.jphototagger.program.module.keywords.KeywordsPanel;
import org.jphototagger.program.module.keywords.list.DeleteKeywordsController;
import org.jphototagger.program.module.keywords.list.DeleteKeywordsFromEditPanelController;
import org.jphototagger.program.module.keywords.list.DisplayKeywordController;
import org.jphototagger.program.module.keywords.list.EditKeywordSynonymsController;
import org.jphototagger.program.module.keywords.list.InsertKeywordsController;
import org.jphototagger.program.module.keywords.list.KeywordListItemSelectedController;
import org.jphototagger.program.module.keywords.list.RenameKeywordsController;
import org.jphototagger.program.module.keywords.tree.AddKeywordToTreeController;
import org.jphototagger.program.module.keywords.tree.CopyCutPasteKeywordController;
import org.jphototagger.program.module.keywords.tree.DeleteKeywordFromEditPanelController;
import org.jphototagger.program.module.keywords.tree.HighlightKeywordsTreeController;
import org.jphototagger.program.module.keywords.tree.KeywordsDisplayImagesController;
import org.jphototagger.program.module.keywords.tree.KeywordsRepositoryUpdatesController;
import org.jphototagger.program.module.keywords.tree.KeywordsSelectionController;
import org.jphototagger.program.module.keywords.tree.RenameKeywordController;
import org.jphototagger.program.module.keywords.tree.ToggleButtonExpandKeywordsTreeController;
import org.jphototagger.program.module.keywords.tree.ToggleRealKeywordController;
import org.jphototagger.program.module.metadatatemplates.AddMetadataTemplateController;
import org.jphototagger.program.module.metadatatemplates.DeleteMetadataTemplateController;
import org.jphototagger.program.module.metadatatemplates.EditMetadataTemplateController;
import org.jphototagger.program.module.metadatatemplates.RenameMetadataTemplateController;
import org.jphototagger.program.module.metadatatemplates.SetMetadataTemplateToSelectedImagesController;
import org.jphototagger.program.module.miscmetadata.AddMetadataToSelectedImagesController;
import org.jphototagger.program.module.miscmetadata.DeleteMiscMetadataController;
import org.jphototagger.program.module.miscmetadata.MiscMetadataItemSelectedController;
import org.jphototagger.program.module.miscmetadata.MiscMetadataPopupMenu;
import org.jphototagger.program.module.miscmetadata.RemoveMetadataFromSelectedImagesController;
import org.jphototagger.program.module.miscmetadata.RenameMiscMetadataController;
import org.jphototagger.program.module.programs.OpenFilesWithOtherAppController;
import org.jphototagger.program.module.programs.OpenFilesWithStandardAppController;
import org.jphototagger.program.module.rating.SetRatingController;
import org.jphototagger.program.module.search.AdvancedSearchController;
import org.jphototagger.program.module.search.CreateSavedSearchController;
import org.jphototagger.program.module.search.DeleteSavedSearchController;
import org.jphototagger.program.module.search.EditSavedSearchController;
import org.jphototagger.program.module.search.RenameSavedSearchController;
import org.jphototagger.program.module.search.SavedSearchSelectedController;
import org.jphototagger.program.module.search.ShowAdvancedSearchDialogAction;
import org.jphototagger.program.module.thumbnails.CopyOrCutFilesToClipboardController;
import org.jphototagger.program.module.thumbnails.CopyPasteMetadataController;
import org.jphototagger.program.module.thumbnails.CreateMetadataOfDisplayedThumbnailsController;
import org.jphototagger.program.module.thumbnails.CreateMetadataOfSelectedThumbnailsController;
import org.jphototagger.program.module.thumbnails.DeleteThumbnailsFromRepositoryController;
import org.jphototagger.program.module.thumbnails.PasteFilesFromClipboardController;
import org.jphototagger.program.module.thumbnails.RefreshThumbnailsPanelController;
import org.jphototagger.program.module.thumbnails.RotateThumbnailsController;
import org.jphototagger.program.module.thumbnails.SelectOrDeselectAllThumbnailsController;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanelPersistenceController;
import org.jphototagger.program.module.thumbnails.ThumbnailsRepositoryChangesController;
import org.jphototagger.program.module.timeline.TimelineItemSelectedController;
import org.jphototagger.program.plugins.FileProcessorPluginsController;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.ShowUserSettingsDialogAction;

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
                String message = Bundle.getString(ControllerFactory.class, "ControllerFactory.Init.Start");
                Support.setStatusbarInfo(message);
                addAppWindowMenuItemControllers();
                addAppWindowSelectionControllers();
                addImageCollectionControllers();
                addSavedSearchControllers();
                addSearchControllers();
                addThumbnailsPanelControllers();
                addFavoritesControllers();
                addDirectoryTreeControllers();
                addMetadataTemplatesControllers();
                addMiscMetadataControllers();
                addKeywordsControllers();
                addMiscControllers();
                message = Bundle.getString(ControllerFactory.class, "ControllerFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private void addAppWindowMenuItemControllers() {
        support.add(new MenuItemEnablerController());
        support.add(new ErrorLogHandler());
        support.add(new GoToController());
        support.add(new ShowAboutJPhotoTaggerAction());
        support.add(new ShowUserSettingsDialogAction());
        support.add(new ShowAdvancedSearchDialogAction());
        support.add(new EditKeywordSynonymsController());
    }

    private void addAppWindowSelectionControllers() {
        support.add(new MaximumOneTreeOrListItemSelectedController());
        support.add(new KeywordListItemSelectedController());
        support.add(new SavedSearchSelectedController());
        support.add(new ImageCollectionSelectedController());
        support.add(new CreateMetadataOfSelectedThumbnailsController());
        support.add(new KeywordsSelectionController());
        support.add(new MiscMetadataItemSelectedController());
        support.add(new TimelineItemSelectedController());
        support.add(new FavoriteSelectedController());
        support.add(new DirectorySelectedController());
        support.add(new SelectOrDeselectAllThumbnailsController());
    }

    private void addImageCollectionControllers() {
        support.add(new AddImageCollectionController());
        support.add(new DeleteImageCollectionController());
        support.add(new DeleteFromImageCollectionController());
        support.add(new AddToImageCollectionController());
        support.add(new RenameImageCollectionController());
    }

    private void addSavedSearchControllers() {
        support.add(new CreateSavedSearchController());
        support.add(new DeleteSavedSearchController());
        support.add(new EditSavedSearchController());
        support.add(new RenameSavedSearchController());
    }

    private void addSearchControllers() {
        support.add(new AdvancedSearchController());
    }

    private void addThumbnailsPanelControllers() {
        support.add(new ThumbnailsPanelPersistenceController());
        support.add(new RotateThumbnailsController());
        support.add(new OpenFilesWithStandardAppController());
        support.add(new OpenFilesWithOtherAppController());
        support.add(new DeleteThumbnailsFromRepositoryController());
        support.add(new CreateMetadataOfDisplayedThumbnailsController());
        support.add(new CopyFilesToDirectoryController());
        support.add(new DeleteFilesController());
        support.add(new RenameFilesController());
        support.add(new MoveFilesController());
        support.add(new ThumbnailsRepositoryChangesController());
        support.add(new CopyOrCutFilesToClipboardController());
        support.add(new PasteFilesFromClipboardController());
        support.add(new RefreshThumbnailsPanelController());
        support.add(new PickRejectController());
        support.add(new SetRatingController());
        support.add(new CopyPasteMetadataController());
        support.add(new ActionsMenuUpdater());
    }

    private void addMiscMetadataControllers() {
        MiscMetadataPopupMenu popupAppWindow = new MiscMetadataPopupMenu(GUI.getAppPanel().getTreeMiscMetadata());

        support.add(new DeleteMiscMetadataController(popupAppWindow));
        support.add(new RenameMiscMetadataController(popupAppWindow));
        support.add(new AddMetadataToSelectedImagesController(popupAppWindow));
        support.add(new RemoveMetadataFromSelectedImagesController(popupAppWindow));

        MiscMetadataPopupMenu popupInputHelper =
                new MiscMetadataPopupMenu(InputHelperDialog.INSTANCE.getPanelMiscXmpMetadata().getTree());

        support.add(new DeleteMiscMetadataController(popupInputHelper));
        support.add(new RenameMiscMetadataController(popupInputHelper));
        support.add(new AddMetadataToSelectedImagesController(popupInputHelper));
        support.add(new RemoveMetadataFromSelectedImagesController(popupInputHelper));
    }

    private void addFavoritesControllers() {
        support.add(new InsertFavoriteController());
        support.add(new DeleteFavoriteController());
        support.add(new UpdateFavoriteController());
        support.add(new OpenFavoriteInDirectoriesTreeController());
        support.add(new MoveFavoriteController());
        support.add(new RefreshFavoritesController());
        support.add(new AddFilesystemFolderToFavoritesController());
        support.add(new RenameFilesystemFolderInFavoritesController());
        support.add(new DeleteFilesystemFolderFromFavoritesController());
    }

    private void addDirectoryTreeControllers() {
        support.add(new PasteFilesIntoDirectoryController());
        support.add(new CreateDirectoryController());
        support.add(new RenameDirectoryController());
        support.add(new DeleteDirectoryController());
        support.add(new RefreshDirectoryTreeController());
        support.add(new OpenDirectoryInDesktopController());
    }

    private void addKeywordsControllers() {
        KeywordsPanel[] keywordPanels = {GUI.getAppPanel().getPanelEditKeywords(),
            InputHelperDialog.INSTANCE.getPanelKeywords()};

        for (KeywordsPanel keywordsPanel : keywordPanels) {
            support.add(new ToggleRealKeywordController(keywordsPanel));
            support.add(new RenameKeywordController(keywordsPanel));
            support.add(new AddKeywordToTreeController(keywordsPanel));
            support.add(new org.jphototagger.program.module.keywords.tree.DeleteKeywordsFromTreeController(keywordsPanel));
            support.add(
                    new org.jphototagger.program.module.keywords.tree.AddKeywordsToEditPanelController(keywordsPanel));
            support.add(new DeleteKeywordFromEditPanelController(keywordsPanel));
            support.add(new CopyCutPasteKeywordController(keywordsPanel));
            support.add(new KeywordsDisplayImagesController());
        }

        support.add(new HighlightKeywordsTreeController());
        support.add(new KeywordsRepositoryUpdatesController());
        support.add(new RenameKeywordsController());
        support.add(new DeleteKeywordsController());
        support.add(new ToggleButtonExpandKeywordsTreeController());
        support.add(new DisplayKeywordController());
        support.add(new InsertKeywordsController());
        support.add(new org.jphototagger.program.module.keywords.list.AddKeywordsToEditPanelController());
        support.add(new DeleteKeywordsFromEditPanelController());
    }

    private void addMetadataTemplatesControllers() {
        support.add(new SetMetadataTemplateToSelectedImagesController());
        support.add(new AddMetadataTemplateController());
        support.add(new EditMetadataTemplateController());
        support.add(new DeleteMetadataTemplateController());
        support.add(new RenameMetadataTemplateController());
    }

    private void addMiscControllers() {
        support.add(new FileProcessorPluginsController());
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
