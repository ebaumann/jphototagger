/*
 * @(#)DatabaseProgramsListener.java    Created on 2010-01-11
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

import org.jphototagger.program.data.Program;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabasePrograms}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseProgramsListener {

    /**
     * Called if a programs was deleted from
     * {@link org.jphototagger.program.database.DatabasePrograms}.
     *
     * @param program deleted program
     */
    public void programDeleted(Program program);

    /**
     * Called if a programs was inserted into
     * {@link org.jphototagger.program.database.DatabasePrograms}.
     *
     * @param program inserted program
     */
    public void programInserted(Program program);

    /**
     * Called if a programs was updated in
     * {@link org.jphototagger.program.database.DatabasePrograms}.
     *
     * @param program updated program
     */
    public void programUpdated(Program program);
}
