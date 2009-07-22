package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Model for {@link de.elmar_baumann.imv.data.Program} where
 * {@link de.elmar_baumann.imv.data.Program#isAction()} is false.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-16
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
            MessageDisplayer.error(null, "ListModelPrograms.Error.Add", // NOI18N
                    program.getAlias());
        }
    }

    public void remove(Program program) {
        if (contains(program) && DatabasePrograms.INSTANCE.delete(program)) {
            removeElement(program);
        } else {
            MessageDisplayer.error(null, "ListModelPrograms.Error.Remove", // NOI18N
                    program.getAlias());
        }
    }

    public void update(Program program) {
        if (contains(program) && DatabasePrograms.INSTANCE.update(program)) {
            int index = indexOf(program);
            fireContentsChanged(this, index, index);
        } else {
            MessageDisplayer.error(null, "ListModelPrograms.Error.Update", // NOI18N
                    program.getAlias());
        }
    }

    private void addElements() {
        List<Program> programs = DatabasePrograms.INSTANCE.getAll(action);
        for (Program program : programs) {
            addElement(program);
        }
    }
}
