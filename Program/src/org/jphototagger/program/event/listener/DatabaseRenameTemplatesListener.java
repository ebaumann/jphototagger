/*
 * @(#)DatabaseRenameTemplatesListener.java    Created on 2010-03-01
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

package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.RenameTemplate;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
 *
 * @author  Elmar Baumann
 */
public interface DatabaseRenameTemplatesListener {

    /**
     * Will be called if a template was
     * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
     *
     * @param template  template
     */
    void templateDeleted(RenameTemplate template);

    /**
     * Will be called if a template was inserted into
     * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
     *
     * @param template inserted template
     */
    void templateInserted(RenameTemplate template);

    /**
     * Will be called if a template was updated in
     * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
     *
     * @param template updated template
     */
    void templateUpdated(RenameTemplate template);
}
