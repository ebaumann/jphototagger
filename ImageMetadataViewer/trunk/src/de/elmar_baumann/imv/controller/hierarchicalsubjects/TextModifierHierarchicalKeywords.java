package de.elmar_baumann.imv.controller.hierarchicalsubjects;

import de.elmar_baumann.imv.database.DatabaseHierarchicalSubjects;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.TextModifyer;
import de.elmar_baumann.imv.view.dialogs.PathSelectionDialog;
import de.elmar_baumann.imv.view.panels.EditRepeatableTextEntryPanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Modifies text returned from an {@link EditRepeatableTextEntryPanel}. Searches
 * for parent keywords and adds them.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public class TextModifierHierarchicalKeywords implements TextModifyer {

    private final String DELIM = XmpMetadata.getXmpTokenDelimiter();

    @Override
    @SuppressWarnings("unchecked")
    public String modify(String text, Collection<String> ignoreWords) {
        StringTokenizer strToken = new StringTokenizer(text, DELIM);
        List<String> subjects = new ArrayList<String>();
        while (strToken.hasMoreTokens()) {
            String subjectName = strToken.nextToken();
            Collection parentSubjects =
                    DatabaseHierarchicalSubjects.INSTANCE.getParentNames(
                    subjectName);
            subjects.add(subjectName);
            if (!ignoreWords.contains(subjectName)) {
                subjects.addAll(getUniquePath(subjectName, parentSubjects));
            }
        }
        return toSubjectString(new HashSet<String>(subjects)); // make them unique
    }

    private String toSubjectString(Collection<String> collection) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String s : collection) {
            sb.append((index++ == 0
                    ? ""
                    : DELIM) + s);
        }
        return sb.toString();
    }

    private Collection<String> getUniquePath(
            String subjectName, Collection<Collection<String>> parentSubjects) {
        List<String> subjects = new ArrayList<String>();
        if (parentSubjects.size() <= 1) {
            addToSubjects(subjects, parentSubjects);
        } else {
            PathSelectionDialog dlg = new PathSelectionDialog(parentSubjects);
            dlg.setInfoMessage(Bundle.getString(
                    "TextModifierHierarchicalKeywords.Error.MultiplePaths",
                    subjectName));
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                addToSubjects(subjects, dlg.getSelPaths());
            }
        }
        return subjects;
    }

    private void addToSubjects(Collection<String> subjects,
            Collection<Collection<String>> parentSubjects) {
        for (Collection<String> collection : parentSubjects) {
            subjects.addAll(collection);
        }
    }
}
