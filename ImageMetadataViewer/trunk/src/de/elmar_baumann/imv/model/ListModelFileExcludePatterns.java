package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.DatabaseFileExcludePattern;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/09
 */
public final class ListModelFileExcludePatterns extends DefaultListModel {

    private final DatabaseFileExcludePattern db = DatabaseFileExcludePattern.INSTANCE;
    private List<String> patterns;

    public ListModelFileExcludePatterns() {
        addElements();
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void insertPattern(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.existsFileExcludePattern(trimmedPattern)) {
            errorMessage(Bundle.getString("ListModelFileExcludePatterns.ErrorMessage.InsertPattern.Exists"), trimmedPattern);
        }
        if (db.insertFileExcludePattern(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            errorMessage(Bundle.getString("ListModelFileExcludePatterns.ErrorMessage.InsertPattern.Add"), trimmedPattern);
        }
    }

    public void deletePattern(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.deleteFileExcludePattern(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessage(Bundle.getString("ListModelFileExcludePatterns.ErrorMessage.Delete"), trimmedPattern);
        }
    }

    private void errorMessage(String message, String pattern) {
        MessageFormat msg = new MessageFormat(message);
        JOptionPane.showMessageDialog(
            null, msg.format(new Object[]{pattern}),
            Bundle.getString("ListModelFileExcludePatterns.ErrorMessage.Title"),
            JOptionPane.ERROR_MESSAGE);
    }

    private void addElements() {
        patterns = db.getFileExcludePatterns();
        for (String pattern : patterns) {
            addElement(pattern);
        }
    }
}
