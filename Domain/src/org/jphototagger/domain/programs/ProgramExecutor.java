package org.jphototagger.domain.programs;

import org.jphototagger.domain.database.programs.Program;

/**
 *
 * @author Elmar Baumann
 */
public interface ProgramExecutor {

    void execute(Program program);
}
