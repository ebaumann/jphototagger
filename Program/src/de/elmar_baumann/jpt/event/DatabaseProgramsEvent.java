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

import de.elmar_baumann.jpt.data.Program;

/**
 * Event in a database related to an program.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-19
 */
public final class DatabaseProgramsEvent {

    /**
     * Event type.
     */
    public enum Type {

        PROGRAM_INSERTED,
        PROGRAM_DELETED,
        PROGRAM_UPDATED,
    };

    private Type type;
    private Program program;

    public DatabaseProgramsEvent(Type type) {
        this.type = type;
    }

    /**
     * Returns the event type.
     *
     * @return event type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the event type.
     *
     * @param type event type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the related program.
     *
     * @return program
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Sets the related program.
     *
     * @param program program
     */
    public void setProgram(Program program) {
        this.program = program;
    }
}
