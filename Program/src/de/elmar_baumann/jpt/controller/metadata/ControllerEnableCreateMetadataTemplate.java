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
package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import javax.swing.JButton;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-22
 */
public final class ControllerEnableCreateMetadataTemplate implements ThumbnailsPanelListener {

    private final ThumbnailsPanel thumbnailsPanel              = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JButton         buttonMetadataTemplateCreate = GUI.INSTANCE.getAppPanel().getButtonMetadataTemplateCreate();

    public ControllerEnableCreateMetadataTemplate() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        buttonMetadataTemplateCreate.setEnabled(thumbnailsPanel.getSelectionCount() > 0);
    }

    @Override
    public void thumbnailsChanged() {
        // ignore
    }
}
