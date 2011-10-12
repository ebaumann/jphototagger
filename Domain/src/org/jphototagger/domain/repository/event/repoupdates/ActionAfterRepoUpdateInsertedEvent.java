package org.jphototagger.domain.repository.event.repoupdates;

import org.jphototagger.domain.programs.Program;

/**
 * @author Elmar Baumann
 */
public final class ActionAfterRepoUpdateInsertedEvent {

    private final Object source;
    private final Program action;

    public ActionAfterRepoUpdateInsertedEvent(Object source, Program action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        this.source = source;
        this.action = action;
    }

    public Program getAction() {
        return action;
    }

    public Object getSource() {
        return source;
    }
}
