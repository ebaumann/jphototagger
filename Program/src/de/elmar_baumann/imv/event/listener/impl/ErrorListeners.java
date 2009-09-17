/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Error-Listener, erspart Klassen die Implementation von
 * <code>addErrorListener()</code> und <code>removeErrorListener()</code>.
 * Diese rufen statt dessen bei dieser Instanz auf:
 * {@link #notifyErrorListener(de.elmar_baumann.imv.event.ErrorEvent)}.
 * 
 * Klassen, die sich für Fehler interessieren, melden sich bei der Instanz
 * dieser Klasse an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class ErrorListeners {

    private final List<ErrorListener> errorListeners = new ArrayList<ErrorListener>();
    public static final ErrorListeners INSTANCE = new ErrorListeners();

    /**
     * Meldet einen Beobachter an.
     * 
     * @param listener  Beobachter
     */
    public synchronized void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    /**
     * Meldet allen angemeldeten Errorlistenern einen Fehler.
     * 
     * @param evt  Fehlerereignis
     */
    public synchronized void notifyErrorListener(ErrorEvent evt) {
        for (ErrorListener listener : errorListeners) {
            listener.error(evt);
        }
    }

    private ErrorListeners() {
    }
}
