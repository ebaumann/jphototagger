package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.Program;

/**
 * Listens to events in {@link org.jphototagger.program.database.DatabaseActionsAfterDbInsertion}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseActionsAfterDbInsertionListener {

    /**
     * A program was inserted into the database.
     *
     * @param program inserted program
     */
    void programInserted(Program program);

    /**
     * A program was delete from the database.
     *
     * @param program deleted program
     */
    void programDeleted(Program program);

    /**
     * All programs were reordered.
     */
    void programsReordered();
}
