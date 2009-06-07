package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
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
        assert action.isAction() : "Program is not an action!";
        if (!contains(action) &&
                DatabaseActionsAfterDbInsertion.INSTANCE.insert(action,
                getSize())) {
            addElement(action);
        } else {
            errorMessage("ListModelActionsAfterDbInsertion.ErrorMessage.Add",
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
            if (!DatabaseActionsAfterDbInsertion.INSTANCE.reorder(getActions(), 0)) {
                errorMessage("ListModelActionsAfterDbInsertion.ErrorMessage.Swap",
                        ((Program)get(indexFirstElement)).getAlias());
            }
        }
    }

    public void remove(Program action) {
        if (contains(action) && DatabaseActionsAfterDbInsertion.INSTANCE.delete(
                action)) {
            removeElement(action);
        } else {
            errorMessage("ListModelActionsAfterDbInsertion.ErrorMessage.Remove",
                    action.getAlias());
        }
    }

    private void errorMessage(String bundleKey, String alias) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString(bundleKey, alias),
                Bundle.getString(
                "ListModelActionsAfterDbInsertion.ErrorMessage.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void addItems() {
        List<Program> programs = DatabaseActionsAfterDbInsertion.INSTANCE.getAll();
        for (Program program : programs) {
            addElement(program);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type type = action.getType();
        Program program = action.getProgram();
        int index = indexOf(program);
        boolean contains = program != null && index >= 0;
        if (type.equals(DatabaseAction.Type.PROGRAM_DELETED) && contains) {
            removeElementAt(index);
            fireIntervalRemoved(this, index, index);
        } else if (type.equals(DatabaseAction.Type.PROGRAM_UPDATED) && contains) {
            set(index, program);
            fireContentsChanged(this, index, index);
        }
    }
}
