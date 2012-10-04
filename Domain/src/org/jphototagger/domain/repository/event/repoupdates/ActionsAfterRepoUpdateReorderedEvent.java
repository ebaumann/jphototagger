package org.jphototagger.domain.repository.event.repoupdates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jphototagger.domain.programs.Program;

/**
 * @author Elmar Baumann
 */
public final class ActionsAfterRepoUpdateReorderedEvent {

    private final Object source;
    private final List<Program> actions;

    public ActionsAfterRepoUpdateReorderedEvent(Object source, List<Program> actions) {
        if (actions == null) {
            throw new NullPointerException("actions == null");
        }

        this.source = source;
        this.actions = new ArrayList<Program>(actions);
    }

    public List<Program> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public Object getSource() {
        return source;
    }
}
