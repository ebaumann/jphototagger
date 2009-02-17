package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/27
 */
public final class ControllerMenuItemEnabler
        implements UserSettingsChangeListener, ThumbnailsPanelListener {

    private final Map<JMenuItem, List<Content>> contentsOfMenuItem = new HashMap<JMenuItem, List<Content>>();
    private final List<JMenuItem> itemsIsSelection = new ArrayList<JMenuItem>();
    private final AppFrame appFrame = Panels.getInstance().getAppFrame();
    private final PopupMenuPanelThumbnails popupThumbnails = PopupMenuPanelThumbnails.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private final JMenuItem itemOpenFilesWithStandardApp = popupThumbnails.getItemOpenFilesWithStandardApp();
    private final JMenu menuOtherOpenImageApps = popupThumbnails.getMenuOtherOpenImageApps();
    private boolean hasPrograms = DatabasePrograms.getInstance().hasProgram();

    public ControllerMenuItemEnabler() {
        init();
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private void init() {
        List<Content> contents;

        contents = new ArrayList<Content>();
        contents.add(Content.DIRECTORY);
        contents.add(Content.FAVORITE_DIRECTORY);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemMoveFiles(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.DIRECTORY);
        contentsOfMenuItem.put(appFrame.getMenuItemInsert(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.IMAGE_COLLECTION);
        contentsOfMenuItem.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);

        itemsIsSelection.add(appFrame.getMenuItemCopy());
        itemsIsSelection.add(appFrame.getMenuItemCut());
        itemsIsSelection.add(appFrame.getMenuItemDelete());
        itemsIsSelection.add(appFrame.getMenuItemRename());
        itemsIsSelection.add(appFrame.getMenuItemRenameInXmp());
        itemsIsSelection.add(popupThumbnails.getItemUpdateThumbnail());
        itemsIsSelection.add(popupThumbnails.getItemUpdateMetadata());
        itemsIsSelection.add(popupThumbnails.getItemDeleteImageFromDatabase());
        itemsIsSelection.add(popupThumbnails.getItemCreateImageCollection());
        itemsIsSelection.add(popupThumbnails.getItemAddToImageCollection());
        itemsIsSelection.add(popupThumbnails.getItemRotateThumbnai90());
        itemsIsSelection.add(popupThumbnails.getItemRotateThumbnai180());
        itemsIsSelection.add(popupThumbnails.getItemRotateThumbnai270());
        itemsIsSelection.add(popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsIsSelection.add(popupThumbnails.getItemFileSystemDeleteFiles());
        itemsIsSelection.add(popupThumbnails.getItemFileSystemRenameFiles());
    }

    private void setEnabled() {
        Content content = thumbnailsPanel.getContent();
        boolean isSelection = thumbnailsPanel.getSelectionCount() > 0;

        for (JMenuItem item : itemsIsSelection) {
            item.setEnabled(isSelection);
        }

        for (JMenuItem item : contentsOfMenuItem.keySet()) {
            item.setEnabled(
                    isSelection &&
                    contentsOfMenuItem.get(item).contains(content));
        }

        UserSettings settings = UserSettings.getInstance();

        itemOpenFilesWithStandardApp.setEnabled(
                isSelection &&
                settings.hasDefaultImageOpenApp());

        menuOtherOpenImageApps.setEnabled(
                isSelection && hasPrograms);
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
        setEnabled();
    }

    @Override
    public void thumbnailsChanged() {
        setEnabled();
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.OTHER_IMAGE_OPEN_APPS)) {
            hasPrograms = DatabasePrograms.getInstance().hasProgram();
            setEnabled();
        }
    }
}
