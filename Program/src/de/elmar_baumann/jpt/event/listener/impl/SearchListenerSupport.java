/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.event.SearchEvent;
import de.elmar_baumann.jpt.event.listener.SearchListener;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-18
 */
public final class SearchListenerSupport extends ListenerSupport<SearchListener> {

    public void notifyListeners(SearchEvent event) {
        synchronized (listeners) {
            for (SearchListener listener : listeners) {
                listener.actionPerformed(event);
            }
        }
    }
}
