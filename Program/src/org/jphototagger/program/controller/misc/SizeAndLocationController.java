/*
 * @(#)SizeAndLocationController.java    Created on 2010-01-14
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

import org.jphototagger.program.UserSettings;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Listens to <code>windowOpend()</code> and <code>windowClosing()</code> and
 * reads and writes the size and location of a component to {@link UserSettings}.
 * <p>
 * Usage: Bevor setting a component visible, call
 * <code>Component#addWindowListener(new SizeAndLocationController())</code> or
 * use a singleton instance rather than creation a new.
 *
 * @author  Elmar Baumann
 */
public final class SizeAndLocationController extends WindowAdapter {
    @Override
    public void windowOpened(WindowEvent evt) {
        UserSettings.INSTANCE.getSettings().applySizeAndLocation(
            evt.getComponent());
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(
                evt.getComponent());
        UserSettings.INSTANCE.writeToFile();
    }
}
