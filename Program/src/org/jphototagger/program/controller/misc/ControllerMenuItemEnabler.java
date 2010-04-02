/*
 * @(#)ControllerMenuItemEnabler.java    Created on 2008-10-27
 *
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

package org.jphototagger.program.controller.misc;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerMenuItemEnabler
        implements DatabaseProgramsListener, ThumbnailsPanelListener,
                   PopupMenuListener {
    private final Map<JMenuItem, List<Content>> contentsOfItemsRequiresSelImages =
        new HashMap<JMenuItem, List<Content>>();
    private final List<JMenuItem> itemsRequiresSelImages =
        new ArrayList<JMenuItem>();
    private final PopupMenuThumbnails popupThumbnails =
        PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenu     menPrograms = popupThumbnails.getMenuPrograms();
    private final JMenuItem itemOpenFilesWithStandardApp =
        popupThumbnails.getItemOpenFilesWithStandardApp();
    private boolean hasProgram = DatabasePrograms.INSTANCE.hasProgram();

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
        contentsOfItemsRequiresSelImages.put(
            popupThumbnails.getItemFileSystemMoveFiles(), contents);
        contents = new ArrayList<Content>();
        contents.add(Content.IMAGE_COLLECTION);
        contentsOfItemsRequiresSelImages.put(
            popupThumbnails.getItemDeleteFromImageCollection(), contents);
        itemsRequiresSelImages.add(popupThumbnails.getItemUpdateThumbnail());
        itemsRequiresSelImages.add(popupThumbnails.getItemUpdateMetadata());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemDeleteImageFromDatabase());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemCreateImageCollection());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemAddToImageCollection());
        itemsRequiresSelImages.add(popupThumbnails.getItemRotateThumbnail90());
        itemsRequiresSelImages.add(popupThumbnails.getItemRotateThumbnai180());
        itemsRequiresSelImages.add(popupThumbnails.getItemRotateThumbnail270());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemRenameFiles());
        itemsRequiresSelImages.add(popupThumbnails.getItemIptcToXmp());
        itemsRequiresSelImages.add(popupThumbnails.getItemExifToXmp());
        itemsRequiresSelImages.add(popupThumbnails.getItemPick());
        itemsRequiresSelImages.add(popupThumbnails.getItemCopyMetadata());
        itemsRequiresSelImages.add(popupThumbnails.getItemCopyToClipboard());
        itemsRequiresSelImages.add(popupThumbnails.getItemCutToClipboard());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemCopyToDirectory());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemDeleteFiles());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemMoveFiles());
        itemsRequiresSelImages.add(
            popupThumbnails.getItemFileSystemRenameFiles());
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
        Content content     = thumbnailsPanel.getContent();
        boolean isSelection = thumbnailsPanel.getSelectionCount() > 0;

        for (JMenuItem item : itemsRequiresSelImages) {
            item.setEnabled(isSelection);
        }

        for (JMenuItem item : contentsOfItemsRequiresSelImages.keySet()) {
            item.setEnabled(
                isSelection
                && contentsOfItemsRequiresSelImages.get(item).contains(
                    content));
        }

        itemOpenFilesWithStandardApp.setEnabled(isSelection
                && (DatabasePrograms.INSTANCE.getDefaultImageOpenProgram()
                    != null));
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
        popupThumbnails.getItemSelectAll().setEnabled(
            thumbnailsPanel.getFileCount() > 0);
        popupThumbnails.getItemSelectNothing().setEnabled(
            thumbnailsPanel.getSelectionCount() > 0);
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
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {

        // ignore
    }

    private void setEnabledProgramsMenu() {
        hasProgram = DatabasePrograms.INSTANCE.hasProgram();
        setEnabled();
    }

    @Override
    public void programDeleted(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        setEnabledProgramsMenu();
    }

    @Override
    public void programInserted(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        setEnabledProgramsMenu();
    }

    @Override
    public void programUpdated(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        setEnabledProgramsMenu();
    }
}
