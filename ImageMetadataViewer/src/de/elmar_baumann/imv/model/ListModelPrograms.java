package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

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

    public void add(Program program) {
        if (!contains(program) && DatabasePrograms.getInstance().insert(program)) {
            addElement(program);
        } else {
            errorMessage(Bundle.getString("ListModelPrograms.ErrorMessage.Add"),
                program.getAlias());
        }
    }

    public void remove(Program program) {
        if (contains(program) && DatabasePrograms.getInstance().delete(program)) {
            removeElement(program);
        } else {
            errorMessage(Bundle.getString("ListModelPrograms.ErrorMessage.Remove"),
                program.getAlias());
        }
    }

    public void update(Program program) {
        if (contains(program) && DatabasePrograms.getInstance().update(program)) {
            int index = indexOf(program);
            fireContentsChanged(this, index, index);
        } else {
            errorMessage(Bundle.getString("ListModelPrograms.ErrorMessage.Update"), program.getAlias());
        }
    }

    private void errorMessage(String format, String alias) {
        MessageFormat msg = new MessageFormat(format);
        JOptionPane.showMessageDialog(
            null,
            msg.format(new Object[]{alias}),
            Bundle.getString("ListModelPrograms.ErrorMessage.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void addItems() {
        List<Program> programs = DatabasePrograms.getInstance().getAll();
        for (Program program : programs) {
            addElement(program);
        }
    }
}
