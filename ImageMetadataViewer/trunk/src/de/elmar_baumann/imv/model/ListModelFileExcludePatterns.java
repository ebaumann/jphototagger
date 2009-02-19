package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.AppIcons;
import de.elmar_baumann.imv.database.DatabaseFileExcludePattern;
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

    private final DatabaseFileExcludePattern db = DatabaseFileExcludePattern.getInstance();
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
            errorMessage("Das Muster {0} existiert bereits!", trimmedPattern);
        }
        if (db.insertFileExcludePattern(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            errorMessage("Das Muster {0} konnte nicht hinzugefügt werden!", trimmedPattern);
        }
    }

    public void deletePattern(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.deleteFileExcludePattern(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessage("Das Muster {0} konnte nicht gelöscht werden!", trimmedPattern);
        }
    }

    private void errorMessage(String message, String pattern) {
        MessageFormat msg = new MessageFormat(message);
        JOptionPane.showMessageDialog(
            null, msg.format(new Object[]{pattern}),
            "Fehler",
            JOptionPane.ERROR_MESSAGE,
            AppIcons.getMediumAppIcon());
    }

    private void addElements() {
        patterns = db.getFileExcludePatterns();
        for (String pattern : patterns) {
            addElement(pattern);
        }
    }
}
