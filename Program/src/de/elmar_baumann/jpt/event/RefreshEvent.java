/*
 * @(#)RefreshEvent.java    2010-01-14
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;

import java.awt.Point;

import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class RefreshEvent {
    private final Object  source;
    private final Point   currentViewPosition;
    private List<Integer> selThumbnails;

    public RefreshEvent(Object source, Point currentViewPosition) {
        this.source              = source;
        this.currentViewPosition = currentViewPosition;
    }

    public Point getCurrentViewPosition() {
        return currentViewPosition;
    }

    public Object getSource() {
        return source;
    }

    public List<Integer> getSelThumbnails() {
        return selThumbnails;
    }

    public void setSelThumbnails(List<Integer> selThumbnails) {
        this.selThumbnails = selThumbnails;
    }

    public boolean hasSelThumbnails() {
        return selThumbnails != null;
    }

    public ThumbnailsPanel.Settings getSettings() {
        return new ThumbnailsPanel.Settings(currentViewPosition, selThumbnails);
    }
}
