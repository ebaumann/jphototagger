/*
 * @(#)TabOrEnterLeavingTextArea.java    Created on 2008-09-19
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

package org.jphototagger.lib.component;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

/**
 * Text area where the tab or enter key transferring the focus.
 *
 * This text area is thought as a replacement for a text field but with the
 * capabilities of a text area.
 *
 * @author  Elmar Baumann
 */
public final class TabOrEnterLeavingTextArea extends JTextArea {
    private static final long serialVersionUID = -6104921627665799043L;

    @Override
    protected void processComponentKeyEvent(KeyEvent evt) {
        if ((evt.getID() == KeyEvent.KEY_PRESSED) && isLeave(evt.getKeyCode())) {
            evt.consume();

            if (evt.isShiftDown()) {
                transferFocusBackward();
            } else {
                transferFocus();
            }
        } else {
            super.processComponentKeyEvent(evt);
        }
    }

    private static boolean isLeave(int keyCode) {
        return (keyCode == KeyEvent.VK_TAB) || (keyCode == KeyEvent.VK_ENTER);
    }
}
