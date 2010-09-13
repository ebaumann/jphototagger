/*
 * @(#)ControllerThumbnailsPanelPersistence.java    Created on 2008-10-15
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

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.comparator.ComparatorFilesNoSort;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.AppExitListener;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author  Elmar Baumann
 */
public final class ControllerThumbnailsPanelPersistence
        implements ThumbnailsPanelListener, AppExitListener {
    private static final String KEY_SELECTED_FILES =
        "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.SelectedFiles";
    private static final String KEY_SORT =
        "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.Sort";
    private static final String KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION =
        "org.jphototagger.program.view.panels.controller.ViewportViewPosition";
    private volatile boolean      propertiesRead;
    private final ThumbnailsPanel tnPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private List<File> persistentSelectedFiles = new ArrayList<File>();

    public ControllerThumbnailsPanelPersistence() {
        listen();
        readProperties();
    }

    private void listen() {
        tnPanel.addThumbnailsPanelListener(this);
        AppLifeCycle.INSTANCE.addAppExitListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        writeSelectionToProperties();
    }

    @Override
    public void thumbnailsChanged() {
        checkFirstChange();
    }

    private void checkFirstChange() {
        synchronized (this) {
            if (propertiesRead) {
                return;
            }

            propertiesRead = true;
        }

        readSelectedFilesFromProperties();
        readViewportViewPositionFromProperties();
    }

    private void writeSelectionToProperties() {
        UserSettings.INSTANCE.getSettings().setStringCollection(
            FileUtil.getAsFilenames(tnPanel.getSelectedFiles()),
            KEY_SELECTED_FILES);
        UserSettings.INSTANCE.writeToFile();
    }

    private void readSelectedFilesFromProperties() {
        List<Integer> indices = new ArrayList<Integer>();

        for (File file : persistentSelectedFiles) {
            int index = tnPanel.getIndexOf(file);

            if (index >= 0) {
                indices.add(index);
            }
        }

        tnPanel.setSelected(indices);
    }

    private void readProperties() {
        persistentSelectedFiles = FileUtil.getAsFiles(
            UserSettings.INSTANCE.getSettings().getStringCollection(
                KEY_SELECTED_FILES));
        readSortFromProperties();
    }

    @SuppressWarnings("unchecked")
    private void readSortFromProperties() {
        tnPanel.setFileSortComparator(getFileSortComparator());
    }

    public void setFileSortComparator(Comparator<File> cmp) {
        Class<?> sortClass = cmp.getClass();

        if (!sortClass.equals(ComparatorFilesNoSort.class)) {
            UserSettings.INSTANCE.getSettings().set(sortClass.getName(),
                    KEY_SORT);
        }
    }

    /**
     * Returns the file sort comparator from the user settings.
     *
     * @return sort comparator or if not defined the comparator of
     *         {@link FileSort#NAMES_ASCENDING}
     */
    @SuppressWarnings("unchecked")
    public static Comparator<File> getFileSortComparator() {
        if (UserSettings.INSTANCE.getProperties().containsKey(KEY_SORT)) {
            try {
                String className =
                    UserSettings.INSTANCE.getSettings().getString(KEY_SORT);

                return (Comparator<File>) Class.forName(
                    className).newInstance();
            } catch (Exception ex) {
                AppLogger.logSevere(ControllerThumbnailsPanelPersistence.class,
                                    ex);
            }
        }

        return FileSort.NAMES_ASCENDING.getComparator();
    }

    private void readViewportViewPositionFromProperties() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Waiting until TN panel size was calculated
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    AppLogger.logSevere(getClass(), ex);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        UserSettings.INSTANCE.getSettings().applySettings(
                            GUI.INSTANCE.getAppPanel().getScrollPaneThumbnailsPanel(),
                            KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION);
                    }
                });
            }
        }, "JPhotoTagger: Restoring viewport position").start();
    }

    @Override
    public void appWillExit() {
        writeViewportViewPositionToProperties();
    }

    private void writeViewportViewPositionToProperties() {
        UserSettings.INSTANCE.getSettings().set(
            GUI.INSTANCE.getAppPanel().getScrollPaneThumbnailsPanel(),
            KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION);
        UserSettings.INSTANCE.writeToFile();
    }
}
