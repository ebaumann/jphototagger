package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.Program;

/**
 * Event in a database related to an program.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-19
 */
public final class DatabaseProgramEvent {

    /**
     * Event type.
     */
    public enum Type {

        /**
         * A program was deleted
         */
        PROGRAM_DELETED,
        /**
         * A program was updated
         */
        PROGRAM_UPDATED,
    };
    private Type type;
    private Program program;

    public DatabaseProgramEvent(Type type) {
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
