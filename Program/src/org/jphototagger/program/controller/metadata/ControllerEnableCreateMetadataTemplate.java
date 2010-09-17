/*
 * @(#)ControllerEnableCreateMetadataTemplate.java    Created on 2010-01-22
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

package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;

import java.awt.EventQueue;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEnableCreateMetadataTemplate
        implements ThumbnailsPanelListener {
    public ControllerEnableCreateMetadataTemplate() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.getAppPanel().getButtonMetadataTemplateCreate().setEnabled(
                    GUI.getThumbnailsPanel().isFileSelected());
            }
        });
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }
}
