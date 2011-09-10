package org.jphototagger.program.event.listener;

import org.jphototagger.domain.database.programs.Program;

/**
 *
 * @author Elmar Baumann
 */
public interface ProgramExecutionListener {
    void execute(Program program);
}
