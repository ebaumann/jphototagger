/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.data.RenameTemplate;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-01
 */
public final class DatabaseRenameTemplatesEvent {

    public enum Type {
        TEMPLATE_INSERTED,
        TEMPLATE_UPDATED,
        TEMPLATE_DELETED
    }

    private final Type           type;
    private final RenameTemplate template;
    private       RenameTemplate oldTemplate;

    public DatabaseRenameTemplatesEvent(Type type, RenameTemplate template) {
        this.type     = type;
        this.template = new RenameTemplate(template);
    }

    public RenameTemplate getOldTemplate() {
        return oldTemplate == null ? null : new RenameTemplate(oldTemplate);
    }

    public void setOldTemplate(RenameTemplate oldTemplate) {
        this.oldTemplate = template == null ? null : new RenameTemplate(oldTemplate);
    }

    public RenameTemplate getTemplate() {
        return new RenameTemplate(template);
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
