/*
 * @(#)ControllerThumbnailCountDisplay.java    Created on 2008-09-25
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

package org.jphototagger.program.controller.misc;

import java.awt.EventQueue;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * Zeigt die Anzahl der Thumbnails an.
 *
 * @author  Elmar Baumann
 */
public final class ControllerThumbnailCountDisplay
        implements ThumbnailsPanelListener, ChangeListener {
    private final AppPanel appPanel            = GUI.INSTANCE.getAppPanel();
    private final JSlider  sliderThumbnailSize =
        appPanel.getSliderThumbnailSize();
    private final JLabel          label           =
        appPanel.getLabelThumbnailInfo();
    private final ThumbnailsPanel panelThumbnails =
        appPanel.getPanelThumbnails();
    private int thumbnailZoom = sliderThumbnailSize.getValue();
    private int thumbnailCount;
    private int selectionCount;

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
    public void stateChanged(ChangeEvent evt) {
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
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                String info = JptBundle.INSTANCE.getString(
                                  "ControllerThumbnailCountDisplay.Info",
                                  thumbnailCount, selectionCount,
                                  thumbnailZoom);

                label.setText(info);
                label.setToolTipText(info);
            }
        });
    }
}
