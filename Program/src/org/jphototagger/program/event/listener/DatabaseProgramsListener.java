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
    void programDeleted(Program program);

    /**
     * Called if a programs was inserted into
     * {@link org.jphototagger.program.database.DatabasePrograms}.
     *
     * @param program inserted program
     */
    void programInserted(Program program);

    /**
     * Called if a programs was updated in
     * {@link org.jphototagger.program.database.DatabasePrograms}.
     *
     * @param program updated program
     */
    void programUpdated(Program program);
}
