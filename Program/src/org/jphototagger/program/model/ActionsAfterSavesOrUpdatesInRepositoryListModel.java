package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Elmar Baumann
 */
public final class ActionsAfterSavesOrUpdatesInRepositoryListModel extends DefaultListModel {

    private static final long serialVersionUID = -6490813457178023686L;
    private final ActionsAfterRepoUpdatesRepository actionsRepo = Lookup.getDefault().lookup(ActionsAfterRepoUpdatesRepository.class);

    public ActionsAfterSavesOrUpdatesInRepositoryListModel() {
        addElements();
        AnnotationProcessor.process(this);
    }

    public void insert(final Program action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        assert action.isAction() : action;

        if (!contains(action) && actionsRepo.saveAction(action, getSize())) {
            addElement(action);
        } else {
            errorMessageInsert(action);
        }
    }

    public void moveUp(final int index) {
        if (canMoveUp(index)) {
            swapElements(index, index - 1);
        }
    }

    public void moveDown(final int index) {
        if (canMoveDown(index)) {
            swapElements(index, index + 1);
        }
    }

    public boolean canMoveUp(int index) {
        return (index > 0) && (index < getSize());
    }

    public boolean canMoveDown(int index) {
        return (index >= 0) && (index < getSize() - 1);
    }

    private List<Program> getActions() {
        Object[] array = toArray();
        List<Program> actions = new ArrayList<Program>(array.length);

        for (Object o : array) {
            actions.add((Program) o);
        }

        return actions;
    }

    private void swapElements(int indexFirstElement, int indexSecondElement) {
        if (ListUtil.swapModelElements(this, indexFirstElement, indexSecondElement)) {
            fireContentsChanged(this, indexFirstElement, indexFirstElement);
            fireContentsChanged(this, indexSecondElement, indexSecondElement);

            if (!actionsRepo.setActionOrder(getActions(), 0)) {
                errorMessageSwap(indexFirstElement);
            }
        }
    }

    public void delete(final Program action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        if (contains(action) && actionsRepo.deleteAction(action)) {
            removeElement(action);
        } else {
            errorMessageDelete(action);
        }
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
            fireIntervalRemoved(this, index, index);
        }
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

    private void errorMessageDelete(Program action) {
        String message = Bundle.getString(ActionsAfterSavesOrUpdatesInRepositoryListModel.class, "ActionsAfterSavesOrUpdatesInRepositoryListModel.Error.Remove", action);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageSwap(int indexFirstElement) {
        Program program = (Program) get(indexFirstElement);
        String message = Bundle.getString(ActionsAfterSavesOrUpdatesInRepositoryListModel.class, "ActionsAfterSavesOrUpdatesInRepositoryListModel.Error.Swap", program);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageInsert(Program action) {
        String message = Bundle.getString(ActionsAfterSavesOrUpdatesInRepositoryListModel.class, "ActionsAfterSavesOrUpdatesInRepositoryListModel.Error.Add", action);
        MessageDisplayer.error(null, message);
    }

    @EventSubscriber(eventClass = ProgramDeletedEvent.class)
    public void programDeleted(final ProgramDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                deleteProgram(evt.getProgram());
            }
        });
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                updateProgram(evt.getProgram());
            }
        });
    }
}
