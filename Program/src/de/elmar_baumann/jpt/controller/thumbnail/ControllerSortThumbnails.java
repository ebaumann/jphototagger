/*
 * @(#)ControllerSortThumbnails.java    Created on 2008-10-14
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

package de.elmar_baumann.jpt.controller.thumbnail;

import de.elmar_baumann.jpt.factory.ControllerFactory;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Comparator;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerSortThumbnails implements ActionListener {
    private final ThumbnailsPanel thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final AppFrame appFrame = GUI.INSTANCE.getAppFrame();

    public ControllerSortThumbnails() {
        listen();
        appFrame.getMenuItemOfSortCmp(
            thumbnailsPanel.getFileSortComparator()).setSelected(true);
    }

    private void listen() {
        for (JRadioButtonMenuItem item : appFrame.getSortMenuItems()) {
            item.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sortThumbnails(e);
    }

    public static void setLastSort() {
        Comparator<File> cmp =
            ControllerThumbnailsPanelPersistence.getFileSortComparator();

        GUI.INSTANCE.getAppPanel().getPanelThumbnails().setFileSortComparator(
            cmp);
        GUI.INSTANCE.getAppFrame().getMenuItemOfSortCmp(cmp).setSelected(true);
    }

    private void sortThumbnails(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JRadioButtonMenuItem item =
                    (JRadioButtonMenuItem) e.getSource();
                Comparator<File> sortCmp = appFrame.getSortCmpOfMenuItem(item);

                ControllerFactory.INSTANCE
                    .getController(ControllerThumbnailsPanelPersistence.class)
                    .setFileSortComparator(sortCmp);
                item.setSelected(true);
                thumbnailsPanel.setFileSortComparator(sortCmp);
                thumbnailsPanel.sort();
            }
        });
    }
}
