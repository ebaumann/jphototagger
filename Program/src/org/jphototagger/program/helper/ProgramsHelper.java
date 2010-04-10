/*
 * @(#)ProgramsHelper.java    Created on 2010-04-10
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

package org.jphototagger.program.helper;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ProgramsHelper {

    /**
     * Moves in a list with {@link Program}s the selected progam up and reorders
     * all program's sequence numbers in the database.
     * <p>
     * Does nothing if the program can't be moved: No list item or the first
     * list item is selected.
     *
     * @param listPrograms list with {@link DefaultListModel} as model and
     *                     {@link Program} as values
     */
    public static void moveProgramUp(JList listPrograms) {
        if (listPrograms == null) {
            throw new NullPointerException("listPrograms == null");
        }

        int              selIndex        = listPrograms.getSelectedIndex();
        int              upIndex         = selIndex - 1;
        boolean          programSelected = listPrograms.getSelectedIndex() >= 0;
        DefaultListModel model = (DefaultListModel) listPrograms.getModel();

        if (programSelected && (upIndex >= 0)) {
            ListUtil.swapModelElements(model, upIndex, selIndex);
            reorderPrograms(model);
            listPrograms.setSelectedIndex(upIndex);
        }
    }

    /**
     * Moves in a list with {@link Program}s the selected progam down and
     * reorders all program's sequence numbers in the database.
     * <p>
     * Does nothing if the program can't be moved: No list item or the last
     * list item is selected.
     *
     * @param listPrograms list with {@link DefaultListModel} as model and
     *                     {@link Program} as values
     */
    public static void moveProgramDown(JList listPrograms) {
        if (listPrograms == null) {
            throw new NullPointerException("listPrograms == null");
        }

        DefaultListModel model = (DefaultListModel) listPrograms.getModel();
        int              size            = model.getSize();
        int              selIndex        = listPrograms.getSelectedIndex();
        int              downIndex       = selIndex + 1;
        boolean          programSelected = listPrograms.getSelectedIndex() >= 0;

        if (programSelected && (downIndex < size)) {
            ListUtil.swapModelElements(model, downIndex, selIndex);
            reorderPrograms(model);
            listPrograms.setSelectedIndex(downIndex);
        }
    }

    /**
     * Reorders in the database the programs sequence number to match their
     * order a list model.
     *
     * @param model model with {@link Program}s as elements
     */
    public static void reorderPrograms(DefaultListModel model) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        int           size     = model.getSize();
        List<Program> programs = new ArrayList<Program>(size);

        for (int sequenceNo = 0; sequenceNo < size; sequenceNo++) {
            Object  o       = model.get(sequenceNo);
            Program program = (Program) o;

            program.setSequenceNumber(sequenceNo);
            programs.add(program);
        }

        for (Program program : programs) {
            DatabasePrograms.INSTANCE.update(program);
        }
    }

    public static class ReorderListener implements ListDataListener {
        private volatile boolean       listenToModel = true;
        private final DefaultListModel model;

        public ReorderListener(DefaultListModel model) {
            if (model == null) {
                throw new NullPointerException("model == null");
            }

            this.model = model;
            model.addListDataListener(this);
        }

        public void setListenToModel(boolean listenToModel) {
            this.listenToModel = listenToModel;
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            if (listenToModel) {
                listenToModel = false;
                ProgramsHelper.reorderPrograms(model);
                listenToModel = true;
            }
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            if (listenToModel) {
                listenToModel = false;
                ProgramsHelper.reorderPrograms(model);
                listenToModel = true;
            }
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            if (listenToModel) {
                listenToModel = false;
                ProgramsHelper.reorderPrograms(model);
                listenToModel = true;
            }
        }
    }


    private ProgramsHelper() {}
}
