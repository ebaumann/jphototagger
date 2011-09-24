package org.jphototagger.program.controller.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.api.image.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MenuItemEnablerController implements PopupMenuListener {

    private final Map<JMenuItem, List<OriginOfDisplayedThumbnails>> originsOfItemsRequiresSelImages = new HashMap<JMenuItem, List<OriginOfDisplayedThumbnails>>();
    private final List<JMenuItem> itemsRequiresSelImages = new ArrayList<JMenuItem>();

    public MenuItemEnablerController() {
        init();
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.addPopupMenuListener(this);
        AnnotationProcessor.process(this);
    }

    private void init() {
        List<OriginOfDisplayedThumbnails> contents = new ArrayList<OriginOfDisplayedThumbnails>();
        ThumbnailsPopupMenu popupThumbnails = ThumbnailsPopupMenu.INSTANCE;

        contents.add(OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY);
        contents.add(OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY);
        originsOfItemsRequiresSelImages.put(ThumbnailsPopupMenu.INSTANCE.getItemFileSystemMoveFiles(), contents);
        contents = new ArrayList<OriginOfDisplayedThumbnails>();
        contents.add(OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION);
        originsOfItemsRequiresSelImages.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);
        itemsRequiresSelImages.add(popupThumbnails.getItemUpdateThumbnail());
        itemsRequiresSelImages.add(popupThumbnails.getItemUpdateMetadata());
        itemsRequiresSelImages.add(popupThumbnails.getItemDeleteImageFromRepository());
        itemsRequiresSelImages.add(popupThumbnails.getItemCreateImageCollection());
        itemsRequiresSelImages.add(popupThumbnails.getItemAddToImageCollection());
        itemsRequiresSelImages.add(popupThumbnails.getItemRotateThumbnail90());
        itemsRequiresSelImages.add(popupThumbnails.getItemRotateThumbnai180());
        itemsRequiresSelImages.add(popupThumbnails.getItemRotateThumbnail270());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemRenameFiles());
        itemsRequiresSelImages.add(popupThumbnails.getItemIptcToXmp());
        itemsRequiresSelImages.add(popupThumbnails.getItemExifToXmp());
        itemsRequiresSelImages.add(popupThumbnails.getItemPick());
        itemsRequiresSelImages.add(popupThumbnails.getItemCopyMetadata());
        itemsRequiresSelImages.add(popupThumbnails.getItemCopyToClipboard());
        itemsRequiresSelImages.add(popupThumbnails.getItemCutToClipboard());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemMoveFiles());
        itemsRequiresSelImages.add(popupThumbnails.getItemFileSystemRenameFiles());
        itemsRequiresSelImages.add(popupThumbnails.getItemPasteMetadata());
        itemsRequiresSelImages.add(popupThumbnails.getItemReject());
        itemsRequiresSelImages.add(popupThumbnails.getMenuImageCollection());
        itemsRequiresSelImages.add(popupThumbnails.getMenuMetadata());
        itemsRequiresSelImages.add(popupThumbnails.getMenuPlugins());
        itemsRequiresSelImages.add(popupThumbnails.getMenuPrograms());
        itemsRequiresSelImages.add(popupThumbnails.getMenuRating());
        itemsRequiresSelImages.add(popupThumbnails.getMenuRotateThumbnail());
        itemsRequiresSelImages.add(popupThumbnails.getMenuSelection());
    }

    private void setEnabled() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
                OriginOfDisplayedThumbnails originOfOfDisplayedThumbnails = tnPanel.getOriginOfDisplayedThumbnails();
                boolean fileSelected = tnPanel.isAFileSelected();

                for (JMenuItem item : itemsRequiresSelImages) {
                    item.setEnabled(fileSelected);
                }

                for (JMenuItem item : originsOfItemsRequiresSelImages.keySet()) {
                    item.setEnabled(fileSelected && originsOfItemsRequiresSelImages.get(item).contains(originOfOfDisplayedThumbnails));
                }

                ThumbnailsPopupMenu.INSTANCE.getItemOpenFilesWithStandardApp().setEnabled(fileSelected);
                ThumbnailsPopupMenu.INSTANCE.getMenuPrograms().setEnabled(fileSelected);
            }
        });
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
        Object source = evt.getSource();

        if (source == ThumbnailsPopupMenu.INSTANCE) {
            popupMenuThumbnailsBecomeVisible();
        }
    }

    private void popupMenuThumbnailsBecomeVisible() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        ThumbnailsPopupMenu popupThumbnails = ThumbnailsPopupMenu.INSTANCE;

        popupThumbnails.getItemSelectAll().setEnabled(tnPanel.hasFiles());
        popupThumbnails.getItemSelectNothing().setEnabled(tnPanel.isAFileSelected());
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        setEnabled();
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
        setEnabled();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent evt) {
        // ignore
    }
}
