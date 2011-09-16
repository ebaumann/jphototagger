package org.jphototagger.program.factory;

import java.util.List;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.logging.ErrorLogHandler;
import org.jphototagger.program.controller.actions.ActionsMenuUpdater;
import org.jphototagger.program.controller.directories.CreateDirectoryController;
import org.jphototagger.program.controller.directories.DeleteDirectoryController;
import org.jphototagger.program.controller.directories.DirectorySelectedController;
import org.jphototagger.program.controller.directories.PasteFilesIntoDirectoryController;
import org.jphototagger.program.controller.directories.RefreshDirectoryTreeController;
import org.jphototagger.program.controller.directories.RenameDirectoryController;
import org.jphototagger.program.controller.favorites.AddFilesystemFolderToFavoritesController;
import org.jphototagger.program.controller.favorites.DeleteFavoriteController;
import org.jphototagger.program.controller.favorites.DeleteFilesystemFolderFromFavoritesController;
import org.jphototagger.program.controller.favorites.FavoriteSelectedController;
import org.jphototagger.program.controller.favorites.InsertFavoriteController;
import org.jphototagger.program.controller.favorites.MoveFavoriteController;
import org.jphototagger.program.controller.favorites.OpenFavoriteInDirectoriesTreeController;
import org.jphototagger.program.controller.favorites.RefreshFavoritesController;
import org.jphototagger.program.controller.favorites.RenameFilesystemFolderInFavoritesController;
import org.jphototagger.program.controller.favorites.UpdateFavoriteController;
import org.jphototagger.program.controller.filesystem.CopyFilesToDirectoryController;
import org.jphototagger.program.controller.filesystem.DeleteFilesController;
import org.jphototagger.program.controller.filesystem.ImportImageFilesController;
import org.jphototagger.program.controller.filesystem.MoveFilesController;
import org.jphototagger.program.controller.filesystem.RenameFilesController;
import org.jphototagger.program.controller.imagecollection.AddImageCollectionController;
import org.jphototagger.program.controller.imagecollection.AddToImageCollectionController;
import org.jphototagger.program.controller.imagecollection.DeleteFromImageCollectionController;
import org.jphototagger.program.controller.imagecollection.DeleteImageCollectionController;
import org.jphototagger.program.controller.imagecollection.ImageCollectionSelectedController;
import org.jphototagger.program.controller.imagecollection.PickRejectController;
import org.jphototagger.program.controller.imagecollection.RenameImageCollectionController;
import org.jphototagger.program.controller.keywords.list.DeleteKeywordsController;
import org.jphototagger.program.controller.keywords.list.DeleteKeywordsFromEditPanelController;
import org.jphototagger.program.controller.keywords.list.DisplayKeywordController;
import org.jphototagger.program.controller.keywords.list.EditKeywordSynonymsController;
import org.jphototagger.program.controller.keywords.list.InsertKeywordsController;
import org.jphototagger.program.controller.keywords.list.KeywordItemSelectedController;
import org.jphototagger.program.controller.keywords.list.RenameKeywordsController;
import org.jphototagger.program.controller.keywords.tree.AddKeywordController;
import org.jphototagger.program.controller.keywords.tree.CopyCutPasteKeywordController;
import org.jphototagger.program.controller.keywords.tree.DeleteKeywordFromEditPanelController;
import org.jphototagger.program.controller.keywords.tree.HighlightKeywordsTreeController;
import org.jphototagger.program.controller.keywords.tree.KeywordsDisplayImagesController;
import org.jphototagger.program.controller.keywords.tree.KeywordsRepositoryUpdatesController;
import org.jphototagger.program.controller.keywords.tree.KeywordsSelectionController;
import org.jphototagger.program.controller.keywords.tree.RenameKeywordController;
import org.jphototagger.program.controller.keywords.tree.ShowKeywordsDialogController;
import org.jphototagger.program.controller.keywords.tree.ToggleButtonSelectKeywordsController;
import org.jphototagger.program.controller.keywords.tree.ToggleRealKeywordController;
import org.jphototagger.program.controller.metadata.CopyPasteMetadataController;
import org.jphototagger.program.controller.metadata.DisplayIptcUserSettingsController;
import org.jphototagger.program.controller.metadata.EmptyMetadataController;
import org.jphototagger.program.controller.metadata.EnableCreateMetadataTemplateController;
import org.jphototagger.program.controller.metadata.EnableInsertMetadataTemplateController;
import org.jphototagger.program.controller.metadata.ExifToXmpController;
import org.jphototagger.program.controller.metadata.ExtractEmbeddedXmpController;
import org.jphototagger.program.controller.metadata.IptcToXmpController;
import org.jphototagger.program.controller.metadata.MetadataTemplatesController;
import org.jphototagger.program.controller.metadata.ShowMetadataController;
import org.jphototagger.program.controller.metadata.ShowUpdateMetadataDialogController;
import org.jphototagger.program.controller.metadata.ThumbnailSelectionEditMetadataController;
import org.jphototagger.program.controller.metadatatemplates.AddMetadataTemplateController;
import org.jphototagger.program.controller.metadatatemplates.DeleteMetadataTemplateController;
import org.jphototagger.program.controller.metadatatemplates.EditMetadataTemplateController;
import org.jphototagger.program.controller.metadatatemplates.RenameMetadataTemplateController;
import org.jphototagger.program.controller.metadatatemplates.SetMetadataTemplateToSelectedImagesController;
import org.jphototagger.program.controller.misc.AboutJPhotoTaggerController;
import org.jphototagger.program.controller.misc.GoToController;
import org.jphototagger.program.controller.misc.HelpController;
import org.jphototagger.program.controller.misc.MaintainRepositoryController;
import org.jphototagger.program.controller.misc.MaximumOneItemSelectedController;
import org.jphototagger.program.controller.misc.MenuItemEnablerController;
import org.jphototagger.program.controller.misc.ShowSynonymsDialogController;
import org.jphototagger.program.controller.misc.ShowUserSettingsDialogController;
import org.jphototagger.program.controller.misc.ThumbnailCountDisplayController;
import org.jphototagger.program.controller.misc.UpdateCheckController;
import org.jphototagger.program.controller.miscmetadata.AddMetadataToSelectedImagesController;
import org.jphototagger.program.controller.miscmetadata.DeleteMiscMetadataController;
import org.jphototagger.program.controller.miscmetadata.MiscMetadataItemSelectedController;
import org.jphototagger.program.controller.miscmetadata.RemoveMetadataFromSelectedImagesController;
import org.jphototagger.program.controller.miscmetadata.RenameMiscMetadataController;
import org.jphototagger.program.controller.nometadata.NoMetadataItemSelectedController;
import org.jphototagger.program.controller.plugin.FileProcessorPluginsController;
import org.jphototagger.program.controller.programs.OpenFilesWithOtherAppController;
import org.jphototagger.program.controller.programs.OpenFilesWithStandardAppController;
import org.jphototagger.program.controller.rating.SetRatingController;
import org.jphototagger.program.controller.search.AdvancedSearchController;
import org.jphototagger.program.controller.search.CreateSavedSearchController;
import org.jphototagger.program.controller.search.DeleteSavedSearchController;
import org.jphototagger.program.controller.search.EditSavedSearchController;
import org.jphototagger.program.controller.search.FastSearchController;
import org.jphototagger.program.controller.search.RenameSavedSearchController;
import org.jphototagger.program.controller.search.SavedSearchSelectedController;
import org.jphototagger.program.controller.search.ShowAdvancedSearchDialogController;
import org.jphototagger.program.controller.thumbnail.CopyOrCutFilesToClipboardController;
import org.jphototagger.program.controller.thumbnail.CreateMetadataOfDisplayedThumbnailsController;
import org.jphototagger.program.controller.thumbnail.CreateMetadataOfSelectedThumbnailsController;
import org.jphototagger.program.controller.thumbnail.DeleteThumbnailsFromRepositoryController;
import org.jphototagger.program.controller.thumbnail.PasteFilesFromClipboardController;
import org.jphototagger.program.controller.thumbnail.RefreshThumbnailsPanelController;
import org.jphototagger.program.controller.thumbnail.RotateThumbnailsController;
import org.jphototagger.program.controller.thumbnail.SelectOrDeselectAllThumbnailsController;
import org.jphototagger.program.controller.thumbnail.SortThumbnailsController;
import org.jphototagger.program.controller.thumbnail.ThumbnailSizeSliderController;
import org.jphototagger.program.controller.thumbnail.ThumbnailsFileFilterController;
import org.jphototagger.program.controller.thumbnail.ThumbnailsPanelPersistenceController;
import org.jphototagger.program.controller.thumbnail.ThumbnailsRepositoryChangesController;
import org.jphototagger.program.controller.thumbnail.ToggleKeywordOverlayController;
import org.jphototagger.program.controller.timeline.TimelineItemSelectedController;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu;

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
                addMetadataEditPanelsControllers();
                addFavoritesControllers();
                addMetadataTablesControllers();
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
        support.add(new ToggleKeywordOverlayController());
        support.add(new ThumbnailSizeSliderController());
        support.add(new SortThumbnailsController());
        support.add(new ImportImageFilesController());
        support.add(new ExtractEmbeddedXmpController());
        support.add(new AboutJPhotoTaggerController());
        support.add(new HelpController());
        support.add(new MaintainRepositoryController());
        support.add(new ShowUpdateMetadataDialogController());
        support.add(new ShowUserSettingsDialogController());
        support.add(new ShowAdvancedSearchDialogController());
        support.add(new ShowSynonymsDialogController());
        support.add(new EditKeywordSynonymsController());
    }

    private void addAppWindowSelectionControllers() {
        support.add(new MaximumOneItemSelectedController());
        support.add(new KeywordItemSelectedController());
        support.add(new SavedSearchSelectedController());
        support.add(new ImageCollectionSelectedController());
        support.add(new CreateMetadataOfSelectedThumbnailsController());
        support.add(new ThumbnailSelectionEditMetadataController());
        support.add(new NoMetadataItemSelectedController());
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
        support.add(new FastSearchController());
        support.add(new AdvancedSearchController());
    }

    private void addThumbnailsPanelControllers() {
        support.add(new ThumbnailsPanelPersistenceController());
        support.add(new ThumbnailCountDisplayController());
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
        support.add(new ThumbnailsFileFilterController());
    }

    private void addMetadataEditPanelsControllers() {
        support.add(new EmptyMetadataController());
        support.add(new EnableInsertMetadataTemplateController());
        support.add(new ShowKeywordsDialogController());
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
        support.add(new UpdateCheckController());
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

    private void addMetadataTablesControllers() {
        support.add(new ShowMetadataController());
        support.add(new IptcToXmpController());
        support.add(new ExifToXmpController());
        support.add(new DisplayIptcUserSettingsController());
    }

    private void addDirectoryTreeControllers() {
        support.add(new PasteFilesIntoDirectoryController());
        support.add(new CreateDirectoryController());
        support.add(new RenameDirectoryController());
        support.add(new DeleteDirectoryController());
        support.add(new RefreshDirectoryTreeController());
    }

    private void addKeywordsControllers() {
        KeywordsPanel[] keywordPanels = {GUI.getAppPanel().getPanelEditKeywords(),
            InputHelperDialog.INSTANCE.getPanelKeywords()};

        for (KeywordsPanel keywordsPanel : keywordPanels) {
            support.add(new ToggleRealKeywordController(keywordsPanel));
            support.add(new RenameKeywordController(keywordsPanel));
            support.add(new AddKeywordController(keywordsPanel));
            support.add(new org.jphototagger.program.controller.keywords.tree.DeleteKeywordsController(keywordsPanel));
            support.add(
                    new org.jphototagger.program.controller.keywords.tree.AddKeywordsToEditPanelController(keywordsPanel));
            support.add(new DeleteKeywordFromEditPanelController(keywordsPanel));
            support.add(new CopyCutPasteKeywordController(keywordsPanel));
            support.add(new KeywordsDisplayImagesController());
        }

        support.add(new HighlightKeywordsTreeController());
        support.add(new KeywordsRepositoryUpdatesController());
        support.add(new RenameKeywordsController());
        support.add(new DeleteKeywordsController());
        support.add(new ToggleButtonSelectKeywordsController());
        support.add(new DisplayKeywordController());
        support.add(new InsertKeywordsController());
        support.add(new org.jphototagger.program.controller.keywords.list.AddKeywordsToEditPanelController());
        support.add(new DeleteKeywordsFromEditPanelController());
    }

    private void addMetadataTemplatesControllers() {
        support.add(new SetMetadataTemplateToSelectedImagesController());
        support.add(new AddMetadataTemplateController());
        support.add(new EditMetadataTemplateController());
        support.add(new DeleteMetadataTemplateController());
        support.add(new RenameMetadataTemplateController());
        support.add(new MetadataTemplatesController());
        support.add(new EnableCreateMetadataTemplateController());
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
