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
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Zeigt die Anzahl der Thumbnails an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-25
 */
public final class ControllerThumbnailCountDisplay
        implements ThumbnailsPanelListener, ChangeListener {

    private final AppPanel        appPanel            = GUI.INSTANCE.getAppPanel();
    private final JSlider         sliderThumbnailSize = appPanel.getSliderThumbnailSize();
    private final JLabel          label               = appPanel.getLabelThumbnailInfo();
    private final ThumbnailsPanel panelThumbnails     = appPanel.getPanelThumbnails();
    private int                   thumbnailZoom       = sliderThumbnailSize.getValue();
    private int                   thumbnailCount;
    private int                   selectionCount;

    public ControllerThumbnailCountDisplay() {
        listen();
    }

    private void listen() {
        panelThumbnails.addThumbnailsPanelListener(this);
        sliderThumbnailSize.addChangeListener(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        selectionCount = panelThumbnails.getSelectionCount();
        setCount();
    }

    @Override
    public void thumbnailsChanged() {
        setCount();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        setZoom();
    }

    private void setCount() {
        thumbnailCount = panelThumbnails.getFileCount();
        setLabel();
    }

    private void setZoom() {
        thumbnailZoom = sliderThumbnailSize.getValue();
        setLabel();
    }

    private void setLabel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String info = Bundle.getString("ControllerThumbnailCountDisplay.Info", thumbnailCount, selectionCount, thumbnailZoom);
                label.setText(info);
                label.setToolTipText(info);
            }
        });
    }
}
