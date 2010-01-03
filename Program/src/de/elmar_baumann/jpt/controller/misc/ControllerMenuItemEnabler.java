/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.event.UserSettingsChangeEvent;
import de.elmar_baumann.jpt.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
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
 * @version 2008-10-27
 */
public final class ControllerMenuItemEnabler
        implements UserSettingsChangeListener, ThumbnailsPanelListener {

    private final Map<JMenuItem, List<Content>> contentsOfMenuItemRequiresSelectedImages = new HashMap<JMenuItem, List<Content>>();
    private final List<JMenuItem>               itemsRequiresSelectedImages              = new ArrayList<JMenuItem>();
    private final AppFrame                      appFrame                                 = GUI.INSTANCE.getAppFrame();
    private final PopupMenuThumbnails           popupThumbnails                          = PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel               thumbnailsPanel                          = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem                     itemOpenFilesWithStandardApp             = popupThumbnails.getItemOpenFilesWithStandardApp();
    private final JMenu                         menuOtherOpenImageApps                   = popupThumbnails.getMenuOtherOpenImageApps();
    private final JMenu                         menuSort                                 = appFrame.getMenuSort();
    private       boolean                       hasPrograms                              = DatabasePrograms.INSTANCE.hasProgram();

    public ControllerMenuItemEnabler() {
        init();
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
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
        itemsRequiresSelectedImages.add(popupThumbnails.getItemPick());
        itemsRequiresSelectedImages.add(popupThumbnails.getItemReject());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuRating());
        itemsRequiresSelectedImages.add(popupThumbnails.getMenuPlugins());
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

        menuSort.setEnabled(thumbnailsPanel.getContent().isSortable());

        UserSettings settings = UserSettings.INSTANCE;

        itemOpenFilesWithStandardApp.setEnabled(
                isSelection && settings.hasDefaultImageOpenApp());

        menuOtherOpenImageApps.setEnabled(isSelection && hasPrograms);
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
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.OTHER_IMAGE_OPEN_APPS)) {
            hasPrograms = DatabasePrograms.INSTANCE.hasProgram();
            setEnabled();
        }
    }
}
