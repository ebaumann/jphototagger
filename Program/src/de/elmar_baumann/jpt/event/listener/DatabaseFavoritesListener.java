/*
 * @(#)DatabaseFavoritesListener.java    Created on 2010-03-04
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

package de.elmar_baumann.jpt.event.listener;

import de.elmar_baumann.jpt.data.Favorite;

/**
 * Listens to events in
 * {@link de.elmar_baumann.jpt.database.DatabaseFavorites}.
 *
 * @author  Elmar Baumann
 */
public interface DatabaseFavoritesListener {

    /**
     * Will be called if a favorite was inserted into
     * {@link de.elmar_baumann.jpt.database.DatabaseFavorites}.
     *
     * @param favorite inserted favorite
     */
    public void favoriteInserted(Favorite favorite);

    /**
     * Will be called if a favorite was deleted from
     * {@link de.elmar_baumann.jpt.database.DatabaseFavorites}.
     *
     * @param favorite deleted favorite
     */
    public void favoriteDeleted(Favorite favorite);

    /**
     * Will be called if a favorite was update in
     * {@link de.elmar_baumann.jpt.database.DatabaseFavorites}.
     *
     * @param oldFavorite old favorite before update
     * @param updatedFavorite new favorite after update
     */
    public void favoriteUpdated(Favorite oldFavorite, Favorite updatedFavorite);
}
