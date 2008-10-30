package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
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
public class ControllerMenuItemEnabler extends Controller implements ThumbnailsPanelListener {

    private Map<JMenuItem, List<Content>> contentsOfMenuItem = new HashMap<JMenuItem, List<Content>>();
    private List<JMenuItem> itemsIsSelection = new ArrayList<JMenuItem>();
    private List<Content> contentsOfDelete = new ArrayList<Content>();
    private AppFrame appFrame = Panels.getInstance().getAppFrame();
    private PopupMenuPanelThumbnails popupThumbnails = PopupMenuPanelThumbnails.getInstance();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private JMenuItem itemDelete = appFrame.getMenuItemDelete();
    private JMenuItem itemOpenFilesWithStandardApp = popupThumbnails.getItemOpenFilesWithStandardApp();
    private JMenu menuOtherOpenImageApps = popupThumbnails.getMenuOtherOpenImageApps();

    public ControllerMenuItemEnabler() {
        init();
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private void init() {
        List<Content> contents;

        contents = new ArrayList<Content>();
        contents.add(Content.Directory);
        contents.add(Content.FavoriteDirectory);
        contentsOfMenuItem.put(appFrame.getMenuItemCopy(), contents);
        contentsOfMenuItem.put(appFrame.getMenuItemCut(), contents);
        contentsOfMenuItem.put(appFrame.getMenuItemFileSystemRename(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemCopySelectedFilesToDirectory(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemDeleteFiles(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemMoveFiles(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemRenameFiles(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.Directory);
        contentsOfMenuItem.put(appFrame.getMenuItemInsert(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.ImageCollection);
        contentsOfMenuItem.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);

        itemsIsSelection.add(appFrame.getMenuItemRenameInXmp());
        itemsIsSelection.add(popupThumbnails.getItemUpdateThumbnail());
        itemsIsSelection.add(popupThumbnails.getItemUpdateMetadata());
        itemsIsSelection.add(popupThumbnails.getItemDeleteImageFromDatabase());
        itemsIsSelection.add(popupThumbnails.getItemCreateImageCollection());
        itemsIsSelection.add(popupThumbnails.getItemAddToImageCollection());
        itemsIsSelection.add(popupThumbnails.getItemRotateThumbnai90());
        itemsIsSelection.add(popupThumbnails.getItemRotateThumbnai180());
        itemsIsSelection.add(popupThumbnails.getItemRotateThumbnai270());

        contentsOfDelete.add(Content.Directory);
        contentsOfDelete.add(Content.FavoriteDirectory);
        contentsOfDelete.add(Content.FastSearch);
        contentsOfDelete.add(Content.ImageCollection);
        contentsOfDelete.add(Content.SafedSearch);
    }

    private void setEnabled() {
        Content content = thumbnailsPanel.getContent();
        boolean isSelection = thumbnailsPanel.getSelectionCount() > 0;
        boolean hasFocus = thumbnailsPanel.hasFocus();
        for (JMenuItem item : contentsOfMenuItem.keySet()) {
            item.setEnabled(hasFocus && isSelection && contentsOfMenuItem.get(item).contains(content));
        }
        itemDelete.setEnabled(hasFocus && isSelection && contentsOfDelete.contains(content));
        UserSettings settings = UserSettings.getInstance();
        itemOpenFilesWithStandardApp.setEnabled(hasFocus && isSelection && settings.hasDefaultImageOpenApp());
        menuOtherOpenImageApps.setEnabled(hasFocus && isSelection && settings.hasOtherImageOpenApps());
        for (JMenuItem item : itemsIsSelection) {
            item.setEnabled(hasFocus && isSelection);
        }
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
}
