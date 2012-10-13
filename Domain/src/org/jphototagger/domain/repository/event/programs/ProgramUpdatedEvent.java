package org.jphototagger.domain.repository.event.programs;

import org.jphototagger.domain.programs.Program;

/**
 * @author Elmar Baumann
 */
public final class ProgramUpdatedEvent {

    private final Object source;
    private final Program program;

    public ProgramUpdatedEvent(Object source, Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        this.source = source;
        this.program = program;
    }

    public Program getProgram() {
        return program;
    }

    public Object getSource() {
        return source;
    }
}
