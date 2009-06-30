package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.ThumbnailsPanelEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
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

    private final Map<JMenuItem, List<Content>> contentsOfMenuItemRequiresSelectedImages = new HashMap<JMenuItem, List<Content>>();
    private final Map<JMenuItem, List<Content>> contentsOfMenuItemRequiresContent = new HashMap<JMenuItem, List<Content>>();
    private final List<JMenuItem> itemsRequiresSelectedImages = new ArrayList<JMenuItem>();
    private final AppFrame appFrame = GUI.INSTANCE.getAppFrame();
    private final PopupMenuThumbnails popupThumbnails = PopupMenuThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem itemOpenFilesWithStandardApp = popupThumbnails.getItemOpenFilesWithStandardApp();
    private final JMenu menuOtherOpenImageApps = popupThumbnails.getMenuOtherOpenImageApps();
    private boolean hasPrograms = DatabasePrograms.INSTANCE.hasProgram();

    public ControllerMenuItemEnabler() {
        init();
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private void init() {
        List<Content> contents;

        contents = new ArrayList<Content>();
        contents.add(Content.DIRECTORY);
        contents.add(Content.FAVORITE_DIRECTORY);
        contentsOfMenuItemRequiresSelectedImages.put(popupThumbnails.getItemFileSystemMoveFiles(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.DIRECTORY);
        contentsOfMenuItemRequiresContent.put(appFrame.getMenuItemPaste(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.IMAGE_COLLECTION);
        contentsOfMenuItemRequiresSelectedImages.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);

        itemsRequiresSelectedImages.add(appFrame.getMenuItemCopy());
        itemsRequiresSelectedImages.add(appFrame.getMenuItemCut());
        itemsRequiresSelectedImages.add(appFrame.getMenuItemDelete());
        itemsRequiresSelectedImages.add(appFrame.getMenuItemRename());
        itemsRequiresSelectedImages.add(appFrame.getMenuItemRenameInXmp());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemUpdateThumbnail());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemUpdateMetadata());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemDeleteImageFromDatabase());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemCreateImageCollection());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemAddToImageCollection());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemRotateThumbnai90());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemRotateThumbnai180());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemRotateThumbnai270());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemRenameFiles());
    }

    private void setEnabled() {
        Content content = thumbnailsPanel.getContent();
        boolean isSelection = thumbnailsPanel.getSelectionCount() > 0;

        for (JMenuItem item : itemsRequiresSelectedImages) {
            item.setEnabled(isSelection);
        }

        for (JMenuItem item : contentsOfMenuItemRequiresSelectedImages.keySet()) {
            item.setEnabled(
                    isSelection &&
                    contentsOfMenuItemRequiresSelectedImages.get(item).contains(content));
        }

        for (JMenuItem item : contentsOfMenuItemRequiresContent.keySet()) {
            item.setEnabled(
                    contentsOfMenuItemRequiresContent.get(item).contains(content));
        }

        UserSettings settings = UserSettings.INSTANCE;

        itemOpenFilesWithStandardApp.setEnabled(
                isSelection &&
                settings.hasDefaultImageOpenApp());

        menuOtherOpenImageApps.setEnabled(
                isSelection && hasPrograms);
    }

    @Override
    public void selectionChanged(ThumbnailsPanelEvent action) {
        setEnabled();
    }

    @Override
    public void thumbnailsChanged() {
        setEnabled();
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.OTHER_IMAGE_OPEN_APPS)) {
            hasPrograms = DatabasePrograms.INSTANCE.hasProgram();
            setEnabled();
        }
    }
}
