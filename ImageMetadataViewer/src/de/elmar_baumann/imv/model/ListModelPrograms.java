package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/16
 */
public class ListModelPrograms extends DefaultListModel {

    public ListModelPrograms() {
        addItems();
    }

    private void addItems() {
        List<Program> programs = DatabasePrograms.getInstance().selectAll();
        for (Program program : programs) {
            addElement(program);
        }
    }
}
