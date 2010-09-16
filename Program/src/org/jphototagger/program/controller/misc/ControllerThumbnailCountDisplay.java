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

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.ViewUtil;

import java.awt.EventQueue;

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
    private int thumbnailZoom;
    private int thumbnailCount;
    private int selectionCount;

    public ControllerThumbnailCountDisplay() {
        thumbnailZoom = getSlider().getValue();
        listen();
    }

    private void listen() {
        ViewUtil.getThumbnailsPanel().addThumbnailsPanelListener(this);
        getSlider().addChangeListener(this);
    }

    private JSlider getSlider() {
        return GUI.INSTANCE.getAppPanel().getSliderThumbnailSize();
    }

    @Override
    public void thumbnailsSelectionChanged() {
        selectionCount = ViewUtil.getThumbnailsPanel().getSelectionCount();
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
        thumbnailCount = ViewUtil.getThumbnailsPanel().getFileCount();
        setLabel();
    }

    private void setZoom() {
        thumbnailZoom = getSlider().getValue();
        setLabel();
    }

    private void setLabel() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JLabel label =
                    GUI.INSTANCE.getAppPanel().getLabelThumbnailInfo();
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
