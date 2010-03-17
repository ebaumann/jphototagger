/*
 * @(#)ListenerSupport.java    2010-01-12
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

package de.elmar_baumann.jpt.event.listener.impl;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @param <T> listener type
 * @author  Elmar Baumann
 */
public class ListenerSupport<T> {
    protected final Set<T> listeners = new HashSet<T>();

    public void add(T listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void remove(T listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public Set<T> get() {
        synchronized (listeners) {
            return listeners;
        }
    }
}
