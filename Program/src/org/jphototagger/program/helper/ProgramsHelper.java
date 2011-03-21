package org.jphototagger.program.helper;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.ProgramPropertiesDialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.jdesktop.swingx.JXList;

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
    public static void moveProgramUp(JXList listPrograms) {
        if (listPrograms == null) {
            throw new NullPointerException("listPrograms == null");
        }

        int selectedIndex = listPrograms.getSelectedIndex();
        int modelIndex = listPrograms.convertIndexToModel(selectedIndex);
        int upIndex = modelIndex - 1;
        boolean programIsSelected = selectedIndex >= 0;
        DefaultListModel model = (DefaultListModel) listPrograms.getModel();

        if (programIsSelected && (upIndex >= 0)) {
            ListUtil.swapModelElements(model, upIndex, modelIndex);
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
    public static void moveProgramDown(JXList listPrograms) {
        if (listPrograms == null) {
            throw new NullPointerException("listPrograms == null");
        }

        DefaultListModel model = (DefaultListModel) listPrograms.getModel();
        int size = model.getSize();
        int selectedIndex = listPrograms.getSelectedIndex();
        int modelIndex = listPrograms.convertIndexToModel(selectedIndex);
        int downIndex = modelIndex + 1;
        boolean programIsSelected = selectedIndex >= 0;

        if (programIsSelected && (downIndex < size)) {
            ListUtil.swapModelElements(model, downIndex, modelIndex);
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

        int size = model.getSize();
        List<Program> programs = new ArrayList<Program>(size);

        for (int sequenceNo = 0; sequenceNo < size; sequenceNo++) {
            Program program = (Program) model.get(sequenceNo);

            program.setSequenceNumber(sequenceNo);
            programs.add(program);
        }

        for (Program program : programs) {
            DatabasePrograms.INSTANCE.update(program);
        }
    }

    public static class ReorderListener implements ListDataListener {
        private volatile boolean listenToModel = true;
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
            reorder();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            reorder();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            reorder();
        }

        private void reorder() {
            if (listenToModel) {
                listenToModel = false;
                reorderPrograms(model);
                listenToModel = true;
            }
        }
    }

    public static Program addProgram() {
        ProgramPropertiesDialog dlg = new ProgramPropertiesDialog(false);

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            Program program = dlg.getProgram();

            DatabasePrograms.INSTANCE.insert(program);

            return program;
        }

        return null;
    }

    /**
     *
     * @param program may be null
     */
    public static void openSelectedFilesWidth(Program program) {
        if (program != null) {
            new StartPrograms(null).startProgram(program, GUI.getSelectedImageFiles());
        }
    }

    private ProgramsHelper() {}
}
