/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.event.DatabaseProgramsEvent;
import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.event.listener.DatabaseProgramsListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-27
 */
public final class ControllerMenuItemEnabler
        implements DatabaseProgramsListener,
                   ThumbnailsPanelListener,
                   PopupMenuListener
    {

    private final Map<JMenuItem, List<Content>> contentsOfMenuItemRequiresSelectedImages = new HashMap<JMenuItem, List<Content>>();
    private final List<JMenuItem>               itemsRequiresSelectedImages              = new ArrayList<JMenuItem>();
    private final PopupMenuThumbnails           popupThumbnails                          = PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel               thumbnailsPanel                          = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem                     itemOpenFilesWithStandardApp             = popupThumbnails.getItemOpenFilesWithStandardApp();
    private final JMenu                         menPrograms                              = popupThumbnails.getMenuPrograms();
    private       boolean                       hasProgram                               = DatabasePrograms.INSTANCE.hasProgram();

    public ControllerMenuItemEnabler() {
        init();
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
        DatabasePrograms.INSTANCE.addListener(this);
        popupThumbnails.addPopupMenuListener(this);
    }

    private void init() {
        List<Content> contents = new ArrayList<Content>();

        contents.add(Content.DIRECTORY);
        contents.add(Content.FAVORITE);

        contentsOfMenuItemRequiresSelectedImages.put(popupThumbnails.getItemFileSystemMoveFiles(), contents);

        contents = new ArrayList<Content>();
        contents.add(Content.IMAGE_COLLECTION);

        contentsOfMenuItemRequiresSelectedImages.put(popupThumbnails.getItemDeleteFromImageCollection(), contents);

        itemsRequiresSelectedImages.add(popupThumbnails.getItemUpdateThumbnail());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemUpdateMetadata());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemDeleteImageFromDatabase());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemCreateImageCollection());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemAddToImageCollection());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemRotateThumbnail90());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemRotateThumbnai180());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemRotateThumbnail270());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemRenameFiles());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemIptcToXmp());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemExifToXmp());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemPick());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemCopyMetadata());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemCopyToClipboard());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemCutToClipboard());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemMoveFiles());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemFileSystemRenameFiles());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemPasteMetadata());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemReject());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuImageCollection());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuMetadata());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuPlugins());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuPrograms());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuRating());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuRotateThumbnail());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuSelection());
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

        UserSettings settings = UserSettings.INSTANCE;

        itemOpenFilesWithStandardApp.setEnabled(isSelection && settings.hasDefaultImageOpenApp());

        menPrograms.setEnabled(isSelection && hasProgram);
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        Object source = e.getSource();

        if (source == popupThumbnails) {
            popupMenuThumbnailsBecomeVisible();
        }
    }

    private void popupMenuThumbnailsBecomeVisible() {
        popupThumbnails.getItemSelectAll()    .setEnabled(thumbnailsPanel.getFileCount() > 0);
        popupThumbnails.getItemSelectNothing().setEnabled(thumbnailsPanel.getSelectionCount() > 0);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        setEnabled();
    }

    @Override
    public void thumbnailsChanged() {
        setEnabled();
    }

    @Override
    public void actionPerformed(DatabaseProgramsEvent event) {
        hasProgram = DatabasePrograms.INSTANCE.hasProgram();
        setEnabled();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // ignore
    }
}
