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
package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.data.MetadataTemplate;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-05
 */
public final class DatabaseMetadataTemplatesEvent {

    public enum Type {
        ADDED,
        DELETED,
        UPDATED,
        ;
    }

    private final Type             type;
    private final MetadataTemplate template;
    private final MetadataTemplate oldTemplate;
    private final Object           source;

    public DatabaseMetadataTemplatesEvent(Type type, MetadataTemplate template, MetadataTemplate oldTemplate, Object source) {
        this.type        = type;
        this.template    = template;
        this.oldTemplate = oldTemplate;
        this.source      = source;
    }

    public DatabaseMetadataTemplatesEvent(Type type, MetadataTemplate template, Object source) {
        this.type        = type;
        this.template    = template;
        this.source      = source;
        this.oldTemplate = null;
    }

    public MetadataTemplate getOldTemplate() {
        return oldTemplate;
    }

    public Object getSource() {
        return source;
    }

    public MetadataTemplate getTemplate() {
        return template;
    }

    public Type getType() {
        return type;
    }

    public boolean wasAdded() {
        return type.equals(Type.ADDED);
    }

    public boolean wasUpdated() {
        return type.equals(Type.UPDATED);
    }

    public boolean wasDeleted() {
        return type.equals(Type.DELETED);
    }
}
