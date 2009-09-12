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
package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-14
 */
public final class ControllerSortThumbnails implements ActionListener {

    private final ThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final AppFrame appFrame = GUI.INSTANCE.getAppFrame();

    public ControllerSortThumbnails() {
        listen();
        appFrame.getMenuItemOfSort(thumbnailsPanel.getSort()).setSelected(true);
    }

    private void listen() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        for (FileSort sort : FileSort.values()) {
            appFrame.getMenuItemOfSort(sort).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sortThumbnails(e);
    }

    private void sortThumbnails(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem) e.getSource();
                FileSort sort = appFrame.getSortOfMenuItem(item);
                setSelectedMenuItems(sort);
                thumbnailsPanel.setSort(sort);
                thumbnailsPanel.sort();
            }
        });
    }

    private void setSelectedMenuItems(FileSort sort) {
        for (FileSort sortType : FileSort.values()) {
            appFrame.getMenuItemOfSort(sortType).setSelected(
                    sortType.equals(sort));
        }
    }
}
