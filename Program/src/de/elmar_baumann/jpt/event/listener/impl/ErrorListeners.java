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
package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.event.ErrorEvent;
import de.elmar_baumann.jpt.event.listener.ErrorListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Error-Listener, erspart Klassen die Implementation von
 * <code>add()</code> und <code>removeErrorListener()</code>.
 * Diese rufen statt dessen bei dieser Instanz auf:
 * {@link #notifyListeners(de.elmar_baumann.jpt.event.ErrorEvent)}.
 *
 * Klassen, die sich für Fehler interessieren, melden sich bei der Instanz
 * dieser Klasse an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class ErrorListeners {

    private final       Set<ErrorListener> listeners = new HashSet<ErrorListener>();
    public static final ErrorListeners     INSTANCE  = new ErrorListeners();

    public void add(ErrorListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void remove(ErrorListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void notifyListeners(ErrorEvent evt) {
        synchronized (listeners) {
            for (ErrorListener listener : listeners) {
                listener.error(evt);
            }
        }
    }

    private ErrorListeners() {
    }
}
