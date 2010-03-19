/*
 * @(#)DatabaseFavoritesEvent.java    Created on 2010-03-04
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

package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.data.Favorite;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class DatabaseFavoritesEvent {
    public enum Type { FAVORITE_INSERTED, FAVORITE_UPDATED, FAVORITE_DELETED; }

    private final Type     type;
    private final Favorite favorite;
    private Favorite       oldFavorite;

    public DatabaseFavoritesEvent(Type type, Favorite favorite) {
        this.type     = type;
        this.favorite = (favorite == null)
                        ? null
                        : new Favorite(favorite);
    }

    public Favorite getFavorite() {
        return (favorite == null)
               ? null
               : new Favorite(favorite);
    }

    public Type getType() {
        return type;
    }

    public Favorite getOldFavorite() {
        return oldFavorite;
    }

    public void setOldFavorite(Favorite oldFavorite) {
        this.oldFavorite = oldFavorite;
    }

    public boolean isFavoriteInserted() {
        return type.equals(Type.FAVORITE_INSERTED);
    }

    public boolean isFavoriteUpdated() {
        return type.equals(Type.FAVORITE_UPDATED);
    }

    public boolean isFavoriteDeleted() {
        return type.equals(Type.FAVORITE_DELETED);
    }
}
