/*
 * @(#)GUI.java    Created on 2008-09-29
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

package org.jphototagger.program.resource;

import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.panels.AppPanel;

/**
 * Provides access to GUI elements.
 *
 * @author  Elmar Baumann
 */
public final class GUI {
    private AppPanel        appPanel;
    private AppFrame        appFrame;
    public static final GUI INSTANCE = new GUI();

    public void setAppPanel(AppPanel panel) {
        if (panel == null) {
            throw new NullPointerException("panel == null");
        }

        appPanel = panel;
    }

    public void setAppFrame(AppFrame frame) {
        if (frame == null) {
            throw new NullPointerException("frame == null");
        }

        appFrame = frame;
    }

    public AppPanel getAppPanel() {
        return appPanel;
    }

    public AppFrame getAppFrame() {
        return appFrame;
    }
}
