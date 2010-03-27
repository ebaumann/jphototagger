/*
 * @(#)ListModelPrograms.java    Created on 2008-10-16
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.database.DatabasePrograms.Type;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;

import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Contains {@link Program}s retrieved through
 * {@link DatabasePrograms#getAll(DatabasePrograms.Type)}.
 *
 * All programs in this model are actions, where
 * {@link org.jphototagger.program.data.Program#isAction()} is true, <em>or</em>
 * programs, where that method returns <code>false</code>.
 *
 * @author  Elmar Baumann
 */
public final class ListModelPrograms extends DefaultListModel
        implements DatabaseProgramsListener {
    private static final long serialVersionUID = 1107244876982338977L;
    private boolean           listenToDb       = true;
    private Type              type;

    public ListModelPrograms(Type type) {
        this.type = type;
        addElements();
        DatabasePrograms.INSTANCE.addListener(this);
    }

    public void add(Program program) {
        listenToDb = false;

        if (!contains(program) && DatabasePrograms.INSTANCE.insert(program)) {
            addElement(program);
        } else {
            MessageDisplayer.error(null, "ListModelPrograms.Error.Add",
                                   program.getAlias());
        }

        listenToDb = true;
    }

    public void delete(Program program) {
        listenToDb = false;

        if (contains(program) && DatabasePrograms.INSTANCE.delete(program)) {
            removeElement(program);
        } else {
            MessageDisplayer.error(null, "ListModelPrograms.Error.Remove",
                                   program.getAlias());
        }

        listenToDb = true;
    }

    public void update(Program program) {
        listenToDb = false;

        if (contains(program) && DatabasePrograms.INSTANCE.update(program)) {
            int index = indexOf(program);

            fireContentsChanged(this, index, index);
        } else {
            MessageDisplayer.error(null, "ListModelPrograms.Error.Update",
                                   program.getAlias());
        }

        listenToDb = true;
    }

    private void addElements() {
        List<Program> programs = DatabasePrograms.INSTANCE.getAll(type);

        for (Program program : programs) {
            addElement(program);
        }
    }

    private boolean isAppropriateProgramType(Program program) {
        return (program.isAction() && type.equals(Type.ACTION))
               || (!program.isAction() && type.equals(Type.PROGRAM));
    }

    @Override
    public void programDeleted(Program program) {
        if (listenToDb && isAppropriateProgramType(program)) {
            removeElement(program);
        }
    }

    @Override
    public void programInserted(Program program) {
        if (listenToDb && isAppropriateProgramType(program)) {
            addElement(program);
        }
    }

    @Override
    public void programUpdated(Program program) {
        if (listenToDb && isAppropriateProgramType(program)) {
            int index = indexOf(program);

            if (index >= 0) {
                set(index, program);
            }
        }
    }
}
