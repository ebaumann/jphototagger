/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.util.logging;

import java.util.ArrayList;
import java.util.List;


/**
 * Ausnahme in einer Logdatei von  <code>java.util.logging.Logger</code>,
 * benutzt für Java-Exceptions und sonstigen Throwable-Objekten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExceptionLogfileRecord {

    private final List<FrameLogfileRecord> logfileRecordFrames = new ArrayList<FrameLogfileRecord>();
    private String message;

    /**
     * Liefert die Stack-Frames der Ausnahme.
     *
     * @return Stack-Frames
     */
    public List<FrameLogfileRecord> getFrames() {
        return logfileRecordFrames;
    }

    /**
     * Fügt einen Stack-Frame hinzu.
     *
     * @param  frame Stack-Frame
     * @throws NullPointerException if frame is null
     */
    public void addFrame(FrameLogfileRecord frame) {
        if (frame == null)
            throw new NullPointerException("frame == null");

        logfileRecordFrames.add(frame);
    }

    /**
     * Liefert die Nachricht der Ausnahme.
     *
     * @return Nachricht oder null, falls nicht existent
     * @see    #hasMessage()
     */
    public String getMessage() {
        return message;
    }

    /**
     * Liefert, ob eine Nachricht existiert.
     *
     * @return true, falls existent
     */
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * Setzt die Nachricht der Ausnahme.
     *
     * @param message Nachricht
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Liefert, ob ein Teilstring in irgendeinem der Inhalte vorkommt.
     *
     * @param  substring  Teilstring
     * @return true, wenn der Teilstring in irgendeinem der Inhalte vorkommt
     * @throws NullPointerException if substring is null
     */
    boolean contains(String substring) {
        if (substring == null)
            throw new NullPointerException("substring == null");

        boolean contains = containsSubstring(getMessage(), substring);
        int count = logfileRecordFrames.size();
        int index = 0;
        while (!contains && count < index) {
            contains = logfileRecordFrames.get(index).contains(substring);
            index++;
        }
        return contains;
    }

    private boolean containsSubstring(String string, String substring) {
        if (string == null) {
            return false;
        }
        return string.toLowerCase().contains(substring.toLowerCase());
    }
}
