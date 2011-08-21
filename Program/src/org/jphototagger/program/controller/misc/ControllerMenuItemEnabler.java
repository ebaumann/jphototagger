package org.jphototagger.program.controller.misc;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JMenuItem;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerMenuItemEnabler implements PopupMenuListener {
    private final Map<JMenuItem, List<TypeOfDisplayedImages>> contentsOfItemsRequiresSelImages = new HashMap<JMenuItem, List<TypeOfDisplayedImages>>();
    private final List<JMenuItem> itemsRequiresSelImages = new ArrayList<JMenuItem>();

    public ControllerMenuItemEnabler() {
        init();
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.addPopupMenuListener(this);
        AnnotationProcessor.process(this);
    }

    private void init() {
        List<TypeOfDisplayedImages> contents = new ArrayList<TypeOfDisplayedImages>();
        PopupMenuThumbnails popupThumbnails = PopupMenuThumbnails.INSTANCE;

        contents.add(TypeOfDisplayedImages.DIRECTORY);
        contents.add(TypeOfDisplayedImages.FAVORITE);
        contentsOfItemsRequiresSelImages.put(PopupMenuThumbnails.INSTANCE.getItemFileSystemMoveFiles(), contents);
        contents = new ArrayList<TypeOfDisplayedImages>();
        contents.add(TypeOfDisplayedImages.IMAGE_COLLECTION);
        contentsOfItemsRequiresSelImages.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);
        itemsRequiresSelImages.add(popupThumbnails.getItemUpdateThumbnail());
        itemsRequiresSelImages.add(popupThumbnails.getItemUpdateMetadata());
        itemsRequiresSelImages.add(popupThumbnails.getItemDeleteImageFromDatabase());
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
                TypeOfDisplayedImages content = tnPanel.getContent();
                boolean fileSelected = tnPanel.isAFileSelected();

                for (JMenuItem item : itemsRequiresSelImages) {
                    item.setEnabled(fileSelected);
                }

                for (JMenuItem item : contentsOfItemsRequiresSelImages.keySet()) {
                    item.setEnabled(fileSelected && contentsOfItemsRequiresSelImages.get(item).contains(content));
                }

                PopupMenuThumbnails.INSTANCE.getItemOpenFilesWithStandardApp().setEnabled(fileSelected);
                PopupMenuThumbnails.INSTANCE.getMenuPrograms().setEnabled(fileSelected);
            }
        });
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
        Object source = evt.getSource();

        if (source == PopupMenuThumbnails.INSTANCE) {
            popupMenuThumbnailsBecomeVisible();
        }
    }

    private void popupMenuThumbnailsBecomeVisible() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        PopupMenuThumbnails popupThumbnails = PopupMenuThumbnails.INSTANCE;

        popupThumbnails.getItemSelectAll().setEnabled(tnPanel.hasFiles());
        popupThumbnails.getItemSelectNothing().setEnabled(tnPanel.isAFileSelected());
    }

    @EventSubscriber(eventClass=ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        setEnabled();
    }

    @EventSubscriber(eventClass=ThumbnailsChangedEvent.class)
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
