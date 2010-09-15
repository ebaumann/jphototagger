/*
 * @(#)ControllerThumbnailsSelectAllOrNothing.java    Created on 2010-01-28
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

import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import org.jphototagger.program.view.ViewUtil;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerThumbnailsSelectAllOrNothing
        implements ActionListener {
    private final ThumbnailsPanel tnPanel =
        ViewUtil.getThumbnailsPanel();
    private final JMenuItem itemSelectAll =
        PopupMenuThumbnails.INSTANCE.getItemSelectAll();
    private final JMenuItem itemSelectNoting =
        PopupMenuThumbnails.INSTANCE.getItemSelectNothing();

    public ControllerThumbnailsSelectAllOrNothing() {
        listen();
    }

    private void listen() {
        itemSelectAll.addActionListener(this);
        itemSelectNoting.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == itemSelectAll) {
            tnPanel.selectAll();
        } else if (source == itemSelectNoting) {
            tnPanel.clearSelection();
        }
    }
}
