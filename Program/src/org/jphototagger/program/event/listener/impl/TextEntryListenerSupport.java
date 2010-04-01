/*
 * @(#)TextEntryListenerSupport.java    Created on 2009/07/20
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.event.listener.TextEntryListener;

/**
 * Support to add, remove and notify {@link TextEntryListener}s.
 *
 * @author  Elmar Baumann
 */
public final class TextEntryListenerSupport
        extends ListenerSupport<TextEntryListener> {
    public void notifyTextRemoved(Column column, String removedText) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (removedText == null) {
            throw new NullPointerException("removedText == null");
        }

        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textRemoved(column, removedText);
            }
        }
    }

    public void notifyTextAdded(Column column, String addedText) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (addedText == null) {
            throw new NullPointerException("addedText == null");
        }

        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textAdded(column, addedText);
            }
        }
    }

    public void notifyTextChanged(Column column, String oldText,
                                  String newText) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (oldText == null) {
            throw new NullPointerException("oldText == null");
        }

        if (newText == null) {
            throw new NullPointerException("newText == null");
        }

        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textChanged(column, oldText, newText);
            }
        }
    }
}
