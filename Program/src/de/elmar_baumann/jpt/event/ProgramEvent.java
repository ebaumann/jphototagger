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
 * Event relating to a program. A program is outside this program and will be
 * called from this program. It displays or modifies images etc.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
public final class ProgramEvent {

    private final Type type;
    private final Program program;

    public enum Type {

        /**
         * A program was created
         */
        PROGRAM_CREATED,
        /**
         * A program was deleted
         */
        PROGRAM_DELETED,
        /**
         * A program was executed (will be executed)
         */
        PROGRAM_EXECUTED,
        /**
         * A program was updated
         */
        PROGRAM_UPDATED,
    }

    /**
     * Constructor.
     *
     * @param type    event type
     * @param program program
     */
    public ProgramEvent(Type type, Program program) {
        this.type = type;
        this.program = program;
    }

    /**
     * Returns the program.
     *
     * @return program
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Returns the event type.
     *
     * @return event type
     */
    public Type getType() {
        return type;
    }
}
