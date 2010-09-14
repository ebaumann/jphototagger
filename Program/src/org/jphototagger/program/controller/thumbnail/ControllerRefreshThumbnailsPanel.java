/*
 * @(#)ControllerRefreshThumbnailsPanel.java    Created on 2008-09-25
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

import java.awt.EventQueue;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author  Elmar Baumann
 */
public final class ControllerRefreshThumbnailsPanel implements ActionListener {
    private final ThumbnailsPanel thumbnailspanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerRefreshThumbnailsPanel() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemRefresh().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        refresh();
    }

    private void refresh() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                thumbnailspanel.refresh();
            }
        });
    }
}
