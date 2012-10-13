package org.jphototagger.program.module.actions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.domain.repository.event.repoupdates.ActionAfterRepoUpdateDeletedEvent;
import org.jphototagger.domain.repository.event.repoupdates.ActionAfterRepoUpdateInsertedEvent;
import org.jphototagger.domain.repository.event.repoupdates.ActionsAfterRepoUpdateReorderedEvent;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ActionsAfterSavesOrUpdatesInRepositoryListModel extends DefaultListModel<Object> {

    private static final long serialVersionUID = 1L;
    private final ActionsAfterRepoUpdatesRepository actionsRepo = Lookup.getDefault().lookup(ActionsAfterRepoUpdatesRepository.class);

    public ActionsAfterSavesOrUpdatesInRepositoryListModel() {
        addElements();
        listen();
    }

    private void addElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        List<Program> actions = actionsRepo.findAllActions();

        for (Program action : actions) {
            addElement(action);
        }
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ActionAfterRepoUpdateInsertedEvent.class)
    public void actionInserted(ActionAfterRepoUpdateInsertedEvent event) {
        Program action = event.getAction();
        addElement(action);
    }

    @EventSubscriber(eventClass = ActionAfterRepoUpdateDeletedEvent.class)
    public void actionDeleted(ActionAfterRepoUpdateDeletedEvent event) {
        Program action = event.getAction();
        removeElement(action);
    }

    @EventSubscriber(eventClass = ActionsAfterRepoUpdateReorderedEvent.class)
    public void actionsReordered(ActionsAfterRepoUpdateReorderedEvent evt) {
        removeAllElements();
        addElements();
    }

    public List<Program> getActions() {
        Object[] array = toArray();
        List<Program> actions = new ArrayList<>(array.length);

        for (Object o : array) {
            actions.add((Program) o);
        }

        return actions;
    }

    private void updateProgram(Program program) {
        int index = indexOf(program);

        if (index >= 0) {
            set(index, program);
            fireContentsChanged(this, index, index);
        }
    }

    private void deleteProgram(Program program) {
        int index = indexOf(program);

        if (index >= 0) {
            removeElementAt(index);
        }
    }

    @EventSubscriber(eventClass = ProgramDeletedEvent.class)
    public void programDeleted(final ProgramDeletedEvent evt) {
        deleteProgram(evt.getProgram());
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        updateProgram(evt.getProgram());
    }
}
