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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.Comparator;

import javax.swing.JRadioButtonMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerSortThumbnails implements ActionListener {
    public ControllerSortThumbnails() {
        listen();
        GUI.getAppFrame().getMenuItemOfSortCmp(
            GUI.getThumbnailsPanel().getFileSortComparator()).setSelected(true);
    }

    private void listen() {
        for (JRadioButtonMenuItem item : GUI.getAppFrame().getSortMenuItems()) {
            item.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        sortThumbnails(evt);
    }

    public static void setLastSort() {
        Comparator<File> cmp =
            ControllerThumbnailsPanelPersistence.getFileSortComparator();

        GUI.getThumbnailsPanel().setFileSortComparator(cmp);
        GUI.getAppFrame().getMenuItemOfSortCmp(cmp).setSelected(true);
    }

    private void sortThumbnails(final ActionEvent evt) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WaitDisplay.show();

                JRadioButtonMenuItem item =
                    (JRadioButtonMenuItem) evt.getSource();
                Comparator<File> sortCmp =
                    GUI.getAppFrame().getSortCmpOfMenuItem(item);
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

                ControllerFactory.INSTANCE
                    .getController(ControllerThumbnailsPanelPersistence.class)
                    .setFileSortComparator(sortCmp);
                item.setSelected(true);
                tnPanel.setFileSortComparator(sortCmp);
                tnPanel.sort();
                WaitDisplay.hide();
            }
        });
    }
}
