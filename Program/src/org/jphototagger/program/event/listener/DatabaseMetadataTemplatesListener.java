/*
 * @(#)DatabaseMetadataTemplatesListener.java    Created on 2010-01-05
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.MetadataTemplate;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
 *
 * @author  Elmar Baumann
 */
public interface DatabaseMetadataTemplatesListener {

    /**
     * Called if a template was deleted from
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param template  template
     */
    void templateDeleted(MetadataTemplate template);

    /**
     * Called if a template was inserted into
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param template inserted template
     */
    void templateInserted(MetadataTemplate template);

    /**
     * Called if a template was updated in
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param oldTemplate     old template before update
     * @param updatedTemplate updated template
     */
    void templateUpdated(MetadataTemplate oldTemplate,
                                MetadataTemplate updatedTemplate);

    /**
     * Called if a template was renamed in
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param fromName old template name
     * @param toName   new template name
     */
    void templateRenamed(String fromName, String toName);
}
