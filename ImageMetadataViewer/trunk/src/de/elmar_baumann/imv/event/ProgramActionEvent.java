package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.Program;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public final class ProgramActionEvent {

    private final Type type;
    private final Program program;

    public enum Type {

        ACTION_CREATED,
        ACTION_DELETED,
        ACTION_EXECUTE,
        ACTION_UPDATED,
    }

    public ProgramActionEvent(Type type, Program program) {
        this.type = type;
        this.program = program;
    }

    public Program getProgram() {
        return program;
    }

    public Type getType() {
        return type;
    }

    public boolean isExecute() {
        return type.equals(Type.ACTION_EXECUTE);
    }
}
