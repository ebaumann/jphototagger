/*
 * @(#)TableButtonMouseListener.java    Created on 2009-04-01
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

package org.jphototagger.lib.event.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * Ursprünglicher Code von http://www.devx.com/getHelpOn/10MinuteSolution/20425
 *
 */
public final class TableButtonMouseListener implements MouseListener {
    private final JTable table;

    private void forwardEventToButton(MouseEvent evt) {
        JButton button = getButton(evt);

        if (button != null) {
            MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(table, evt,
                                        button);

            button.dispatchEvent(mouseEvent);

            // This is necessary so that when a button is pressed and released
            // it gets rendered properly.  Otherwise, the button may still
            // appear pressed down when it has been released.
            table.repaint();
        }
    }

    private JButton getButton(MouseEvent evt) {
        int row    = evt.getY() / table.getRowHeight();
        int column = table.getColumnModel().getColumnIndexAtX(evt.getX());

        if ((row >= table.getRowCount()) || (row < 0)
                || (column >= table.getColumnCount()) || (column < 0)) {
            return null;
        }

        Object value = table.getValueAt(row, column);

        if (value instanceof JButton) {
            return (JButton) value;
        }

        return null;
    }

    public TableButtonMouseListener(JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        JButton button = getButton(evt);

        if (button != null) {
            button.doClick();
        }
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        forwardEventToButton(evt);
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        forwardEventToButton(evt);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        forwardEventToButton(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        forwardEventToButton(evt);
    }
}
