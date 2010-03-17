/*
 * @(#)ListModelActionsAfterDbInsertion.java    2009-06-07
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

package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.event.DatabaseProgramsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseProgramsListener;
import de.elmar_baumann.lib.componentutil.ListUtil;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Elements are {@link Program}s retrieved through
 * {@link DatabaseActionsAfterDbInsertion#getAll()}.
 *
 * The programs are actions, {@link Program#isAction()} is true for every
 * element in this model. All actions shall be executed after inserting metadata
 * into the database.
 *
 * @author  Elmar Baumann
 */
public final class ListModelActionsAfterDbInsertion extends DefaultListModel
        implements DatabaseProgramsListener {
    private static final long serialVersionUID = -6490813457178023686L;

    public ListModelActionsAfterDbInsertion() {
        addElements();
        DatabasePrograms.INSTANCE.addListener(this);
    }

    public void insert(Program action) {
        assert action.isAction() : action;

        if (!contains(action)
                && DatabaseActionsAfterDbInsertion.INSTANCE.insert(action,
                    getSize())) {
            addElement(action);
        } else {
            errorMessageInsert(action);
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
        return (index > 0) && (index < getSize());
    }

    public boolean canMoveDown(int index) {
        return (index >= 0) && (index < getSize() - 1);
    }

    private List<Program> getActions() {
        Object[]      array   = toArray();
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

            if (!DatabaseActionsAfterDbInsertion.INSTANCE.setOrder(
                    getActions(), 0)) {
                errorMessageSwap(indexFirstElement);
            }
        }
    }

    public void delete(Program action) {
        if (contains(action)
                && DatabaseActionsAfterDbInsertion.INSTANCE.delete(action)) {
            removeElement(action);
        } else {
            errorMessageDelete(action);
        }
    }

    private void addElements() {
        List<Program> actions =
            DatabaseActionsAfterDbInsertion.INSTANCE.getAll();

        for (Program action : actions) {
            addElement(action);
        }
    }

    @Override
    public void actionPerformed(DatabaseProgramsEvent event) {
        DatabaseProgramsEvent.Type eventType = event.getType();
        Program                    program   = event.getProgram();
        int                        index     = indexOf(program);
        boolean                    contains  = index >= 0;

        if (eventType.equals(DatabaseProgramsEvent.Type.PROGRAM_DELETED)
                && contains) {
            removeElementAt(index);
            fireIntervalRemoved(this, index, index);
        } else if (eventType.equals(DatabaseProgramsEvent.Type.PROGRAM_UPDATED)
                   && contains) {
            set(index, program);
            fireContentsChanged(this, index, index);
        }
    }

    private void errorMessageDelete(Program action) {
        MessageDisplayer.error(null,
                               "ListModelActionsAfterDbInsertion.Error.Remove",
                               action);
    }

    private void errorMessageSwap(int indexFirstElement) {
        MessageDisplayer.error(null,
                               "ListModelActionsAfterDbInsertion.Error.Swap",
                               (Program) get(indexFirstElement));
    }

    private void errorMessageInsert(Program action) {
        MessageDisplayer.error(null,
                               "ListModelActionsAfterDbInsertion.Error.Add",
                               action);
    }
}
