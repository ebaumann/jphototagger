/*
 * @(#)WaitDisplay.java    Created on 2010-10-14
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

package org.jphototagger.program.view;

import org.jphototagger.program.resource.GUI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.EventQueue;

/**
 * Displays on the (entire) application frame a wait symbol (currently a
 * wait cursor).
 *
 * @author Elmar Baumann
 */
public final class WaitDisplay {
    private static final MouseAdapter ma = new MouseAdapter() {}
    ;
    private static final Cursor       WAIT_CURSOR =
        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private static final Cursor     DEFAULT_CURSOR = Cursor.getDefaultCursor();
    private static volatile boolean mlAdded;
    private static volatile boolean isShow;
    private static final Object     LOCK = new Object();

    /**
     * Shows the wait symbol (sets the wait cursor).
     */
    public static void show() {
        synchronized (LOCK) {
            addMouseListener();
        }

        show(true, WAIT_CURSOR);
    }

    /**
     * Hides the wait symbol (sets the default cursor).
     */
    public static void hide() {
        show(false, DEFAULT_CURSOR);
    }

    public static boolean isShow() {
        synchronized (LOCK) {
            return isShow;
        }
    }

    private static void show(final boolean show, final Cursor cursor) {
        final Component glassPane = GUI.getAppFrame().getGlassPane();

        if (EventQueue.isDispatchThread()) {
            synchronized (LOCK) {
                glassPane.setCursor(cursor);
                glassPane.setVisible(show);
                isShow = show;
            }
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    synchronized (LOCK) {
                        glassPane.setCursor(cursor);
                        glassPane.setVisible(show);
                        isShow = show;
                    }
                }
            });
        }
    }

    private static void addMouseListener() {
        if (!mlAdded) {
            mlAdded = true;
            GUI.getAppFrame().getGlassPane().addMouseListener(ma);
        }
    }

    private WaitDisplay() {}
}
