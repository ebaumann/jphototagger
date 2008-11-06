package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.Program;

/**
 * Event in {@link de.elmar_baumann.imv.view.dialogs.ActionsDialog}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public class DialogActionsEvent {

    private Type type;
    private Program program;

    public enum Type {

        ActionCreated,
        ActionDeleted,
        ActionExecute,
        ActionUpdated,
    }

    public DialogActionsEvent(Type type, Program program) {
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
        return type.equals(Type.ActionExecute);
    }
}
