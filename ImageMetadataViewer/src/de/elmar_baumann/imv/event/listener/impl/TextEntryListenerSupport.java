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

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.listener.TextEntryListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Support to add, remove and notify {@link TextEntryListener}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/20
 */
public final class TextEntryListenerSupport {

    private final Set<TextEntryListener> listeners =
            Collections.synchronizedSet(new HashSet<TextEntryListener>());

    public void addTextEntryListener(TextEntryListener listener) {
        listeners.add(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        listeners.remove(listener);
    }

    public void notifyTextRemoved(Column column, String removedText) {
        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textRemoved(column, removedText);
            }
        }
    }

    public void notifyTextAdded(Column column, String addedText) {
        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textAdded(column, addedText);
            }
        }
    }

    public void notifyTextChanged(Column column, String oldText, String newText) {
        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textChanged(column, oldText, newText);
            }
        }
    }
}
