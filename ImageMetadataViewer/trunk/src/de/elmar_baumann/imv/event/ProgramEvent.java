package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.Program;

/**
 * Event relating to a program. A program is outside this program and will be
 * called from this program. It displays or modifies images etc.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
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
