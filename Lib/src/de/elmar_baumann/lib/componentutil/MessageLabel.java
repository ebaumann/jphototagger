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
package de.elmar_baumann.lib.componentutil;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * Displays text in a {@code JLabel} for an amount of milliseconds, then hides
 * the text.
 *
 * @author  Elmar Baumann
 * @version 2010-01-15
 */
public final class MessageLabel {

    private final JLabel label;

    public MessageLabel(JLabel label) {
        this.label = label;
    }


    public enum MessageType {
        INFO,
        ERROR
        ;

        public boolean isError() {
            return this.equals(ERROR);
        }

        public boolean isInfo() {
            return this.equals(INFO);
        }
    }

    public void showMessage(String message, MessageType type, final long milliseconds) {
        label.setForeground(type.isError() ? Color.RED : Color.BLACK);
        label.setText(message);
        Thread thread = new Thread(new HideInfoMessage(milliseconds));
        thread.setName("Hiding message popup @ " + getClass().getSimpleName());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private class HideInfoMessage implements Runnable {

        private final long milliseconds;

        public HideInfoMessage(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            label.setText("");
        }
    }
}
