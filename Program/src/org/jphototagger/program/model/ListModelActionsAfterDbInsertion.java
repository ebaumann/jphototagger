package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.database.programs.Program;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseActionsAfterDbInsertion;

/**
 * Elements are {@link Program}s retrieved through
 * {@link DatabaseActionsAfterDbInsertion#getAllActions()}.
 *
 * The programs are actions, {@link Program#isAction()} is true for every
 * element in this model. All actions shall be executed after inserting metadata
 * into the database.
 *
 * @author Elmar Baumann
 */
public final class ListModelActionsAfterDbInsertion extends DefaultListModel {

    private static final long serialVersionUID = -6490813457178023686L;

    public ListModelActionsAfterDbInsertion() {
        addElements();
        AnnotationProcessor.process(this);
    }

    public void insert(final Program action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        assert action.isAction() : action;

        if (!contains(action) && DatabaseActionsAfterDbInsertion.INSTANCE.insertAction(action, getSize())) {
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

            if (!DatabaseActionsAfterDbInsertion.INSTANCE.setActionOrder(getActions(), 0)) {
                errorMessageSwap(indexFirstElement);
            }
        }
    }

    public void delete(final Program action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        if (contains(action) && DatabaseActionsAfterDbInsertion.INSTANCE.deleteAction(action)) {
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
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<Program> actions = DatabaseActionsAfterDbInsertion.INSTANCE.getAllActions();

        for (Program action : actions) {
            addElement(action);
        }
    }

    private void errorMessageDelete(Program action) {
        String message = Bundle.getString(ListModelActionsAfterDbInsertion.class, "ListModelActionsAfterDbInsertion.Error.Remove", action);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageSwap(int indexFirstElement) {
        Program program = (Program) get(indexFirstElement);
        String message = Bundle.getString(ListModelActionsAfterDbInsertion.class, "ListModelActionsAfterDbInsertion.Error.Swap", program);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageInsert(Program action) {
        String message = Bundle.getString(ListModelActionsAfterDbInsertion.class, "ListModelActionsAfterDbInsertion.Error.Add", action);
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
