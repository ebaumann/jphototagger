/*
 * @(#)DatabaseRenameTemplatesEvent.java    2010-03-01
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

import de.elmar_baumann.jpt.data.RenameTemplate;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class DatabaseRenameTemplatesEvent {
    public enum Type { TEMPLATE_INSERTED, TEMPLATE_UPDATED, TEMPLATE_DELETED }

    private final Type           type;
    private final RenameTemplate template;

    public DatabaseRenameTemplatesEvent(Type type, RenameTemplate template) {
        this.type     = type;
        this.template = (template == null)
                        ? null
                        : new RenameTemplate(template);
    }

    public RenameTemplate getTemplate() {
        return (template == null)
               ? null
               : new RenameTemplate(template);
    }

    public Type getType() {
        return type;
    }

    public boolean isTemplateInserted() {
        return type.equals(Type.TEMPLATE_INSERTED);
    }

    public boolean isTemplateUpdated() {
        return type.equals(Type.TEMPLATE_UPDATED);
    }

    public boolean isTemplateDeleted() {
        return type.equals(Type.TEMPLATE_DELETED);
    }
}
