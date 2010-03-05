/*
 * JPhotoTagger tags and finds images fast.
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

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-04
 */
public final class DatabaseFileExcludePatternsEvent {
    public enum Type { PATTERN_INSERTED, PATTERN_DELETED; }

    private final Type   type;
    private final String pattern;

    public DatabaseFileExcludePatternsEvent(Type type, String pattern) {
        this.type    = type;
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public Type getType() {
        return type;
    }

    public boolean isPatternInserted() {
        return type.equals(Type.PATTERN_INSERTED);
    }

    public boolean isPatternDeleted() {
        return type.equals(Type.PATTERN_DELETED);
    }
}
