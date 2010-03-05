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
package de.elmar_baumann.jpt.data;

import de.elmar_baumann.jpt.database.DatabaseKeywords;
import java.io.Serializable;

/**
 * A keyword is a keyword (Dublin core subject) with one or zero parents.
 * Because every keyword can have a parent deep are possible.
 *
 * Persistent instances resists in the {@link DatabaseKeywords}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-10
 */
public final class Keyword implements Comparable<Keyword>, Serializable {

    private static final long    serialVersionUID = -8175948472921889128L;
    private              Long    id;
    private              Long    idParent;
    private              String  name;
    private              Boolean real             = true;

    /**
     * Creates a new instance of this class.
     *
     * @param id        database ID. <em>Only
     *                  {@link DatabaseKeywords}</em> shall set this
     *                  ID. Ohter callers shall set null
     * @param idParent  database ID of the keyword's parent
     * @param name      keyword name
     * @param real      true if this keyword is a real keyword
     */
    public Keyword(Long id, Long idParent, String name, Boolean real) {
        this.id       = id;
        this.idParent = idParent;
        this.name     = name;
        this.real     = real;
    }

    public Keyword(Keyword keyword) {
        id       = keyword.id;
        idParent = keyword.idParent;
        name     = keyword.name;
        real     = keyword.real;
    }

    /**
     * Sets the database ID. <em>Only {@link DatabaseKeywords}</em>
     * shall call this mehtod!
     *
     * @param id ID. Default: null.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the dataase ID.
     *
     * @return database ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the database ID of the keyword's parent.
     *
     * @return database ID of the parent or null if undefined
     */
    public Long getIdParent() {
        return idParent;
    }

    /**
     * Sets the database ID of the keyword's parent.
     *
     * @param idParent ID of the parent. Default: null.
     */
    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    /**
     * Returns the keyword.
     *
     * @return keyword or null if undefined
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the keyword.
     *
     * @param name keyword. Default: null.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether this is a real keyword or just a helping container
     * object with no real keyword in it.
     *
     * @return true if this keyword is a real keyword or null if undefined
     */
    public Boolean isReal() {
        return real;
    }

    /**
     * Sets this to be a real keyword.
     *
     * @param real true if this keyword is a real keyword. Default: true.
     */
    public void setReal(Boolean real) {
        this.real = real;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Keyword other = (Keyword) obj;
        if (this.id != other.id &&
                (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null
                ? this.id.hashCode()
                : 0);
        hash =
                59 * hash +
                (this.idParent != null
                ? this.idParent.hashCode()
                : 0);
        hash = 59 * hash + (this.name != null
                ? this.name.hashCode()
                : 0);
        return hash;
    }

    @Override
    public String toString() {
        return name; // Never change that (will be used to find model items)!
    }

    @Override
    public int compareTo(Keyword o) {
        if (o == null) return 1;
        if (o == this) return 0;
        return o.getName().compareToIgnoreCase(name);
    }
}
