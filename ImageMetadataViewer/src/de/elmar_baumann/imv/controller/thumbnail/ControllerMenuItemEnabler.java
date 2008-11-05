package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
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
public class ControllerMenuItemEnabler extends Controller
    implements UserSettingsChangeListener, ThumbnailsPanelListener {

    private Map<JMenuItem, List<Content>> contentsOfMenuItem = new HashMap<JMenuItem, List<Content>>();
    private List<JMenuItem> itemsIsSelection = new ArrayList<JMenuItem>();
    private AppFrame appFrame = Panels.getInstance().getAppFrame();
    private PopupMenuPanelThumbnails popupThumbnails = PopupMenuPanelThumbnails.getInstance();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private JMenuItem itemOpenFilesWithStandardApp = popupThumbnails.getItemOpenFilesWithStandardApp();
    private JMenu menuOtherOpenImageApps = popupThumbnails.getMenuOtherOpenImageApps();
    private boolean hasPrograms = DatabasePrograms.getInstance().hasProgram();

    public ControllerMenuItemEnabler() {
        init();
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private void init() {
        List<Content> contents;

        contents = new ArrayList<Content>();
        contents.add(Content.Directory);
        contents.add(Content.FavoriteDirectory);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemMoveFiles(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.Directory);
        contentsOfMenuItem.put(appFrame.getMenuItemInsert(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.ImageCollection);
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
        if (isControl()) {
            setEnabled();
        }
    }

    @Override
    public void thumbnailsChanged() {
        if (isControl()) {
            setEnabled();
        }
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (isControl() && evt.getType().equals(UserSettingsChangeEvent.Type.OtherImageOpenApps)) {
            hasPrograms = DatabasePrograms.getInstance().hasProgram();
            setEnabled();
        }
    }
}
