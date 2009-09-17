/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Model for {@link de.elmar_baumann.jpt.data.Program} where
 * {@link de.elmar_baumann.jpt.data.Program#isAction()} is false.
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
