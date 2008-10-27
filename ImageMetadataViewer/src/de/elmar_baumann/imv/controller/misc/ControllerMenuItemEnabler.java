package de.elmar_baumann.imv.controller.misc;

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

    public ControllerMenuItemEnabler() {
        init();
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    @SuppressWarnings("empty-statement")
    private void init() {
        List<Content> contents;

        contents = new ArrayList<Content>();
        contents.add(Content.Directory);
        contents.add(Content.FavoriteDirectory);
        contentsOfMenuItem.put(appFrame.getMenuItemCopy(), contents);
        contentsOfMenuItem.put(appFrame.getMenuItemCut(), contents);
        contentsOfMenuItem.put(appFrame.getMenuItemFileSystemRename(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemDeleteFiles(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemCopySelectedFilesToDirectory(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemMoveFiles(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemFileSystemRenameFiles(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.Directory);
        contentsOfMenuItem.put(appFrame.getMenuItemInsert(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.ImageCollection);
        contentsOfMenuItem.put(popupThumbnails.getItemAddToImageCollection(), contents);
        contentsOfMenuItem.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);

        itemsIsSelection.add(appFrame.getMenuItemRenameInXmp());
        itemsIsSelection.add(popupThumbnails.getItemCreateImageCollection());
        itemsIsSelection.add(popupThumbnails.getItemDeleteThumbnail());

        contentsOfDelete.add(Content.Directory);
        contentsOfDelete.add(Content.FavoriteDirectory);
        contentsOfDelete.add(Content.ImageCollection);
    }

    private void setEnabled() {
        Content content = thumbnailsPanel.getContent();
        boolean isSelection = thumbnailsPanel.getSelectionCount() > 0;
        for (JMenuItem item : contentsOfMenuItem.keySet()) {
            item.setEnabled(contentsOfMenuItem.get(item).contains(content) && isSelection);
        }
        itemDelete.setEnabled(contentsOfDelete.contains(content) && isSelection);
        for (JMenuItem item : itemsIsSelection) {
            item.setEnabled(isSelection);
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
