/*
 * @(#)Favorite.java    Created on 2008-09-23
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

package org.jphototagger.program.data;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Favorite: File system directory + alias name + order (index).
 *
 * @author  Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Favorite {
    private Long   id;
    private int    index = Integer.MIN_VALUE;
    private String name;
    private File   directory;

    public Favorite() {}

    public Favorite(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        set(favorite);
    }

    public void set(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        this.id        = favorite.id;
        this.index     = favorite.index;
        this.name      = favorite.name;
        this.directory = favorite.directory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return name;    // Never change that (will be used to find model items)!
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Favorite other = (Favorite) obj;

        if ((this.id != other.id)
                && ((this.id == null) ||!this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 59 * hash + ((this.id != null)
                            ? this.id.hashCode()
                            : 0);

        return hash;
    }
}
