/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktion: Thumbnailspanelanzeige aktualisieren.
 *
 * @author  Elmar Baumann
 * @version 2008-09-25
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
    public void actionPerformed(ActionEvent e) {
        refresh();
    }

    private void refresh() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                thumbnailspanel.refresh();
            }
        });
    }
}
