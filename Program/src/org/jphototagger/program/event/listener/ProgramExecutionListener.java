package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.Program;

/**
 *
 * @author Elmar Baumann
 */
public interface ProgramExecutionListener {
    void execute(Program program);
}
