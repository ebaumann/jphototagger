package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.database.DatabaseFileExcludePattern;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-09
 */
public final class ListModelFileExcludePatterns extends DefaultListModel {

    private final DatabaseFileExcludePattern db =
            DatabaseFileExcludePattern.INSTANCE;
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
            MessageDisplayer.error(
                    "ListModelFileExcludePatterns.Error.InsertPattern.Exists", // NOI18N
                    trimmedPattern);
        }
        if (db.insertFileExcludePattern(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            MessageDisplayer.error(
                    "ListModelFileExcludePatterns.Error.InsertPattern.Add", // NOI18N
                    trimmedPattern);
        }
    }

    public void deletePattern(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.deleteFileExcludePattern(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            MessageDisplayer.error(
                    "ListModelFileExcludePatterns.Error.Delete", // NOI18N
                    trimmedPattern);
        }
    }

    private void addElements() {
        patterns = db.getFileExcludePatterns();
        for (String pattern : patterns) {
            addElement(pattern);
        }
    }
}
