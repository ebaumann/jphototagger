package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/07
 */
public final class ListModelActionsAfterDbInsertion extends DefaultListModel
        implements DatabaseListener {

    public ListModelActionsAfterDbInsertion() {
        addItems();
        DatabasePrograms.INSTANCE.addDatabaseListener(this);
    }

    public void add(Program action) {
        assert action.isAction() : "Program is not an action!"; // NOI18N
        if (!contains(action) &&
                DatabaseActionsAfterDbInsertion.INSTANCE.insert(action,
                getSize())) {
            addElement(action);
        } else {
            errorMessage("ListModelActionsAfterDbInsertion.ErrorMessage.Add", // NOI18N
                    action.getAlias());
        }
    }

    public void moveUp(int index) {
        if (canMoveUp(index)) {
            swapElements(index, index - 1);
        }
    }

    public void moveDown(int index) {
        if (canMoveDown(index)) {
            swapElements(index, index + 1);
        }
    }

    public boolean canMoveUp(int index) {
        return index > 0 && index < getSize();
    }

    public boolean canMoveDown(int index) {
        return index >= 0 && index < getSize() - 1;
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
        if (ListUtil.swapModelElements(this, indexFirstElement,
                indexSecondElement)) {
            fireContentsChanged(this, indexFirstElement, indexFirstElement);
            fireContentsChanged(this, indexSecondElement, indexSecondElement);
            if (!DatabaseActionsAfterDbInsertion.INSTANCE.reorder(getActions(),
                    0)) {
                errorMessage(
                        "ListModelActionsAfterDbInsertion.ErrorMessage.Swap", // NOI18N
                        ((Program) get(indexFirstElement)).getAlias());
            }
        }
    }

    public void remove(Program action) {
        if (contains(action) && DatabaseActionsAfterDbInsertion.INSTANCE.delete(
                action)) {
            removeElement(action);
        } else {
            errorMessage("ListModelActionsAfterDbInsertion.ErrorMessage.Remove", // NOI18N
                    action.getAlias());
        }
    }

    private void errorMessage(String bundleKey, String alias) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString(bundleKey, alias),
                Bundle.getString(
                "ListModelActionsAfterDbInsertion.ErrorMessage.Title"), // NOI18N
                JOptionPane.ERROR_MESSAGE);
    }

    private void addItems() {
        List<Program> programs =
                DatabaseActionsAfterDbInsertion.INSTANCE.getAll();
        for (Program program : programs) {
            addElement(program);
        }
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        DatabaseProgramEvent.Type eventType = event.getType();
        Program program = event.getProgram();
        int index = indexOf(program);
        boolean contains = index >= 0;
        if (eventType.equals(DatabaseProgramEvent.Type.PROGRAM_DELETED) &&
                contains) {
            removeElementAt(index);
            fireIntervalRemoved(this, index, index);
        } else if (eventType.equals(DatabaseProgramEvent.Type.PROGRAM_UPDATED) &&
                contains) {
            set(index, program);
            fireContentsChanged(this, index, index);
        }
    }

    @Override
    public void actionPerformed(DatabaseImageEvent action) {
        // nothing to do
    }
}
