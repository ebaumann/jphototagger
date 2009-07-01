package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Model for {@link de.elmar_baumann.imv.data.Program} where
 * {@link de.elmar_baumann.imv.data.Program#isAction()} is false.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/16
 */
public final class ListModelPrograms extends DefaultListModel {

    private boolean action;

    public ListModelPrograms(boolean action) {
        this.action = action;
        addElements();
    }

    public void add(Program program) {
        if (!contains(program) && DatabasePrograms.INSTANCE.insert(program)) {
            addElement(program);
        } else {
            errorMessage("ListModelPrograms.ErrorMessage.Add",
                    program.getAlias());
        }
    }

    public void remove(Program program) {
        if (contains(program) && DatabasePrograms.INSTANCE.delete(program)) {
            removeElement(program);
        } else {
            errorMessage("ListModelPrograms.ErrorMessage.Remove",
                    program.getAlias());
        }
    }

    public void update(Program program) {
        if (contains(program) && DatabasePrograms.INSTANCE.update(program)) {
            int index = indexOf(program);
            fireContentsChanged(this, index, index);
        } else {
            errorMessage("ListModelPrograms.ErrorMessage.Update", program.getAlias());
        }
    }

    private void errorMessage(String bundleKey, String alias) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString(bundleKey, alias),
                Bundle.getString("ListModelPrograms.ErrorMessage.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void addElements() {
        List<Program> programs = DatabasePrograms.INSTANCE.getAll(action);
        for (Program program : programs) {
            addElement(program);
        }
    }
}
