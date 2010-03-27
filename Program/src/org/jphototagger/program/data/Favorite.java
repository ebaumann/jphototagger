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
    private File   directory;
    private int    index;
    private String name;

    public Favorite() {}

    public Favorite(Favorite favorite) {
        set(favorite);
    }

    /**
     * Constructor.
     *
     * @param name       name (alias) of the favorite
     * @param directory  directory
     * @param index      order within the favorites
     */
    public Favorite(String name, File directory, int index) {
        this.name      = name;
        this.directory = directory;
        this.index     = index;
    }

    public void set(Favorite favorite) {
        this.name      = favorite.name;
        this.directory = favorite.directory;
        this.index     = favorite.index;
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

        if (((this.name == null) ||!this.name.equals(other.name))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 53 * hash + ((this.name != null)
                            ? this.name.hashCode()
                            : 0);

        return hash;
    }
}
